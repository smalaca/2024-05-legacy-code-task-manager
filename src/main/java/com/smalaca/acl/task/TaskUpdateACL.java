package com.smalaca.acl.task;

import com.smalaca.taskamanager.model.entities.Task;
import com.smalaca.taskamanager.model.entities.User;
import com.smalaca.taskamanager.repository.TaskRepository;
import com.smalaca.taskamanager.repository.UserRepository;
import com.smalaca.taskamanager.service.ToDoItemService;
import com.smalaca.taskmanager.command.task.TaskDomainModel;
import com.smalaca.taskmanager.command.task.TaskUpdateAntiCorruptionLayer;
import com.smalaca.taskmanager.command.task.UserDomainModel;

import java.util.Optional;

public class TaskUpdateACL implements TaskUpdateAntiCorruptionLayer {
    private final ToDoItemService toDoItemService;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public TaskUpdateACL(ToDoItemService toDoItemService, UserRepository userRepository, TaskRepository taskRepository) {
        this.toDoItemService = toDoItemService;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public void processTask(Long id) {
        toDoItemService.processTask(id);
    }

    @Override
    public Optional<TaskDomainModel> findTaskById(Long taskId) {
        Optional<Task> found = taskRepository.findById(taskId);
        return found.map(TaskDomainModel::new);
    }

    @Override
    public void save(TaskDomainModel taskDomainModel) {
        taskRepository.save(taskDomainModel.asTask());
    }

    @Override
    public Optional<UserDomainModel> findUserById(Long userId) {
        Optional<User> found = userRepository.findById(userId);
        return found.map(UserDomainModel::new);
    }
}
