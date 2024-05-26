package com.smalaca.taskamanager.processor;

import com.smalaca.taskamanager.model.entities.Task;
import com.smalaca.taskamanager.service.SprintBacklogService;

class TaskDefined {
    private final SprintBacklogService sprintBacklogService;

    TaskDefined(SprintBacklogService sprintBacklogService) {
        this.sprintBacklogService = sprintBacklogService;
    }

    void process(Task task) {
        sprintBacklogService.moveToReadyForDevelopment(task, task.getCurrentSprint());
    }
}
