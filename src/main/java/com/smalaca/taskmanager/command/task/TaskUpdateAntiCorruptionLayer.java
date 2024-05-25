package com.smalaca.taskmanager.command.task;

import java.util.Optional;

public interface TaskUpdateAntiCorruptionLayer {
    void processTask(Long id);

    Optional<OwnerDomainModel> findOwnerById(Long ownerId);
}
