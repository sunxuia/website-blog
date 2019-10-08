package net.sunxu.website.blog.service.dto;

import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserArticleInfoDTO {

    private Boolean liked;

    private Map<String, Boolean> share;

    private List<Long> commentIds;
}
