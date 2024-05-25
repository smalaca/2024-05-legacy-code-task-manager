package com.smalaca.taskmanager.command.task;

import lombok.AccessLevel;
import lombok.Getter;

@Getter(AccessLevel.PACKAGE)
public class CreateTaskDto {
    private final String title;
    private final String description;
    private final String status;
    private final Long ownerId;
    private final Long storyId;

    public CreateTaskDto(String title, String description, String status, Long ownerId, Long storyId) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.ownerId = ownerId;
        this.storyId = storyId;
    }
}
