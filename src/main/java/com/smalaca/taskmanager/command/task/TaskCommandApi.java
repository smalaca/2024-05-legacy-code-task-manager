package com.smalaca.taskmanager.command.task;

import com.smalaca.taskamanager.dto.WatcherDto;
import com.smalaca.taskamanager.exception.UserNotFoundException;
import com.smalaca.taskamanager.model.embedded.EmailAddress;
import com.smalaca.taskamanager.model.embedded.PhoneNumber;
import com.smalaca.taskamanager.model.embedded.Watcher;
import com.smalaca.taskamanager.model.entities.Task;
import com.smalaca.taskamanager.model.entities.User;
import com.smalaca.taskamanager.repository.TaskRepository;
import com.smalaca.taskamanager.repository.UserRepository;
import com.smalaca.taskmanager.command.owner.OwnerDomainModelRepository;
import com.smalaca.taskmanager.command.story.StoryDomainModelRepository;

import java.util.Optional;

public class TaskCommandApi {
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final TaskCreateCommand taskCreateCommand;
    private final TaskUpdateCommand taskUpdateCommand;
    private final TaskDeleteCommand taskDeleteCommand;
    private final TaskAddWatcherCommand taskAddWatcherCommand;

    public TaskCommandApi(
            UserRepository userRepository, TaskRepository taskRepository,
            StatusChangeService statusChangeService, TaskDomainModelRepository taskDomainModelRepository,
            OwnerDomainModelRepository ownerDomainModelRepository, StoryDomainModelRepository storyDomainModelRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        taskCreateCommand = new TaskCreateCommand(taskDomainModelRepository, ownerDomainModelRepository, storyDomainModelRepository);
        taskUpdateCommand = new TaskUpdateCommand(taskDomainModelRepository, ownerDomainModelRepository, statusChangeService);
        taskDeleteCommand = new TaskDeleteCommand(taskDomainModelRepository);
        taskAddWatcherCommand = new TaskAddWatcherCommand(taskDomainModelRepository);
    }

    public Long create(CreateTaskDto createTaskDto) {
        return taskCreateCommand.process(createTaskDto);
    }

    public CommandStatus update(UpdateTaskDto updateTaskDto) {
        return taskUpdateCommand.process(updateTaskDto);
    }

    public boolean delete(long id) {
        return taskDeleteCommand.process(id);
    }

    public void addWatcher(long id, WatcherDto dto) {
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

        taskRepository.save(entity1);
    }

    private Task findTaskBy(long id) {
        Optional<Task> found = taskRepository.findById(id);

        if (found.isEmpty()) {
            throw new TaskDomainModelDoesNotExistException(id);
        }

        return found.get();
    }

    private User findUserBy(Long id) {
        Optional<User> found = userRepository.findById(id);

        if (found.isEmpty()) {
            throw new UserNotFoundException();
        }

        return found.get();
    }
}
