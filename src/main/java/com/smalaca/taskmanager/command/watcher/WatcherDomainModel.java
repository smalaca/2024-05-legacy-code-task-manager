package com.smalaca.taskmanager.command.watcher;

public class WatcherDomainModel {
    private final String firstName;
    private final String lastName;
    private final String phoneNumber;
    private final String phonePrefix;
    private final String emailAddress;

    private WatcherDomainModel(Builder builder) {
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.phoneNumber = builder.phoneNumber;
        this.phonePrefix = builder.phonePrefix;
        this.emailAddress = builder.emailAddress;
    }

    public WatcherReadModel asReadModel() {
        return new WatcherReadModel(firstName, lastName, phoneNumber, phonePrefix, emailAddress);
    }

    public static class Builder {
        private final String firstName;
        private final String lastName;
        private String phoneNumber;
        private String phonePrefix;
        private String emailAddress;

        private Builder(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public static Builder watcher(String firstName, String lastName) {
            return new Builder(firstName, lastName);
        }

        public void withPhoneNumber(String phoneNumber, String phonePrefix) {
            this.phoneNumber = phoneNumber;
            this.phonePrefix = phonePrefix;
        }

        public void withEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
        }

        public WatcherDomainModel build() {
            return new WatcherDomainModel(this);
        }
    }
}
