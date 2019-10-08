package net.sunxu.website.blog.service.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ArticleStatisticsDTO {

    private Integer likeCount;

    private Integer viewCount;

    private Integer shareCount;

    private Integer commentCount;
}
