package com.smalaca.taskmanager.command.task;

import com.smalaca.taskmanager.command.watcher.WatcherDomainModel;
import com.smalaca.taskmanager.command.watcher.WatcherDomainModelNotFoundException;
import com.smalaca.taskmanager.command.watcher.WatcherDomainModelRepository;

import java.util.Optional;

class TaskAddWatcherCommand {
    private final TaskDomainModelRepository taskRepository;
    private final WatcherDomainModelRepository watcherRepository;

    TaskAddWatcherCommand(TaskDomainModelRepository taskRepository, WatcherDomainModelRepository watcherRepository) {
        this.taskRepository = taskRepository;
        this.watcherRepository = watcherRepository;
    }

    void process(AddTaskWatcherDto dto) {
        Optional<TaskDomainModel> found = taskRepository.findById(dto.getTaskId());

        if (found.isEmpty()) {
            throw new TaskDomainModelDoesNotExistException(dto.getTaskId());
        }

        update(found.get(), dto);
    }

    private void update(TaskDomainModel taskDomainModel, AddTaskWatcherDto dto) {
        WatcherDomainModel watcher = findUserBy(dto.getWatcherId());
        taskDomainModel.addWatcher(watcher);

        taskRepository.update(taskDomainModel);
    }

    private WatcherDomainModel findUserBy(Long watcherId) {
        Optional<WatcherDomainModel> found = watcherRepository.findById(watcherId);

        if (found.isEmpty()) {
            throw new WatcherDomainModelNotFoundException(watcherId);
        }

        return found.get();
    }
}
