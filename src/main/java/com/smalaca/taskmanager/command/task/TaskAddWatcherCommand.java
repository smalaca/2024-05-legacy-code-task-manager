package com.smalaca.taskmanager.command.task;

import com.smalaca.taskamanager.dto.WatcherDto;
import com.smalaca.taskamanager.exception.UserNotFoundException;
import com.smalaca.taskamanager.model.entities.User;
import com.smalaca.taskamanager.repository.UserRepository;
import com.smalaca.taskmanager.command.watcher.WatcherDomainModel;

import java.util.Optional;

import static com.smalaca.taskmanager.command.watcher.WatcherDomainModel.Builder.watcher;

class TaskAddWatcherCommand {
    private final TaskDomainModelRepository taskRepository;
    private final UserRepository userRepository;

    TaskAddWatcherCommand(TaskDomainModelRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    void process(long id, WatcherDto dto) {
        Optional<TaskDomainModel> found = taskRepository.findById(id);

        if (found.isEmpty()) {
            throw new TaskDomainModelDoesNotExistException(id);
        }

        update(found.get(), dto);
    }

    private void update(TaskDomainModel taskDomainModel, WatcherDto dto) {
        User entity2 = findUserBy(dto.getId());

        WatcherDomainModel.Builder watcher = watcher(entity2.getUserName().getFirstName(), entity2.getUserName().getLastName());

        if (entity2.getEmailAddress() != null) {
            watcher.withEmailAddress(entity2.getEmailAddress().getEmailAddress());
        }

        if (entity2.getPhoneNumber() != null) {
            watcher.withPhoneNumber(entity2.getPhoneNumber().getNumber(), entity2.getPhoneNumber().getPrefix());
        }

        taskDomainModel.addWatcher(watcher);

        taskRepository.update(taskDomainModel);
    }

    private User findUserBy(Long id) {
        Optional<User> found = userRepository.findById(id);

        if (found.isEmpty()) {
            throw new UserNotFoundException();
        }

        return found.get();
    }
}
