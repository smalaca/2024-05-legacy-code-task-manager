package com.smalaca.taskmanager.command.task;

import com.smalaca.taskamanager.model.embedded.EmailAddress;
import com.smalaca.taskamanager.model.embedded.Owner;
import com.smalaca.taskamanager.model.embedded.PhoneNumber;

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

    Owner toOwner() {
        Owner owner = new Owner();
        owner.setLastName(lastName);
        owner.setFirstName(firstName);
        if (emailAddress != null) {
            owner.setEmailAddress(toEmailAddress());
        }
        if (phoneNumber != null) {
            owner.setPhoneNumber(toPhoneNumber());
        }
        return owner;
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
