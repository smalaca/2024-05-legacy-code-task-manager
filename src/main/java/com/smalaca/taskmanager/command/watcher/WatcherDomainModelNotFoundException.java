package com.smalaca.taskmanager.command.watcher;

public class WatcherDomainModelNotFoundException extends RuntimeException {
    private final Long watcherId;

    public WatcherDomainModelNotFoundException(Long watcherId) {
        this.watcherId = watcherId;
    }
}
