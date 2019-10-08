package net.sunxu.website.blog.service.controller;

import java.util.List;
import java.util.stream.Collectors;
import net.sunxu.website.blog.service.dto.ArticleBasicDTO;
import net.sunxu.website.blog.service.dto.UserInfoDTO;
import net.sunxu.website.blog.service.service.ArticleService;
import net.sunxu.website.blog.service.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ArticleService articleService;

    @GetMapping("/{id}/info")
    public UserInfoDTO getUserInfo(@PathVariable("id") Long userId) {
        return userService.getUserInfo(userId);
    }

    @GetMapping("/{id}/recent-article")
    public List<ArticleBasicDTO> getRecentArticle(@PathVariable("id") Long userId,
            @RequestParam(name = "count", defaultValue = "5") Integer count) {
        return articleService.listLatestArticleByUser(System.currentTimeMillis(), count, userId)
                .stream()
                .map(i -> {
                    ArticleBasicDTO dto = new ArticleBasicDTO();
                    BeanUtils.copyProperties(i, dto);
                    return dto;
                }).collect(Collectors.toList());
    }
}
