package com.smalaca.taskmanager.command.task;

import com.smalaca.taskamanager.model.embedded.EmailAddress;
import com.smalaca.taskamanager.model.embedded.Owner;
import com.smalaca.taskamanager.model.embedded.PhoneNumber;
import com.smalaca.taskamanager.model.entities.Story;
import com.smalaca.taskamanager.model.entities.Task;
import com.smalaca.taskamanager.model.enums.ToDoItemStatus;
import com.smalaca.taskmanager.command.owner.OwnerDomainModel;
import com.smalaca.taskmanager.command.owner.OwnerReadModel;
import com.smalaca.taskmanager.command.story.StoryDomainModel;
import com.smalaca.taskmanager.command.watcher.WatcherDomainModel;

public class TaskDomainModel {
    private final Task task;

    public TaskDomainModel(Task task) {
        this.task = task;
    }

    public TaskDomainModel(String title, String description, String status) {
        task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(ToDoItemStatus.valueOf(status));
    }

    public Task asTask() {
        return task;
    }

    Long getId() {
        return task.getId();
    }

    void changeDescription(String description) {
        task.setDescription(description);
    }

    boolean changeStatusIfNeeded(UpdateTaskDto command) {
        if (command.hasStatus()) {
            ToDoItemStatus newStatus = ToDoItemStatus.valueOf(command.getStatus());
            if (newStatus != task.getStatus()) {
                task.setStatus(newStatus);
                return true;
            }
        }

        return false;
    }

    boolean hasOwner() {
        return task.getOwner() != null;
    }

    void updateOwner(UpdateTaskDto dto) {
        if (dto.hasOwnerPhoneNumber()) {
            PhoneNumber phoneNumber = new PhoneNumber();
            phoneNumber.setNumber(dto.getOwnerPhoneNumber());
            phoneNumber.setPrefix(dto.getOwnerPhonePrefix());
            task.getOwner().setPhoneNumber(phoneNumber);
        }

        if (dto.hasOwnerEmailAddress()) {
            EmailAddress emailAddress = new EmailAddress();
            emailAddress.setEmailAddress(dto.getOwnerEmailAddress());
            task.getOwner().setEmailAddress(emailAddress);
        }
    }

    void setOwner(OwnerDomainModel ownerDomainModel) {
        task.setOwner(asOwner(ownerDomainModel.asReadModel()));
    }

    public Owner asOwner(OwnerReadModel ownerReadModel) {
        Owner owner = new Owner();
        owner.setFirstName(ownerReadModel.getFirstName());
        owner.setLastName(ownerReadModel.getLastName());
        if (ownerReadModel.getEmailAddress() != null) {
            owner.setEmailAddress(asEmailAddress(ownerReadModel.getEmailAddress()));
        }
        if (ownerReadModel.getPhoneNumber() != null) {
            owner.setPhoneNumber(asPhoneNumber(ownerReadModel.getPhonePrefix(), ownerReadModel.getPhoneNumber()));
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

    void setStory(StoryDomainModel story) {
        Story legacyStory = story.asStory();
        task.setStory(legacyStory);
        legacyStory.addTask(task);
    }

    void addWatcher(WatcherDomainModel watcher) {
        task.addWatcher(watcher.toWatcher());
    }
}
