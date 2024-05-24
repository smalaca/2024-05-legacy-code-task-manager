package com.smalaca.taskmanager.command.task;

import com.smalaca.taskamanager.model.entities.User;

import static com.smalaca.taskmanager.command.task.OwnerDomainModel.Builder.owner;

public class UserDomainModel {
    private final User user;

    public UserDomainModel(User user) {
        this.user = user;
    }

    OwnerDomainModel convertToOwner() {
        OwnerDomainModel.Builder builder = owner(user.getUserName().getFirstName(), user.getUserName().getLastName());

        if (user.getPhoneNumber() != null) {
            builder.withPhoneNumber(user.getPhoneNumber().getNumber(), user.getPhoneNumber().getPrefix());
        }

        if (user.getEmailAddress() != null) {
            builder.withEmailAddress(user.getEmailAddress().getEmailAddress());
        }

        return builder.build();
    }
}
