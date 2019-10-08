package net.sunxu.website.blog.service.event;

import lombok.Data;
import net.sunxu.website.blog.service.entity.jpa.ResourceInfo;
import org.springframework.context.ApplicationEvent;

@Data
public abstract class AbstractEvent<T extends ResourceInfo> extends ApplicationEvent {

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public AbstractEvent(Object source) {
        super(source);
    }

    private Long eventUserId;

}
