package com.smalaca.taskmanager.command.task;

class TaskDeleteCommand {
    private final TaskDomainModelRepository taskRepository;

    TaskDeleteCommand(TaskDomainModelRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    boolean process(long id) {
        return taskRepository.delete(id);
    }
}
