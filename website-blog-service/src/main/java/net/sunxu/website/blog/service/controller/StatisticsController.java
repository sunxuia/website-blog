package net.sunxu.website.blog.service.controller;

import net.sunxu.website.blog.service.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/article/{id}")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @PostMapping("/like")
    @ResponseBody
    public void postLike(@PathVariable("id") Long articleId, @RequestParam("like") Boolean like) {
        statisticsService.likeArticle(articleId, like);
    }


    @GetMapping("/share")
    public String shareArticle(@PathVariable("id") Long id, @RequestParam("mediaType") String mediaType) {
        String redirect = statisticsService.shareArticle(id, mediaType);
        return "redirect:" + redirect;
    }
}
