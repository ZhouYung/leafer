package org.ziwenxie.leafer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ziwenxie.leafer.db.ArticleTagMapper;
import org.ziwenxie.leafer.model.ArticleTag;

@Service("articleTagService")
public class ArticleTagServiceImpl implements IArticleTagService {

    private ArticleTagMapper articleTagMapper;

    private Logger logger;

    public String oneArticleKey = "cache-key-article-one-";

    public String allTagsKey = "cache-key-tag-all-";

    public String oneTagKey = "cache-key-tag-one-";

    public final String pageArticleKey = "cache-key-article-page-";

    @Autowired
    public ArticleTagServiceImpl(ArticleTagMapper articleTagMapper) {
        this.articleTagMapper = articleTagMapper;
        this.logger = LoggerFactory.getLogger(ArticleServiceImpl.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
        @CacheEvict(value = "cache-value-article-one", key = "#root.target.oneArticleKey + #articleTag.articleId"),
        @CacheEvict(value = "cache-value-tag-one", key = "#root.target.oneTagKey + #articleTag.tagId"),
        @CacheEvict(value = "cache-value-tag-all", key = "#root.target.allTagsKey + #username"),
        @CacheEvict(value = "cache-value-article-page", key = "#root.target.pageArticleKey + #username + #page")
    })
    public boolean insertOneArticleTag(ArticleTag articleTag, String username, int page) {
        articleTagMapper.insertOneArticleTag(articleTag);
        logger.info(username + " insert articleTag " + articleTag.toString() + " successfully");
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
        @CacheEvict(value = "cache-value-article-one", key = "#root.target.oneArticleKey + #articleTag.articleId"),
        @CacheEvict(value = "cache-value-tag-one", key = "#root.target.oneTagKey + #articleTag.tagId"),
        @CacheEvict(value = "cache-value-tag-all", key = "#root.target.allTagsKey + #username"),
        @CacheEvict(value = "cache-value-article-page", key = "#root.target.pageArticleKey + #username + #page")
    })
    public boolean deleteOneArticleTag(ArticleTag articleTag, String username, int page) {
        articleTagMapper.deleteOneArticleTag(articleTag);
        logger.info(username + " delete articleTag " + articleTag.toString() + " successfully");
        return true;
    }

}
