package com.smalaca.taskmanager.command.owner;

public class OwnerDomainModel {
    private final String firstName;
    private final String lastName;
    private final String phoneNumber;
    private final String phonePrefix;
    private final String emailAddress;

    private OwnerDomainModel(Builder builder) {
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.phoneNumber = builder.phoneNumber;
        this.phonePrefix = builder.phonePrefix;
        this.emailAddress = builder.emailAddress;
    }

    public OwnerReadModel asReadModel() {
        return new OwnerReadModel(firstName, lastName, phoneNumber, phonePrefix, emailAddress);
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

        public static Builder owner(String firstName, String lastName) {
            return new Builder(firstName, lastName);
        }

        public void withPhoneNumber(String phoneNumber, String phonePrefix) {
            this.phoneNumber = phoneNumber;
            this.phonePrefix = phonePrefix;
        }

        public void withEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
        }

        public OwnerDomainModel build() {
            return new OwnerDomainModel(this);
        }
    }
}
