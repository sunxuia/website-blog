package net.sunxu.website.blog.service.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class ArticleBreifDTO extends ArticleBasicDTO {

    private String text;

    private ArticleStatisticsDTO statistics;

}
