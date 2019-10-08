package net.sunxu.website.blog.service.service;

import java.util.List;
import net.sunxu.website.blog.service.dto.CommentCreationDTO;
import net.sunxu.website.blog.service.dto.CommentDTO;
import org.springframework.data.domain.Pageable;

public interface CommentService {

    List<CommentDTO> listCommentsByArticle(Long articleId, Pageable page);

    CommentDTO createComment(Long articleId, CommentCreationDTO dto);

    List<CommentDTO> listCommentsByArticleAndUser(Long articleId, Long userId);
}
