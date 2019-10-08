package net.sunxu.website.blog.service.dto;

import java.util.Set;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ArticleCreationDTO {

    private String title;

    private String contentType;

    private String content;

    private Set<String> files;

}
