package net.sunxu.website.blog.service.service;

import java.util.Map;
import java.util.Set;
import net.sunxu.website.blog.service.dto.ArticleStatisticsDTO;

public interface StatisticsService {

    void likeArticle(Long articleId, boolean like);

    String shareArticle(Long articleId, String mediaType);

    ArticleStatisticsDTO getStatistics(Long articleId);

    Map<Long, ArticleStatisticsDTO> listStatistics(Set<Long> articleId);
}
