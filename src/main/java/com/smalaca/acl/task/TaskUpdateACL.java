package com.smalaca.acl.task;

import com.smalaca.taskamanager.service.ToDoItemService;
import com.smalaca.taskmanager.command.task.TaskUpdateAntiCorruptionLayer;

public class TaskUpdateACL implements TaskUpdateAntiCorruptionLayer {
    private final ToDoItemService toDoItemService;

    public TaskUpdateACL(ToDoItemService toDoItemService) {
        this.toDoItemService = toDoItemService;
    }

    @Override
    public void processTask(Long id) {
        toDoItemService.processTask(id);
    }
}
