package com.smalaca.taskmanager.command.task;

import com.smalaca.taskamanager.model.entities.Task;

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
}
