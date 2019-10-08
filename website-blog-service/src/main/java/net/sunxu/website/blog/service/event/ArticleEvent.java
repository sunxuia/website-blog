package net.sunxu.website.blog.service.event;

import lombok.Getter;
import net.sunxu.website.blog.service.entity.jpa.ArticleInfo;

public class ArticleEvent extends AbstractEvent {

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public ArticleEvent(Object source, Type type, ArticleInfo article) {
        super(source);
        this.type = type;
        this.article = article;
    }

    @Getter
    private Type type;

    @Getter
    private ArticleInfo article;

    public enum Type {
        CREATED,
        UPDATED,
        VIEWED,
        SHARED,
        DELETED,
    }
}
