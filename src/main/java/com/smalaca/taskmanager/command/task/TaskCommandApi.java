package com.smalaca.taskmanager.command.task;

import com.smalaca.taskamanager.dto.WatcherDto;
import com.smalaca.taskmanager.command.owner.OwnerDomainModelRepository;
import com.smalaca.taskmanager.command.story.StoryDomainModelRepository;
import com.smalaca.taskmanager.command.watcher.WatcherDomainModelRepository;

public class TaskCommandApi {
    private final TaskCreateCommand taskCreateCommand;
    private final TaskUpdateCommand taskUpdateCommand;
    private final TaskDeleteCommand taskDeleteCommand;
    private final TaskAddWatcherCommand taskAddWatcherCommand;

    public TaskCommandApi(
            StatusChangeService statusChangeService, TaskDomainModelRepository taskRepository,
            OwnerDomainModelRepository ownerRepository, StoryDomainModelRepository storyRepository,
            WatcherDomainModelRepository watcherRepository) {
        taskCreateCommand = new TaskCreateCommand(taskRepository, ownerRepository, storyRepository);
        taskUpdateCommand = new TaskUpdateCommand(taskRepository, ownerRepository, statusChangeService);
        taskDeleteCommand = new TaskDeleteCommand(taskRepository);
        taskAddWatcherCommand = new TaskAddWatcherCommand(taskRepository, watcherRepository);
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
