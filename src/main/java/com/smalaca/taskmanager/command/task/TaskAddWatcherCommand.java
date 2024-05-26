package com.smalaca.taskmanager.command.task;

class TaskAddWatcherCommand {
    private final TaskDomainModelRepository taskDomainModelRepository;

    TaskAddWatcherCommand(TaskDomainModelRepository taskDomainModelRepository) {
        this.taskDomainModelRepository = taskDomainModelRepository;
    }
}
