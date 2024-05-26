package com.smalaca.taskmanager.command.watcher;

import java.util.Optional;

public interface WatcherDomainModelRepository {
    Optional<WatcherDomainModel> findById(Long watcherId);
}
