package com.smalaca.taskmanager.command.owner;

import java.util.Optional;

public interface OwnerDomainModelRepository {
    Optional<OwnerDomainModel> findById(Long ownerId);
}
