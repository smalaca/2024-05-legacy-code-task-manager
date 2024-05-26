package com.smalaca.acl.task;

import com.smalaca.taskamanager.model.entities.User;
import com.smalaca.taskamanager.repository.UserRepository;
import com.smalaca.taskmanager.command.owner.OwnerDomainModel;
import com.smalaca.taskmanager.command.owner.OwnerDomainModelRepository;

import java.util.Optional;

import static com.smalaca.taskmanager.command.owner.OwnerDomainModel.Builder.owner;

public class OwnerDomainModelRepositoryACL implements OwnerDomainModelRepository {
    private final UserRepository userRepository;

    public OwnerDomainModelRepositoryACL(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<OwnerDomainModel> findById(Long ownerId) {
        Optional<User> found = userRepository.findById(ownerId);
        return found.map(this::asOwner);
    }

    private OwnerDomainModel asOwner(User user) {
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
