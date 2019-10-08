package net.sunxu.website.blog.service.entity.jpa;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@MappedSuperclass
public class ResourceInfo implements Serializable {

    private static final long serialVersionUID = -1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long creatorId;

    @Column
    private Date createTime;

    @Column
    private Long lastUpdateUserId;

    @Column
    private Date lastUpdateTime;

    public Long timeKey() {
        return lastUpdateTime == null ? 0L : lastUpdateTime.getTime();
    }
}
