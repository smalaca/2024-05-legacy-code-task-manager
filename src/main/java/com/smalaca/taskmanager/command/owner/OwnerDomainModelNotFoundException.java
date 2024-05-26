package com.smalaca.taskmanager.command.owner;

public class OwnerDomainModelNotFoundException extends RuntimeException {
    private final Long ownerId;

    public OwnerDomainModelNotFoundException(Long ownerId) {
        this.ownerId = ownerId;
    }
}
