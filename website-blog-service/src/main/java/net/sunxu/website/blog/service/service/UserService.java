package net.sunxu.website.blog.service.service;

import java.util.Map;
import java.util.Set;
import net.sunxu.website.blog.service.dto.UserArticleInfoDTO;
import net.sunxu.website.blog.service.dto.UserInfoDTO;

public interface UserService {

    UserArticleInfoDTO getUserArticleInfo(Long articleId, Long userId);

    UserInfoDTO getUserInfo(Long userId);

    Map<Long, UserInfoDTO> getUserInfos(Set<Long> userIds);
}
