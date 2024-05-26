package com.smalaca.taskmanager.command.watcher;

import com.smalaca.taskamanager.model.embedded.EmailAddress;
import com.smalaca.taskamanager.model.embedded.PhoneNumber;
import com.smalaca.taskamanager.model.embedded.Watcher;

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

    public Watcher toWatcher() {
        Watcher watcher = new Watcher();
        watcher.setLastName(lastName);
        watcher.setFirstName(firstName);
        if (emailAddress != null) {
            watcher.setEmailAddress(toEmailAddress());
        }
        if (phoneNumber != null) {
            watcher.setPhoneNumber(toPhoneNumber());
        }
        return watcher;
    }

    private PhoneNumber toPhoneNumber() {
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setNumber(this.phoneNumber);
        phoneNumber.setPrefix(this.phonePrefix);
        return phoneNumber;
    }

    private EmailAddress toEmailAddress() {
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.setEmailAddress(this.emailAddress);
        return emailAddress;
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
