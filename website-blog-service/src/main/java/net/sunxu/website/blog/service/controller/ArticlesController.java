package net.sunxu.website.blog.service.controller;

import java.util.List;
import java.util.stream.Collectors;
import net.sunxu.website.blog.service.dto.ArticleBasicDTO;
import net.sunxu.website.blog.service.dto.ArticleBreifDTO;
import net.sunxu.website.blog.service.service.ArticleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/articles")
public class ArticlesController {

    @Autowired
    private ArticleService articleService;

    @GetMapping("/latest/basic")
    public List<ArticleBasicDTO> getLatestArticlesBasic(@RequestParam("count") Integer count) {
        var list = articleService.listLatestArticle(System.currentTimeMillis(), count);
        return list.stream().map(i -> {
            ArticleBasicDTO dto = new ArticleBasicDTO();
            BeanUtils.copyProperties(i, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    @GetMapping("/latest")
    public List<ArticleBreifDTO> getLatestArticleBreif(@RequestParam("count") Integer count,
            @RequestParam(name = "lastTimeKey", required = false) Long lastTimeKey) {
        if (lastTimeKey == null) {
            lastTimeKey = System.currentTimeMillis();
        }
        return articleService.listLatestArticle(lastTimeKey, count);
    }

    @GetMapping("/search")
    public List<ArticleBreifDTO> getSearchResult(@RequestParam("text") String text,
            @RequestParam(name = "pageIndex", defaultValue = "1") Integer pageIndex,
            @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
        return articleService.search(text, pageable);
    }

    @GetMapping("/search/count")
    public Long getSearchResultCount(@RequestParam("text") String text) {
        return articleService.countSearch(text);
    }
}
