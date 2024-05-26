package com.smalaca.taskmanager.command.task;

import com.smalaca.taskmanager.command.owner.OwnerDomainModel;
import com.smalaca.taskmanager.command.owner.OwnerReadModel;
import com.smalaca.taskmanager.command.watcher.WatcherDomainModel;
import com.smalaca.taskmanager.command.watcher.WatcherReadModel;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class TaskDomainModel {
    private Long taskId;
    private final String title;
    private String description;
    private String status;
    private OwnerDomainModel owner;
    private List<WatcherDomainModel> watchers = new ArrayList<>();
    private Long storyId;

    public TaskDomainModel(
            Long taskId, String title, String description, String status,
            OwnerDomainModel owner, List<WatcherDomainModel> watchers) {
        this(title, description, status);
        this.taskId = taskId;
        this.owner = owner;
        this.watchers = watchers;
    }

    public TaskDomainModel(String title, String description, String status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    Long getId() {
        return taskId;
    }

    void changeDescription(String description) {
        this.description = description;
    }

    boolean changeStatusIfNeeded(UpdateTaskDto command) {
        if (hasDifferentStatusThan(command)) {
            this.status = command.getStatus();
            return true;
        }

        return false;
    }

    private boolean hasDifferentStatusThan(UpdateTaskDto command) {
        return command.hasStatus() && !this.status.equals(command.getStatus());
    }

    boolean hasOwner() {
        return owner != null;
    }

    void updateOwner(UpdateTaskDto dto) {
        if (dto.hasOwnerPhoneNumber()) {
            owner.changePhoneNumber(dto.getOwnerPhonePrefix(), dto.getOwnerPhoneNumber());
        }

        if (dto.hasOwnerEmailAddress()) {
            owner.changeEmailAddress(dto.getOwnerEmailAddress());
        }
    }

    void addWatcher(WatcherDomainModel watcher) {
        watchers.add(watcher);
    }

    void setOwner(OwnerDomainModel owner) {
        this.owner = owner;
    }

    void setStoryId(Long storyId) {
        this.storyId = storyId;
    }

    public TaskReadModel asReadModel() {
        OwnerReadModel owner = hasOwner() ? this.owner.asReadModel() : null;
        List<WatcherReadModel> watchers = this.watchers.stream()
                .map(WatcherDomainModel::asReadModel)
                .collect(toList());
        return new TaskReadModel(taskId, storyId, title, description, status, owner, watchers);
    }
}
