package org.ziwenxie.leafer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ziwenxie.leafer.db.ArticleMapper;
import org.ziwenxie.leafer.model.Article;
import org.ziwenxie.leafer.util.IdWorker;

import java.util.Date;
import java.util.List;

@Service("articleService")
public class ArticleServiceImpl implements IArticleService {

    private ArticleMapper articleMapper;

    private IdWorker idWorker;

    private Logger logger;

    private CacheManager cacheManager;

    public final String oneArticleKey = "cache-key-article-one-";

    public final String pageArticleKey = "cache-key-article-page-";

    public String countArticleKey = "cache-key-article-count-";

    @Autowired
    public ArticleServiceImpl(ArticleMapper articleMapper, CacheManager cacheManager) {
        this.articleMapper = articleMapper;
        this.cacheManager = cacheManager;
        this.idWorker = new IdWorker(1);
        this.logger = LoggerFactory.getLogger(ArticleServiceImpl.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "cache-value-article-count", key = "#root.target.countArticleKey + #article.username")
    public long insertOneArticle(Article article) {
        long articleId = idWorker.nextId();
        article.setId(articleId);
        article.setCreatedTime(new Date());  // add the created time
        article.setModifiedTime(new Date());  // add the modified time

        articleMapper.insertOneArticle(article);

        deleteAllPagesCache(article.getUsername()); // delete all pages cache

        logger.info(article.getUsername() + " insert article " + article.getId() + " successfully");

        return articleId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
        @CacheEvict(value="cache-value-article-one", key = "#root.target.oneArticleKey + #articleId"),
        @CacheEvict(value="cache-value-article-count", key = "#root.target.countArticleKey + #username")
    })
    public boolean deleteOneArticleById(long articleId, String username) {
        articleMapper.deleteOneArticleById(articleId);
        deleteAllPagesCache(username); // delete all pages' cache
        logger.info(username + " delete article " + articleId + " successfully");
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
        @CacheEvict(value = "cache-value-article-one", key = "#root.target.oneArticleKey + #article.id"),
        @CacheEvict(value = "cache-value-article-page", key = "#root.target.pageArticleKey + #username + #page")
    })
    public boolean updateOneArticle(Article article, String username, int page) {
        // note: article object only contains id, title, body property
        article.setModifiedTime(new Date());
        articleMapper.updateOneArticle(article);
        logger.info(username + " update article " + article.getId() + " successfully");
        return true;
    }

    @Override
    @Cacheable(value = "cache-value-article-page#300#30", key = "#root.target.pageArticleKey + #username + #page")
    public List<Article> getArticlesOfOnePage(String username, int page) {
        List<Article> articles = articleMapper.getArticlesOfOnePage(username, (page - 1) * 10);
        for (Article article: articles) {
            if (article.getBody() != null && article.getBody().length() > 150) {
                article.setBody(article.getBody().substring(0, 149));
            }
        }
        return articles;
    }

    @Override
    @Cacheable(value = "cache-value-article-one#300#30", key = "#root.target.oneArticleKey + #id")
    public Article getOneArticleById(long id, String username) {
        return articleMapper.getOneArticleById(id);
    }

    @Override
    @Cacheable(value = "cache-value-article-count#300#30", key = "#root.target.countArticleKey + #username")
    public long getArticlesCount(String username) {
        return articleMapper.getArticlesCount(username);
    }

    @Override
    public int getArticlePage(String username, long articleId) {
        long articleOrder = articleMapper.getArticleOrder(username, articleId);  // start from zero

        int page;
        if (articleOrder < 0) {  // first article
            page = 1;
        } else {
            page = (int) (articleOrder / 10 + 1);
        }

        return page;
    }

    private void deleteAllPagesCache(String username) {
        long pages = articleMapper.getArticlesCount(username) / 10 + 1;
        for (int page = 1; page <= pages; page++) {
            cacheManager.getCache("cache-value-article-page").evict(pageArticleKey + username + page);
        }
    }

}
