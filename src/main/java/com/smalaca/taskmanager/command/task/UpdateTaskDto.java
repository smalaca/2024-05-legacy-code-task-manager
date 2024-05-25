package com.smalaca.taskmanager.command.task;

import lombok.Getter;

@Getter
public
class UpdateTaskDto {
    private final Long taskId;
    private final String status;
    private final String description;
    private final Long ownerId;
    private final String ownerPhoneNumber;
    private final String ownerPhonePrefix;
    private final String ownerEmailAddress;

    public UpdateTaskDto(
            Long taskId, String status, String description, Long ownerId,
            String ownerPhoneNumber, String ownerPhonePrefix, String ownerEmailAddress) {
        this.taskId = taskId;
        this.status = status;
        this.description = description;
        this.ownerId = ownerId;
        this.ownerPhoneNumber = ownerPhoneNumber;
        this.ownerPhonePrefix = ownerPhonePrefix;
        this.ownerEmailAddress = ownerEmailAddress;
    }

    boolean hasDescription() {
        return description != null;
    }

    boolean hasStatus() {
        return status != null;
    }

    boolean hasOwnerPhoneNumber() {
        return ownerPhoneNumber != null && ownerPhonePrefix != null;
    }

    boolean hasOwnerEmailAddress() {
        return ownerEmailAddress != null;
    }

    boolean hasOwnerId() {
        return ownerId != null;
    }
}
