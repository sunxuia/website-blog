package net.sunxu.website.blog.service.dto;

import java.util.Date;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class ArticleDetailDTO {

    private Long id;

    private String title;

    private Date createTime;

    private Date editTime;

    private String contentType;

    private String content;

    private Long creatorId;

    private ArticleStatisticsDTO statistics;

    private Long timeKey;

}
