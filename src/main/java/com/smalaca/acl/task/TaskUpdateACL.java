package com.smalaca.acl.task;

import com.smalaca.taskamanager.model.entities.User;
import com.smalaca.taskamanager.repository.UserRepository;
import com.smalaca.taskamanager.service.ToDoItemService;
import com.smalaca.taskmanager.command.task.TaskUpdateAntiCorruptionLayer;
import com.smalaca.taskmanager.command.task.UserDomainModel;

import java.util.Optional;

public class TaskUpdateACL implements TaskUpdateAntiCorruptionLayer {
    private final ToDoItemService toDoItemService;
    private UserRepository userRepository;

    public TaskUpdateACL(ToDoItemService toDoItemService, UserRepository userRepository) {
        this.toDoItemService = toDoItemService;
        this.userRepository = userRepository;
    }

    @Override
    public void processTask(Long id) {
        toDoItemService.processTask(id);
    }

    @Override
    public boolean existsById(Long ownerId) {
        return userRepository.existsById(ownerId);
    }

    @Override
    public UserDomainModel findById(Long ownerId) {
        User user = userRepository.findById(ownerId).get();
        return new UserDomainModel(user);
    }

    @Override
    public Optional<UserDomainModel> findUserById(Long userId) {
        if (existsById(userId)) {
            return Optional.of(findById(userId));
        } else {
            return Optional.empty();
        }
    }
}
