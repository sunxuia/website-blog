package net.sunxu.website.blog.service.repo.redis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sunxu.website.blog.service.dto.ArticleStatisticsDTO;
import net.sunxu.website.help.util.ObjectHelpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ArticleInfoRedisRepoImpl implements ArticleInfoRedisRepo {

    private static final String ARTICLE_PREFIX = "ARTICLE:";

    private static final String COMMENT_COUNT_PREFIX = ARTICLE_PREFIX + "COMMENT:COUNT:";

    private static final String SHARE_PREFIX = ARTICLE_PREFIX + "SHARE:";

    private static final String SHARE_COUNT_PREFIX = SHARE_PREFIX + "COUNT:";

    private static final String SHARE_MEDIA_PREFIX = SHARE_PREFIX + "MEDIA:";

    private static final String VIEW_COUNT_PREFIX = ARTICLE_PREFIX + "VIEW:COUNT:";

    private static final String LIKE_PREFIX = ARTICLE_PREFIX + "LIKE:";

    @Autowired
    private StringRedisTemplate redisTemplate;

    private String getKey(String prefix, Long articleId) {
        return prefix.endsWith(":") ? prefix + articleId : prefix + ":" + articleId;
    }

    @Override
    public ArticleStatisticsDTO getStatistics(Long articleId) {
        List<Object> results = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            StringRedisConnection con = (StringRedisConnection) connection;
            con.get(getKey(COMMENT_COUNT_PREFIX, articleId));
            con.get(getKey(SHARE_COUNT_PREFIX, articleId));
            con.get(getKey(VIEW_COUNT_PREFIX, articleId));
            con.sCard(getKey(LIKE_PREFIX, articleId));
            return null;
        });
        ArticleStatisticsDTO dto = new ArticleStatisticsDTO();
        dto.setCommentCount(Integer.parseInt(ObjectHelpUtils.nvl((String) results.get(0), "0")));
        dto.setShareCount(Integer.parseInt(ObjectHelpUtils.nvl((String) results.get(1), "0")));
        dto.setViewCount(Integer.parseInt(ObjectHelpUtils.nvl((String) results.get(2), "0")));
        dto.setLikeCount(ObjectHelpUtils.nvl((Long) results.get(3), 0L).intValue());
        return dto;
    }

    @Override
    public Map<Long, ArticleStatisticsDTO> getStatisticsBatch(Set<Long> articleIds) {
        Map<Long, ArticleStatisticsDTO> res = new HashMap<>(articleIds.size());
        List<Object> results = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            StringRedisConnection con = (StringRedisConnection) connection;
            for (Long articleId : articleIds) {
                con.get(getKey(COMMENT_COUNT_PREFIX, articleId));
                con.get(getKey(SHARE_COUNT_PREFIX, articleId));
                con.get(getKey(VIEW_COUNT_PREFIX, articleId));
                con.sCard(getKey(LIKE_PREFIX, articleId));
            }
            return null;
        });
        int i = 0;
        for (Long articleId : articleIds) {
            ArticleStatisticsDTO dto = new ArticleStatisticsDTO();
            dto.setCommentCount(Integer.parseInt(ObjectHelpUtils.nvl((String) results.get(i++), "0")));
            dto.setShareCount(Integer.parseInt(ObjectHelpUtils.nvl((String) results.get(i++), "0")));
            dto.setViewCount(Integer.parseInt(ObjectHelpUtils.nvl((String) results.get(i++), "0")));
            dto.setLikeCount(ObjectHelpUtils.nvl((Long) results.get(i++), 0L).intValue());
            res.put(articleId, dto);
        }
        return res;
    }

    @Override
    public void increaseArticleLike(Long articleId, Long userId) {
        redisTemplate.opsForSet().add(getKey(LIKE_PREFIX, articleId), userId.toString());
    }

    @Override
    public void decreaseArticleLike(Long articleId, Long userId) {
        redisTemplate.opsForSet().remove(getKey(LIKE_PREFIX, articleId), userId.toString());
    }

    @Override
    public void deleteArticleLike(Long articleId) {
        redisTemplate.delete(getKey(LIKE_PREFIX, articleId));
    }

    @Override
    public boolean isArticleLiked(Long articleId, Long userId) {
        var ret = redisTemplate.opsForSet().isMember(getKey(LIKE_PREFIX, articleId), userId.toString());
        return Boolean.TRUE.equals(ret);
    }

    @Override
    public void addArticleViewCount(Long articleId) {
        redisTemplate.opsForValue().increment(getKey(VIEW_COUNT_PREFIX, articleId));
    }

    @Override
    public void deleteArticleViewCount(Long articleId) {
        redisTemplate.delete(getKey(VIEW_COUNT_PREFIX, articleId));
    }

    @Override
    public void addArticleCommentCount(Long articleId) {
        redisTemplate.opsForValue().increment(getKey(COMMENT_COUNT_PREFIX, articleId));
    }

    @Override
    public void deleteArticleCommentCount(Long articleId) {
        redisTemplate.delete(getKey(COMMENT_COUNT_PREFIX, articleId));
    }

    @Override
    public void addArticleShareCount(Long articleId, String mediaType) {
        redisTemplate.opsForValue().increment(getKey(SHARE_COUNT_PREFIX, articleId));
    }

    @Override
    public void addArticleShareCount(Long articleId, String mediaType, Long userId) {
        redisTemplate.opsForValue().increment(getKey(SHARE_COUNT_PREFIX, articleId));

        redisTemplate.opsForSet().add(getKey(SHARE_MEDIA_PREFIX, articleId), mediaType);

        String shareKey = getKey(SHARE_PREFIX + ":" + mediaType, articleId);
        redisTemplate.opsForSet().add(shareKey, userId.toString());
    }

    @Override
    public void deleteArticleShareCount(Long articleId) {
        redisTemplate.delete(getKey(SHARE_COUNT_PREFIX, articleId));
    }

    @Override
    public Map<String, Boolean> isArticleShared(Long articleId, Long userId) {
        var mediaTypes = redisTemplate.opsForSet().members(getKey(SHARE_MEDIA_PREFIX, articleId));
        List<Object> results = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            StringRedisConnection con = (StringRedisConnection) connection;
            for (String mediaType : mediaTypes) {
                con.sCard(getKey(SHARE_PREFIX + ":" + mediaType, articleId));
            }
            return null;
        });
        Map<String, Boolean> res = new HashMap<>();
        int i = 0;
        for (String mediaName : mediaTypes) {
            res.put(mediaName, Boolean.TRUE.equals(results.get(i++)));
        }
        return res;
    }
}
