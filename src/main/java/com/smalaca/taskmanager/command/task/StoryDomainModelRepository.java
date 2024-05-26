package com.smalaca.taskmanager.command.task;

public interface StoryDomainModelRepository {
    boolean existById(Long storyId);
}
