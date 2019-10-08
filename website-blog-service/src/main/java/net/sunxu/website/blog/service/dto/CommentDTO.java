package net.sunxu.website.blog.service.dto;

import java.util.Date;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CommentDTO {

    private Long id;

    private String contentType;

    private String content;

    private Date createTime;

    private Date editTime;

    private AuthorDTO creator;

}
