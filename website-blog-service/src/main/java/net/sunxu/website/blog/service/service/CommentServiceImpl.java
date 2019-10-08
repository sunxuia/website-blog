package net.sunxu.website.blog.service.service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import net.sunxu.website.blog.service.dto.AuthorDTO;
import net.sunxu.website.blog.service.dto.CommentCreationDTO;
import net.sunxu.website.blog.service.dto.CommentDTO;
import net.sunxu.website.blog.service.entity.jpa.CommentInfo;
import net.sunxu.website.blog.service.event.ArticleEvent;
import net.sunxu.website.blog.service.event.CommentEvent;
import net.sunxu.website.blog.service.event.CommentEvent.Type;
import net.sunxu.website.blog.service.event.CommentsEvent;
import net.sunxu.website.blog.service.repo.jpa.CommentInfoRepo;
import net.sunxu.website.config.security.authentication.SecurityHelpUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService, ApplicationEventPublisherAware {

    @Autowired
    private CommentInfoRepo commentInfoRepo;

    @Autowired
    private UserService userService;

    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public List<CommentDTO> listCommentsByArticle(Long articleId, Pageable page) {
        var list = commentInfoRepo.findAllByArticleIdOrderByLastUpdateTimeDesc(articleId, page);
        return convertToDTO(list);
    }

    private List<CommentDTO> convertToDTO(List<CommentInfo> infos) {
        if (infos == null || infos.isEmpty()) {
            return Collections.emptyList();
        }
        var creatorIds = infos.stream().map(CommentInfo::getCreatorId).collect(Collectors.toSet());
        var creators = userService.getUserInfos(creatorIds);
        return infos.stream().map(info -> {
            CommentDTO dto = new CommentDTO();
            BeanUtils.copyProperties(info, dto);
            var creator = creators.get(info.getCreatorId());
            if (creator != null) {
                AuthorDTO authorDTO = new AuthorDTO();
                BeanUtils.copyProperties(creator, authorDTO);
                dto.setCreator(authorDTO);
            }
            return dto;
        }).collect(Collectors.toList());

    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public CommentDTO createComment(Long articleId, CommentCreationDTO dto) {
        CommentInfo info = new CommentInfo();
        info.setArticleId(articleId);
        info.setContentType(dto.getContentType());
        info.setContent(dto.getContent());
        Date now = new Date();
        Long userId = SecurityHelpUtils.getCurrentUserId();
        info.setCreateTime(now);
        info.setCreatorId(userId);
        info.setLastUpdateTime(now);
        info.setLastUpdateUserId(userId);
        commentInfoRepo.save(info);

        var event = new CommentEvent(this);
        event.setEventUserId(userId);
        event.setComment(info);
        event.setType(Type.CREATED);
        this.applicationEventPublisher.publishEvent(event);

        return convertToDTO(List.of(info)).get(0);
    }

    @Override
    public List<CommentDTO> listCommentsByArticleAndUser(Long articleId, Long userId) {
        var list = commentInfoRepo.findAllByArticleIdAndCreatorIdOrderByLastUpdateTimeDesc(articleId, userId);
        return convertToDTO(list);
    }

    @Transactional(rollbackOn = Exception.class)
    @EventListener(ArticleEvent.class)
    public void articleEvent(ArticleEvent event) {
        Long articleId = event.getArticle().getId();
        if (event.getType() == ArticleEvent.Type.DELETED) {
            Set<Long> ids = commentInfoRepo.listIdsByArticleId(articleId);
            commentInfoRepo.deleteAllByArticleId(articleId);

            CommentsEvent commentsEvent = new CommentsEvent(this);
            commentsEvent.setEventUserId(event.getEventUserId());
            commentsEvent.setCommentIds(ids);
            commentsEvent.setType(CommentsEvent.Type.DELETED);
            this.applicationEventPublisher.publishEvent(commentsEvent);
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
