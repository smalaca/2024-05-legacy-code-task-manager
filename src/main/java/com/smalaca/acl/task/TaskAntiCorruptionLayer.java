package com.smalaca.acl.task;

import com.smalaca.taskamanager.model.entities.Task;
import com.smalaca.taskamanager.model.entities.User;
import com.smalaca.taskamanager.repository.TaskRepository;
import com.smalaca.taskamanager.repository.UserRepository;
import com.smalaca.taskamanager.service.ToDoItemService;
import com.smalaca.taskmanager.command.task.OwnerDomainModel;
import com.smalaca.taskmanager.command.task.TaskDomainModel;
import com.smalaca.taskmanager.command.task.TaskDomainModelRepository;
import com.smalaca.taskmanager.command.task.TaskUpdateAntiCorruptionLayer;

import java.util.Optional;

import static com.smalaca.taskmanager.command.task.OwnerDomainModel.Builder.owner;

public class TaskAntiCorruptionLayer implements TaskUpdateAntiCorruptionLayer, TaskDomainModelRepository {
    private final ToDoItemService toDoItemService;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public TaskAntiCorruptionLayer(ToDoItemService toDoItemService, UserRepository userRepository, TaskRepository taskRepository) {
        this.toDoItemService = toDoItemService;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public void processTask(Long id) {
        toDoItemService.processTask(id);
    }

    @Override
    public Optional<TaskDomainModel> findById(Long taskId) {
        Optional<Task> found = taskRepository.findById(taskId);
        return found.map(TaskDomainModel::new);
    }

    @Override
    public void save(TaskDomainModel taskDomainModel) {
        taskRepository.save(taskDomainModel.asTask());
    }

    @Override
    public boolean delete(long id) {
        Optional<Task> found = taskRepository.findById(id);
        found.ifPresent(taskRepository::delete);

        return found.isPresent();
    }

    @Override
    public Optional<OwnerDomainModel> findOwnerById(Long userId) {
        Optional<User> found = userRepository.findById(userId);
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
