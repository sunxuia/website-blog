package net.sunxu.website.blog.service.dto;

import java.util.Set;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ArticleEditDTO {

    private Long id;

    private String content;

    private String contentType;

    private Long timeKey;

    private Set<String> fileIds;

}
