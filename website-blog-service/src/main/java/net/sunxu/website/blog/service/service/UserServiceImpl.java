package net.sunxu.website.blog.service.service;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.sunxu.website.blog.service.dto.CommentDTO;
import net.sunxu.website.blog.service.dto.UserArticleInfoDTO;
import net.sunxu.website.blog.service.dto.UserInfoDTO;
import net.sunxu.website.blog.service.repo.redis.ArticleInfoRedisRepo;
import net.sunxu.website.user.dto.UserDTO;
import net.sunxu.website.user.feignclient.UserFeignClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    @Lazy
    private CommentService commentService;

    @Autowired
    private ArticleInfoRedisRepo articleInfoRedisRepo;

    @Autowired
    private UserFeignClient userFeignClient;

    @Override
    public UserArticleInfoDTO getUserArticleInfo(Long articleId, Long userId) {
        UserArticleInfoDTO dto = new UserArticleInfoDTO();
        dto.setLiked(articleInfoRedisRepo.isArticleLiked(articleId, userId));
        dto.setShare(articleInfoRedisRepo.isArticleShared(articleId, userId));
        dto.setCommentIds(commentService.listCommentsByArticleAndUser(articleId, userId)
                .stream().map(CommentDTO::getId).collect(Collectors.toList()));
        return dto;
    }

    @Override
    public UserInfoDTO getUserInfo(Long userId) {
        var userInfo = userFeignClient.getUserInfo(userId);
        return convertToDTO(userInfo);
    }

    private UserInfoDTO convertToDTO(UserDTO userDTO) {
        UserInfoDTO res = new UserInfoDTO();
        BeanUtils.copyProperties(userDTO, res);
        return res;
    }

    @Override
    public Map<Long, UserInfoDTO> getUserInfos(Set<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return userFeignClient.getBatchUserInfo(userIds.stream().collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(UserDTO::getId, this::convertToDTO));
    }

}
