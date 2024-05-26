package com.smalaca.acl.watcher;

import com.smalaca.taskamanager.model.entities.User;
import com.smalaca.taskamanager.repository.UserRepository;
import com.smalaca.taskmanager.command.watcher.WatcherDomainModel;
import com.smalaca.taskmanager.command.watcher.WatcherDomainModelRepository;

import java.util.Optional;

import static com.smalaca.taskmanager.command.watcher.WatcherDomainModel.Builder.watcher;

public class WatcherDomainModelRepositoryACL implements WatcherDomainModelRepository {
    private final UserRepository userRepository;

    public WatcherDomainModelRepositoryACL(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<WatcherDomainModel> findById(Long watcherId) {
        Optional<User> found = userRepository.findById(watcherId);
        return found.map(this::asWatcher);
    }

    private WatcherDomainModel asWatcher(User user) {
        WatcherDomainModel.Builder builder = watcher(user.getUserName().getFirstName(), user.getUserName().getLastName());

        if (user.getPhoneNumber() != null) {
            builder.withPhoneNumber(user.getPhoneNumber().getNumber(), user.getPhoneNumber().getPrefix());
        }

        if (user.getEmailAddress() != null) {
            builder.withEmailAddress(user.getEmailAddress().getEmailAddress());
        }

        return builder.build();
    }
}
