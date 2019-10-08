package net.sunxu.website.blog.service.dto;

import java.util.Set;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class ArticleResourceDTO extends ArticleDetailDTO {

    private Set<String> fileIds;

}
