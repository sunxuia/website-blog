package net.sunxu.website.blog.service.repo.jpa;

import java.util.List;
import java.util.Set;
import net.sunxu.website.blog.service.entity.jpa.CommentInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentInfoRepo extends ResourceInfoRepo<CommentInfo> {

    List<CommentInfo> findAllByArticleIdOrderByLastUpdateTimeDesc(Long articleId, Pageable page);

    int deleteAllByArticleId(Long articleId);

    @Query("select id from CommentInfo where articleId = ?1")
    Set<Long> listIdsByArticleId(Long articleId);

    List<CommentInfo> findAllByArticleIdAndCreatorIdOrderByLastUpdateTimeDesc(Long articleId, Long creatorId);
}
