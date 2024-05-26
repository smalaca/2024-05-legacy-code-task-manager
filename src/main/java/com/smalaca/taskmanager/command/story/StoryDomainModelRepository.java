package com.smalaca.taskmanager.command.story;

public interface StoryDomainModelRepository {
    boolean existById(Long storyId);
}
