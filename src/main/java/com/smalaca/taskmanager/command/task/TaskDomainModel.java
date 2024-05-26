package com.smalaca.taskmanager.command.task;

import com.smalaca.taskamanager.model.embedded.EmailAddress;
import com.smalaca.taskamanager.model.embedded.Owner;
import com.smalaca.taskamanager.model.embedded.PhoneNumber;
import com.smalaca.taskamanager.model.embedded.Watcher;
import com.smalaca.taskamanager.model.entities.Story;
import com.smalaca.taskamanager.model.entities.Task;
import com.smalaca.taskamanager.model.enums.ToDoItemStatus;
import com.smalaca.taskmanager.command.owner.OwnerDomainModel;
import com.smalaca.taskmanager.command.owner.OwnerReadModel;
import com.smalaca.taskmanager.command.story.StoryDomainModel;
import com.smalaca.taskmanager.command.watcher.WatcherDomainModel;
import com.smalaca.taskmanager.command.watcher.WatcherReadModel;

import static com.smalaca.taskmanager.command.owner.OwnerDomainModel.Builder.owner;

public class TaskDomainModel {
    private final Task task;
    private Long taskId;
    private final String title;
    private String description;
    private String status;
    private OwnerDomainModel owner;

    public TaskDomainModel(Task task) {
        this.task = task;
        this.taskId = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.status = task.getStatus().name();
        if (task.getOwner() != null) {
            owner = asOwner(task.getOwner());
        }
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

    public TaskDomainModel(String title, String description, String status) {
        this.title = title;
        this.description = description;
        this.status = status;
        task = new Task();
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
        task.addWatcher(asWatcher(watcher.asReadModel()));
    }

    void setOwner(OwnerDomainModel owner) {
        this.owner = owner;
    }

    void setStory(StoryDomainModel story) {
        Story legacyStory = story.asStory();
        task.setStory(legacyStory);
        legacyStory.addTask(task);
    }

    public Task asTask() {
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(ToDoItemStatus.valueOf(status));

        if (owner != null) {
            task.setOwner(asOwner(owner.asReadModel()));
        }

        return task;
    }

    private Watcher asWatcher(WatcherReadModel readModel) {
        Watcher watcher = new Watcher();
        watcher.setFirstName(readModel.getFirstName());
        watcher.setLastName(readModel.getLastName());
        if (readModel.getEmailAddress() != null) {
            watcher.setEmailAddress(asEmailAddress(readModel.getEmailAddress()));
        }
        if (readModel.getPhoneNumber() != null) {
            watcher.setPhoneNumber(asPhoneNumber(readModel.getPhonePrefix(), readModel.getPhoneNumber()));
        }
        return watcher;
    }

    public Owner asOwner(OwnerReadModel readModel) {
        Owner owner = new Owner();
        owner.setFirstName(readModel.getFirstName());
        owner.setLastName(readModel.getLastName());
        if (readModel.getEmailAddress() != null) {
            owner.setEmailAddress(asEmailAddress(readModel.getEmailAddress()));
        }
        if (readModel.getPhoneNumber() != null) {
            owner.setPhoneNumber(asPhoneNumber(readModel.getPhonePrefix(), readModel.getPhoneNumber()));
        }
        return owner;
    }

    private PhoneNumber asPhoneNumber(String prefix, String number) {
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setNumber(number);
        phoneNumber.setPrefix(prefix);
        return phoneNumber;
    }

    private EmailAddress asEmailAddress(String email) {
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.setEmailAddress(email);
        return emailAddress;
    }
}
