package net.sunxu.website.blog.service.entity.jpa;

import java.util.Set;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Lob;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@Entity
public class ArticleInfo extends ResourceInfo {

    @Column
    private String title;

    @Column
    private String contentType;

    @Lob
    private String content;

    @ElementCollection
    private Set<String> fileIds;
}
