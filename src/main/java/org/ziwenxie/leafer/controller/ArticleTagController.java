package org.ziwenxie.leafer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.ziwenxie.leafer.model.ArticleTag;
import org.ziwenxie.leafer.service.IArticleService;
import org.ziwenxie.leafer.service.IArticleTagService;

import java.security.Principal;

@Controller
public class ArticleTagController {

    private IArticleTagService articleTagService;

    private IArticleService articleService;

    @Autowired
    public ArticleTagController(IArticleTagService articleTagService, IArticleService articleService) {
        this.articleTagService = articleTagService;
        this.articleService = articleService;
    }

    @ResponseBody
    @PostMapping("/articleTag/new")
    public ArticleTag newArticleTag(@RequestBody ArticleTag articleTag, Principal principal) {
        int page = articleService.getArticlePage(principal.getName(), articleTag.getArticleId());
        articleTagService.insertOneArticleTag(articleTag, principal.getName(), page);

        return articleTag;
    }

    @ResponseBody
    @PostMapping("/articleTag/delete")
    public ArticleTag deleteArticleTag(@RequestBody ArticleTag articleTag, Principal principal) {
        int page = articleService.getArticlePage(principal.getName(), articleTag.getArticleId());
        articleTagService.deleteOneArticleTag(articleTag, principal.getName(), page);

        return articleTag;
    }

}
