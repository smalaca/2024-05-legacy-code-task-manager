package com.smalaca.taskmanager.command.task;

import java.util.Optional;

public interface OwnerDomainModelRepository {
    Optional<OwnerDomainModel> findById(Long ownerId);
}
