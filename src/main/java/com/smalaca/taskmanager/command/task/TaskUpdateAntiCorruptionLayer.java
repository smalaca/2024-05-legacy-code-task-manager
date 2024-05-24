package com.smalaca.taskmanager.command.task;

import com.smalaca.taskamanager.model.entities.User;

import java.util.Optional;

public interface TaskUpdateAntiCorruptionLayer {
    void processTask(Long id);

    boolean existsById(Long ownerId);

    Optional<User> findById(Long ownerId);
}
