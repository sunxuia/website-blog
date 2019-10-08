package net.sunxu.website.blog.service.dto;

import java.util.Date;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ArticleBasicDTO {

    private Long id;

    private String title;

    private Date createTime;

    private Date editTime;

    private AuthorDTO creator;
}
