package com.smalaca.taskamanager.registry;

import com.smalaca.taskamanager.events.EpicReadyToPrioritize;
import com.smalaca.taskamanager.events.StoryApprovedEvent;
import com.smalaca.taskamanager.events.StoryDoneEvent;
import com.smalaca.taskamanager.events.TaskApprovedEvent;
import com.smalaca.taskamanager.events.ToDoItemReleasedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class EventsRegistry {
    private static boolean test;
    private final ApplicationEventPublisher publisher;

    public EventsRegistry(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public static void publishStatic(EpicReadyToPrioritize event) {
        if (test) {
            // do nothing
        } else {
            // 100K
        }
    }

    static void isTest() {
        test = true;
    }

    public void publish(StoryDoneEvent event) {
        publisher.publishEvent(event);
    }

    public void publish(StoryApprovedEvent event) {
        publisher.publishEvent(event);
    }

    public void publish(TaskApprovedEvent event) {
        publisher.publishEvent(event);
    }

    public void publish(EpicReadyToPrioritize event) {
        publisher.publishEvent(event);
    }

    public void publish(ToDoItemReleasedEvent event) {
        publisher.publishEvent(event);
    }
}
