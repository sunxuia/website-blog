package net.sunxu.website.blog.service.repo.es;

import java.util.List;
import net.sunxu.website.blog.service.entity.es.ArticleEs;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleEsRepo extends ElasticsearchRepository<ArticleEs, String> {

    ArticleEs findByArticleId(Long artileId);

    int deleteByArticleId(Long articleId);

    List<ArticleEs> findAllByUpdateTimeLessThanOrderByUpdateTimeDesc(Long updateTime, Pageable pageable);

    List<ArticleEs> findAllByUpdateTimeLessThanAndCreatorIdEqualsOrderByUpdateTimeDesc(
            Long updateTime, Long creatorId, Pageable pageable);

    List<ArticleEs> findAllByTextLikeOrderByUpdateTime(String text, Pageable pageable);

    long countByTextLike(String text);
}
