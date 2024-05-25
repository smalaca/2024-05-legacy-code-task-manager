package com.smalaca.taskmanager.command.task;

import java.util.Optional;

public interface TaskUpdateAntiCorruptionLayer {
    void processTask(Long id);

    Optional<TaskDomainModel> findTaskById(Long taskId);

    void save(TaskDomainModel taskDomainModel);

    Optional<UserDomainModel> findUserById(Long ownerId);
}
