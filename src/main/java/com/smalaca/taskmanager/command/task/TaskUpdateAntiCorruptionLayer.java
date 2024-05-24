package com.smalaca.taskmanager.command.task;

import java.util.Optional;

public interface TaskUpdateAntiCorruptionLayer {
    void processTask(Long id);

    boolean existsById(Long ownerId);

    UserDomainModel findById(Long ownerId);

    Optional<UserDomainModel> findUserById(Long ownerId);
}
