package org.ziwenxie.leafer.service;

import org.ziwenxie.leafer.model.Article;
import org.ziwenxie.leafer.model.Tag;

import java.util.List;

public interface IArticleService {

    /**
     * 往数据库中插入一篇文章
     * @param article
     * @return 文章id
     */
    long insertOneArticle(Article article);

    /**
     * 根据文章id删除一篇文章
     * @param id 文章id
     * @param username 文章用户名
     * @return
     */
    boolean deleteOneArticleById(long id, String username);

    /**
     * 更新文章
     * @param article
     * @param username 文章用户名
     * @param page 文章在哪一页
     * @return
     */
    boolean updateOneArticle(Article article, String username, int page);

    /**
     * 得到一页中的所有文章
     * @param username 文章用户名
     * @param page 文章在哪一页
     * @return
     */
    List<Article> getArticlesOfOnePage(String username, int page);

    /**
     * 根据id查询一篇文章
     * @param id 文章id
     * @param username 文章用户名
     * @return
     */
    Article getOneArticleById(long id, String username);

    /**
     * 得到一个用户的所有文章数量
     * @param username 用户名
     * @return 用户编写的文章数量
     */
    long getArticlesCount(String username);

    /**
     * 判断文章在哪一页
     * @param username 用户名
     * @param articleId 文章id
     * @return 文章页数
     */
    int getArticlePage(String username, long articleId);

}
