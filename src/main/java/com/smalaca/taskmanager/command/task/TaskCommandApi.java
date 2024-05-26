package com.smalaca.taskmanager.command.task;

import com.smalaca.taskmanager.command.owner.OwnerDomainModelRepository;
import com.smalaca.taskmanager.command.watcher.WatcherDomainModelRepository;

public class TaskCommandApi {
    private final TaskCreateCommand taskCreateCommand;
    private final TaskUpdateCommand taskUpdateCommand;
    private final TaskDeleteCommand taskDeleteCommand;
    private final TaskAddWatcherCommand taskAddWatcherCommand;

    private TaskCommandApi(
            TaskCreateCommand taskCreateCommand, TaskUpdateCommand taskUpdateCommand,
            TaskDeleteCommand taskDeleteCommand, TaskAddWatcherCommand taskAddWatcherCommand) {
        this.taskCreateCommand = taskCreateCommand;
        this.taskUpdateCommand = taskUpdateCommand;
        this.taskDeleteCommand = taskDeleteCommand;
        this.taskAddWatcherCommand = taskAddWatcherCommand;
    }

    public static TaskCommandApi taskCommandApi(
            StatusChangeService statusChangeService, TaskDomainModelRepository taskRepository,
            OwnerDomainModelRepository ownerRepository, StoryDomainModelRepository storyRepository,
            WatcherDomainModelRepository watcherRepository) {
        return new TaskCommandApi(
                new TaskCreateCommand(taskRepository, ownerRepository, storyRepository),
                new TaskUpdateCommand(taskRepository, ownerRepository, statusChangeService),
                new TaskDeleteCommand(taskRepository),
                new TaskAddWatcherCommand(taskRepository, watcherRepository));
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

    public void addWatcher(AddTaskWatcherDto dto) {
        taskAddWatcherCommand.process(dto);
    }
}
