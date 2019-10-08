package net.sunxu.website.blog.service.service;

import java.util.List;
import net.sunxu.website.blog.service.dto.ArticleBreifDTO;
import net.sunxu.website.blog.service.dto.ArticleCreationDTO;
import net.sunxu.website.blog.service.dto.ArticleEditDTO;
import net.sunxu.website.blog.service.dto.ArticleResourceDTO;
import net.sunxu.website.blog.service.entity.jpa.ArticleInfo;
import org.springframework.data.domain.Pageable;

public interface ArticleService {

    ArticleResourceDTO viewArticle(Long id);

    ArticleInfo getArticle(Long articleId);

    Long createArticle(ArticleCreationDTO dto);

    void updateArticle(ArticleEditDTO dto);

    void deleteArticle(Long id);

    List<ArticleBreifDTO> listLatestArticle(long lastTimeKey, int count);

    List<ArticleBreifDTO> listLatestArticleByUser(long lastTimeKey, int count, Long userId);

    List<ArticleBreifDTO> search(String text, Pageable pageable);

    long countSearch(String text);
}
