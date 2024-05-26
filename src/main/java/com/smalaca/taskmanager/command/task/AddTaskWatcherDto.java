package com.smalaca.taskmanager.command.task;

import lombok.AccessLevel;
import lombok.Getter;

@Getter(AccessLevel.PACKAGE)
public class AddTaskWatcherDto {
    private final Long taskId;
    private final Long watcherId;

    public AddTaskWatcherDto(Long taskId, Long watcherId) {
        this.taskId = taskId;
        this.watcherId = watcherId;
    }
}
