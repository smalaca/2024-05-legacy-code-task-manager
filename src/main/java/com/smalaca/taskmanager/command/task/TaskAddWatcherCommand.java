package com.smalaca.taskmanager.command.task;

import com.smalaca.taskamanager.dto.WatcherDto;
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

    void process(long id, WatcherDto dto) {
        Optional<TaskDomainModel> found = taskRepository.findById(id);

        if (found.isEmpty()) {
            throw new TaskDomainModelDoesNotExistException(id);
        }

        update(found.get(), dto);
    }

    private void update(TaskDomainModel taskDomainModel, WatcherDto dto) {
        WatcherDomainModel watcher = findUserBy(dto.getId());
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
