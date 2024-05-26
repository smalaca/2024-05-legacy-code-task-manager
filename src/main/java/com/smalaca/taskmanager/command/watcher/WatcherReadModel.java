package com.smalaca.taskmanager.command.watcher;

import lombok.Getter;

@Getter
public class WatcherReadModel {
    private final String firstName;
    private final String lastName;
    private final String phoneNumber;
    private final String phonePrefix;
    private final String emailAddress;

    WatcherReadModel(String firstName, String lastName, String phoneNumber, String phonePrefix, String emailAddress) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.phonePrefix = phonePrefix;
        this.emailAddress = emailAddress;
    }
}
