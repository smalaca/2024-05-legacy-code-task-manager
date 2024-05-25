package com.smalaca.taskmanager.command.task;

public class OwnerDomainModelNotFoundException extends RuntimeException {
    private final Long ownerId;

    OwnerDomainModelNotFoundException(Long ownerId) {
        this.ownerId = ownerId;
    }
}
