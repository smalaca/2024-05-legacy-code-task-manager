package com.smalaca.taskmanager.command.task;

import com.smalaca.taskamanager.model.entities.Task;
import com.smalaca.taskamanager.repository.TaskRepository;

import java.util.Optional;

class TaskDeleteCommand {
    private final TaskDomainModelRepository taskRepository;

    TaskDeleteCommand(TaskDomainModelRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    boolean process(long id) {
        return taskRepository.delete(id);
    }
}
