package com.smalaca.taskmanager.command.task;

import com.smalaca.taskamanager.model.embedded.Owner;
import com.smalaca.taskamanager.model.embedded.Watcher;
import com.smalaca.taskamanager.model.entities.Task;
import com.smalaca.taskmanager.command.owner.OwnerDomainModel;
import com.smalaca.taskmanager.command.owner.OwnerReadModel;
import com.smalaca.taskmanager.command.watcher.WatcherDomainModel;
import com.smalaca.taskmanager.command.watcher.WatcherReadModel;

import java.util.ArrayList;
import java.util.List;

import static com.smalaca.taskmanager.command.owner.OwnerDomainModel.Builder.owner;
import static com.smalaca.taskmanager.command.watcher.WatcherDomainModel.Builder.watcher;
import static java.util.stream.Collectors.toList;

public class TaskDomainModel {
    private Long taskId;
    private final String title;
    private String description;
    private String status;
    private OwnerDomainModel owner;
    private final List<WatcherDomainModel> watchers = new ArrayList<>();
    private Long storyId;

    public TaskDomainModel(Task task) {
        this.taskId = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.status = task.getStatus().name();
        if (task.getOwner() != null) {
            owner = asOwner(task.getOwner());
        }
        task.getWatchers().forEach(watcher -> watchers.add(asWatcher(watcher)));
    }

    private OwnerDomainModel asOwner(Owner owner) {
        OwnerDomainModel.Builder builder = owner(owner.getFirstName(), owner.getLastName());

        if (owner.getPhoneNumber() != null) {
            builder.withPhoneNumber(owner.getPhoneNumber().getNumber(), owner.getPhoneNumber().getPrefix());
        }

        if (owner.getEmailAddress() != null) {
            builder.withEmailAddress(owner.getEmailAddress().getEmailAddress());
        }

        return builder.build();
    }

    private WatcherDomainModel asWatcher(Watcher watcher) {
        WatcherDomainModel.Builder builder = watcher(watcher.getFirstName(), watcher.getLastName());

        if (watcher.getPhoneNumber() != null) {
            builder.withPhoneNumber(watcher.getPhoneNumber().getNumber(), watcher.getPhoneNumber().getPrefix());
        }

        if (watcher.getEmailAddress() != null) {
            builder.withEmailAddress(watcher.getEmailAddress().getEmailAddress());
        }

        return builder.build();
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
