package net.sunxu.website.blog.service.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CommentCreationDTO {

    private String contentType;

    private String content;

}
