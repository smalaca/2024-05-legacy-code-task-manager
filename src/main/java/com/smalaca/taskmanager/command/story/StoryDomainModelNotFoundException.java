package com.smalaca.taskmanager.command.story;

public class StoryDomainModelNotFoundException extends RuntimeException {
    private final Long storyId;

    public StoryDomainModelNotFoundException(Long storyId) {
        this.storyId = storyId;
    }
}
