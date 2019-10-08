package net.sunxu.website.blog.service.entity.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@Entity
public class CommentInfo extends ResourceInfo {

    @Column
    private Long articleId;

    @Column
    private String contentType;

    @Lob
    private String content;

}
