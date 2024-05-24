package com.smalaca.taskmanager.command.task;

class UpdateTaskDto {
    private final long id;
    private final String status;
    private final String description;

    UpdateTaskDto(long id, String status, String description) {
        this.id = id;
        this.status = status;
        this.description = description;
    }

    long getId() {
        return id;
    }

    boolean hasDescription() {
        return description != null;
    }

    String getDescription() {
        return description;
    }

    boolean hasStatus() {
        return status != null;
    }

    String getStatus() {
        return status;
    }
}
