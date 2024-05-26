package com.smalaca.acl.task;

import com.smalaca.taskamanager.service.ToDoItemService;
import com.smalaca.taskmanager.command.task.StatusChangeService;

public class StatusChangeServiceACL implements StatusChangeService {
    private final ToDoItemService toDoItemService;

    public StatusChangeServiceACL(ToDoItemService toDoItemService) {
        this.toDoItemService = toDoItemService;
    }

    @Override
    public void processTask(Long id) {
        toDoItemService.processTask(id);
    }
}
