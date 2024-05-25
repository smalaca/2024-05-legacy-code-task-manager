package com.smalaca.taskmanager.command.task;

import com.smalaca.taskamanager.model.embedded.EmailAddress;
import com.smalaca.taskamanager.model.embedded.PhoneNumber;
import com.smalaca.taskamanager.model.entities.Task;
import com.smalaca.taskamanager.model.enums.ToDoItemStatus;

public class TaskDomainModel {
    private final Task task;

    public TaskDomainModel(Task task) {
        this.task = task;
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
        task.setOwner(ownerDomainModel.toOwner());
    }
}
