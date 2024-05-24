package com.smalaca.taskmanager.command.task;

import com.smalaca.taskamanager.model.embedded.EmailAddress;
import com.smalaca.taskamanager.model.embedded.Owner;
import com.smalaca.taskamanager.model.embedded.PhoneNumber;
import com.smalaca.taskamanager.model.embedded.UserName;
import com.smalaca.taskamanager.model.entities.User;

public class UserDomainModel {
    private final User user;

    public UserDomainModel(User user) {

        this.user = user;
    }

    private PhoneNumber getPhoneNumber() {
        return user.getPhoneNumber();
    }

    private UserName getUserName() {
        return user.getUserName();
    }

    private EmailAddress getEmailAddress() {
        return user.getEmailAddress();
    }

    Owner convertToOwner() {
        Owner owner = new Owner();

        if (getPhoneNumber() != null) {
            PhoneNumber number = new PhoneNumber();
            number.setNumber(getPhoneNumber().getNumber());
            number.setPrefix(getPhoneNumber().getPrefix());
            owner.setPhoneNumber(number);
        }

        owner.setLastName(getUserName().getLastName());
        owner.setFirstName(getUserName().getFirstName());

        if (getEmailAddress() != null) {
            EmailAddress eAdd = new EmailAddress();
            eAdd.setEmailAddress(getEmailAddress().getEmailAddress());
            owner.setEmailAddress(eAdd);
        }
        return owner;
    }
}
