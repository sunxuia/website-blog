package net.sunxu.website.blog.service.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import net.sunxu.website.blog.service.dto.ArticleStatisticsDTO;
import net.sunxu.website.blog.service.event.ArticleEvent;
import net.sunxu.website.blog.service.event.CommentEvent;
import net.sunxu.website.blog.service.repo.redis.ArticleInfoRedisRepo;
import net.sunxu.website.config.security.authentication.SecurityHelpUtils;
import net.sunxu.website.help.webutil.RequestHelpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private ArticleInfoRedisRepo articleInfoRedisRepo;

    @Autowired
    private ArticleService articleService;

    @Override
    public void likeArticle(Long articleId, boolean like) {
        if (like) {
            articleInfoRedisRepo.increaseArticleLike(articleId, SecurityHelpUtils.getCurrentUserId());
        } else {
            articleInfoRedisRepo.decreaseArticleLike(articleId, SecurityHelpUtils.getCurrentUserId());
        }
    }

    @Override
    public String shareArticle(Long articleId, String mediaType) {
        Long userId = SecurityHelpUtils.getCurrentUserId();
        if (userId == null) {
            articleInfoRedisRepo.addArticleShareCount(articleId, mediaType);
        } else {
            articleInfoRedisRepo.addArticleShareCount(articleId, mediaType, userId);
        }

        var article = articleService.getArticle(articleId);
        String title = "", url = "";
        try {
            title = URLEncoder.encode("分享文章 \"" + article.getTitle() + "\"",
                    StandardCharsets.UTF_8.toString());
            url = RequestHelpUtils.getRequest().getRequestURL().toString();
            url = url.substring(0, url.lastIndexOf('/')) + "/view";
            url = URLEncoder.encode(url, StandardCharsets.UTF_8.toString());
        } catch (Exception err) {
            err.printStackTrace();
        }

        if ("qq".equalsIgnoreCase(mediaType)) {
            return "http://sns.qzone.qq.com/cgi-bin/qzshare/cgi_qzshare_onekey?"
                    + "url=" + url
                    + "title=" + article.getTitle();
        } else if ("weibo".equalsIgnoreCase(mediaType)) {
            return "http://service.weibo.com/share/share.php?"
                    + "url=" + url
                    + "title=" + article.getTitle();
        } else {
            // twitter
            return "https://twitter.com/share?"
                    + "&url=" + url
                    + "text=" + title;
        }
    }

    @Override
    public ArticleStatisticsDTO getStatistics(Long articleId) {
        return articleInfoRedisRepo.getStatistics(articleId);
    }

    @Override
    public Map<Long, ArticleStatisticsDTO> listStatistics(Set<Long> articleId) {
        return articleInfoRedisRepo.getStatisticsBatch(articleId);
    }

    @EventListener(ArticleEvent.class)
    public void onArticleEvent(ArticleEvent articleEvent) {
        Long articleId = articleEvent.getArticle().getId();
        switch (articleEvent.getType()) {
            case VIEWED:
                articleInfoRedisRepo.addArticleViewCount(articleId);
                break;
            case DELETED:
                articleInfoRedisRepo.deleteArticleCommentCount(articleId);
                articleInfoRedisRepo.deleteArticleLike(articleId);
                articleInfoRedisRepo.deleteArticleViewCount(articleId);
                articleInfoRedisRepo.deleteArticleShareCount(articleId);
                break;
            default:
                break;
        }
    }

    @EventListener(CommentEvent.class)
    public void onCommentEvent(CommentEvent commentEvent) {
        var commentId = commentEvent.getComment().getId();
        var articleId = commentEvent.getComment().getArticleId();
        var type = commentEvent.getType();
        switch (type) {
            case CREATED:
                articleInfoRedisRepo.addArticleCommentCount(articleId);
                break;
            default:
                break;
        }
    }
}
