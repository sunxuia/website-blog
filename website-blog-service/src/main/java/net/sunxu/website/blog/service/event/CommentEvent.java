package net.sunxu.website.blog.service.event;

import lombok.Data;
import net.sunxu.website.blog.service.entity.jpa.CommentInfo;

@Data
public class CommentEvent extends AbstractEvent {

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public CommentEvent(Object source) {
        super(source);
    }

    private CommentInfo comment;

    private Type type;

    public enum Type {
        CREATED,
    }
}
