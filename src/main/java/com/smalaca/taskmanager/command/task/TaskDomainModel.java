package com.smalaca.taskmanager.command.task;

import com.smalaca.taskamanager.model.entities.Task;
import com.smalaca.taskamanager.model.enums.ToDoItemStatus;

class TaskDomainModel {
    private final Task task;

    TaskDomainModel(Task task) {
        this.task = task;
    }

    Task asTask() {
        return task;
    }

    Long getId() {
        return task.getId();
    }

    void changeDescription(String description) {
        task.setDescription(description);
    }

    boolean changeStatusIfNeeded(UpdateTaskDto command) {
        if (command.hasStatus()) {
            ToDoItemStatus newStatus = ToDoItemStatus.valueOf(command.getStatus());
            if (newStatus != task.getStatus()) {
                task.setStatus(newStatus);
                return true;
            }
        }

        return false;
    }
}
