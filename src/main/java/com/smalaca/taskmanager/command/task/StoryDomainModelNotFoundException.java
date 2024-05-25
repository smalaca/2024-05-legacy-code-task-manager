package com.smalaca.taskmanager.command.task;

public class StoryDomainModelNotFoundException extends RuntimeException {
    private final Long storyId;

    StoryDomainModelNotFoundException(Long storyId) {
        this.storyId = storyId;
    }
}
