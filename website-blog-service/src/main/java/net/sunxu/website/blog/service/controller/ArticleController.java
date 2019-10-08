package net.sunxu.website.blog.service.controller;

import net.sunxu.website.blog.service.dto.ArticleCreationDTO;
import net.sunxu.website.blog.service.dto.ArticleDetailDTO;
import net.sunxu.website.blog.service.dto.ArticleEditDTO;
import net.sunxu.website.blog.service.dto.ArticleResourceDTO;
import net.sunxu.website.blog.service.dto.UserArticleInfoDTO;
import net.sunxu.website.blog.service.service.ArticleService;
import net.sunxu.website.blog.service.service.UserService;
import net.sunxu.website.config.security.authentication.SecurityHelpUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserService userService;

    @GetMapping("/{id}/detail")
    public ArticleDetailDTO getArticleDetail(@PathVariable("id") Long id) {
        var articleResource = articleService.viewArticle(id);
        ArticleDetailDTO dto = new ArticleDetailDTO();
        BeanUtils.copyProperties(articleResource, dto);
        return dto;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/aboutme")
    public UserArticleInfoDTO getAbountMe(@PathVariable("id") Long articleId) {
        Long userId = SecurityHelpUtils.getCurrentUserId();
        return userService.getUserArticleInfo(articleId, userId);
    }

    @GetMapping("/{id}/resource")
    public ArticleResourceDTO getArticleResource(@PathVariable("id") Long id) {
        return articleService.viewArticle(id);
    }

    @PostMapping("/resource")
    public ArticleResourceDTO postArticleResource(@RequestBody ArticleCreationDTO creationDTO) {
        long articleId = articleService.createArticle(creationDTO);
        return articleService.viewArticle(articleId);
    }

    @PutMapping("/{id}/resource")
    public ArticleResourceDTO putArticleResource(@RequestBody ArticleEditDTO editDTO) {
        articleService.updateArticle(editDTO);
        return articleService.viewArticle(editDTO.getId());
    }

    @DeleteMapping("/{id}/resource")
    public void deleteArticleResource(@PathVariable("id") Long id) {
        articleService.deleteArticle(id);
    }
}
