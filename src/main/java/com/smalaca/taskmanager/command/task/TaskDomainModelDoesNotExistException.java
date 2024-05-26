package com.smalaca.taskmanager.command.task;

public class TaskDomainModelDoesNotExistException extends RuntimeException {
    private final long taskId;

    TaskDomainModelDoesNotExistException(long taskId) {
        this.taskId = taskId;
    }
}
