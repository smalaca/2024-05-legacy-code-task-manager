package com.smalaca.taskmanager.command.task;

import com.smalaca.taskamanager.dto.WatcherDto;
import com.smalaca.taskamanager.repository.TaskRepository;
import com.smalaca.taskamanager.repository.UserRepository;
import com.smalaca.taskmanager.command.owner.OwnerDomainModelRepository;
import com.smalaca.taskmanager.command.story.StoryDomainModelRepository;

public class TaskCommandApi {
    private final TaskCreateCommand taskCreateCommand;
    private final TaskUpdateCommand taskUpdateCommand;
    private final TaskDeleteCommand taskDeleteCommand;
    private final TaskAddWatcherCommand taskAddWatcherCommand;

    public TaskCommandApi(
            UserRepository userRepository, TaskRepository taskRepository,
            StatusChangeService statusChangeService, TaskDomainModelRepository taskDomainModelRepository,
            OwnerDomainModelRepository ownerDomainModelRepository, StoryDomainModelRepository storyDomainModelRepository) {
        taskCreateCommand = new TaskCreateCommand(taskDomainModelRepository, ownerDomainModelRepository, storyDomainModelRepository);
        taskUpdateCommand = new TaskUpdateCommand(taskDomainModelRepository, ownerDomainModelRepository, statusChangeService);
        taskDeleteCommand = new TaskDeleteCommand(taskDomainModelRepository);
        taskAddWatcherCommand = new TaskAddWatcherCommand(taskDomainModelRepository, userRepository);
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
        taskAddWatcherCommand.process(id, dto);
    }
}
