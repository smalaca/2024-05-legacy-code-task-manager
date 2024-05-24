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
    public Optional<UserDomainModel> findUserById(Long userId) {
        Optional<User> found = userRepository.findById(userId);
        return found.map(UserDomainModel::new);
    }
}
