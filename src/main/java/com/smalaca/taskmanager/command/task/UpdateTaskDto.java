package com.smalaca.taskmanager.command.task;

class UpdateTaskDto {
    private final long id;
    private final String description;

    UpdateTaskDto(long id, String description) {
        this.id = id;
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
}
