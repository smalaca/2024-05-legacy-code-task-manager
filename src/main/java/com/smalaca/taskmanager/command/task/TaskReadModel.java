package com.smalaca.taskmanager.command.task;

import com.smalaca.taskmanager.command.owner.OwnerReadModel;
import com.smalaca.taskmanager.command.watcher.WatcherReadModel;
import lombok.Getter;

import java.util.List;

@Getter
public class TaskReadModel {
    private final Long taskId;
    private final Long storyId;
    private final String title;
    private final String description;
    private final String status;
    private final OwnerReadModel owner;
    private final List<WatcherReadModel> watchers;

    TaskReadModel(
            Long taskId, Long storyId, String title, String description, String status,
            OwnerReadModel owner, List<WatcherReadModel> watchers) {
        this.taskId = taskId;
        this.storyId = storyId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.owner = owner;
        this.watchers = watchers;
    }
}
