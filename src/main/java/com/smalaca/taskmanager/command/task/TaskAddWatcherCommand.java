package com.smalaca.taskmanager.command.task;

import com.smalaca.taskamanager.dto.WatcherDto;
import com.smalaca.taskamanager.exception.UserNotFoundException;
import com.smalaca.taskamanager.model.embedded.EmailAddress;
import com.smalaca.taskamanager.model.embedded.PhoneNumber;
import com.smalaca.taskamanager.model.embedded.Watcher;
import com.smalaca.taskamanager.model.entities.Task;
import com.smalaca.taskamanager.model.entities.User;
import com.smalaca.taskamanager.repository.UserRepository;

import java.util.Optional;

class TaskAddWatcherCommand {
    private final TaskDomainModelRepository taskRepository;
    private final UserRepository userRepository;

    TaskAddWatcherCommand(TaskDomainModelRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    void process(long id, WatcherDto dto) {
        Task entity1 = findTaskBy(id);

        User entity2 = findUserBy(dto.getId());
        Watcher entity3 = new Watcher();
        entity3.setFirstName(entity2.getUserName().getFirstName());
        entity3.setLastName(entity2.getUserName().getLastName());

        if (entity2.getEmailAddress() != null) {
            EmailAddress entity4 = new EmailAddress();
            entity4.setEmailAddress(entity2.getEmailAddress().getEmailAddress());
            entity3.setEmailAddress(entity4);
        }

        if (entity2.getPhoneNumber() != null) {
            PhoneNumber entity5 = new PhoneNumber();
            entity5.setNumber(entity2.getPhoneNumber().getNumber());
            entity5.setPrefix(entity2.getPhoneNumber().getPrefix());
            entity3.setPhoneNumber(entity5);
        }
        entity1.addWatcher(entity3);

        taskRepository.update(new TaskDomainModel(entity1));
    }

    private Task findTaskBy(long id) {
        Optional<TaskDomainModel> found = taskRepository.findById(id);

        if (found.isEmpty()) {
            throw new TaskDomainModelDoesNotExistException(id);
        }

        return found.get().asTask();
    }

    private User findUserBy(Long id) {
        Optional<User> found = userRepository.findById(id);

        if (found.isEmpty()) {
            throw new UserNotFoundException();
        }

        return found.get();
    }
}
