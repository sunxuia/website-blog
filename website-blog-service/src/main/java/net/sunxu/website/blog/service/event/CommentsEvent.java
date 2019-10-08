package net.sunxu.website.blog.service.event;

import java.util.Set;
import lombok.Data;

@Data
public class CommentsEvent extends AbstractEvent {

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public CommentsEvent(Object source) {
        super(source);
    }

    private Set<Long> commentIds;

    private Type type;

    public enum Type {
        DELETED
    }
}
