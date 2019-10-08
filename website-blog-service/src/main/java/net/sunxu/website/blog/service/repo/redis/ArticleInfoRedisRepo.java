package net.sunxu.website.blog.service.repo.redis;

import java.util.Map;
import java.util.Set;
import net.sunxu.website.blog.service.dto.ArticleStatisticsDTO;

public interface ArticleInfoRedisRepo {

    ArticleStatisticsDTO getStatistics(Long articleId);

    Map<Long, ArticleStatisticsDTO> getStatisticsBatch(Set<Long> articleIds);

    void increaseArticleLike(Long articleId, Long userId);

    void decreaseArticleLike(Long articleId, Long userId);

    void deleteArticleLike(Long articleId);

    boolean isArticleLiked(Long articleId, Long userId);


    void addArticleViewCount(Long articleId);

    void deleteArticleViewCount(Long articleId);


    void addArticleCommentCount(Long articleId);

    void deleteArticleCommentCount(Long articleId);


    void addArticleShareCount(Long articleId, String mediaType);

    void addArticleShareCount(Long articleId, String mediaType, Long userId);

    void deleteArticleShareCount(Long articleId);

    Map<String, Boolean> isArticleShared(Long articleId, Long userId);

}
