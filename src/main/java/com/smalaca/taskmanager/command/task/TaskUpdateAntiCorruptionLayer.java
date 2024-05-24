package com.smalaca.taskmanager.command.task;

public interface TaskUpdateAntiCorruptionLayer {
    void processTask(Long id);

    boolean existsById(Long ownerId);

    UserDomainModel findById(Long ownerId);
}
