package com.smalaca.taskmanager.command.task;

import java.util.Optional;

public interface StoryDomainModelRepository {
    Optional<StoryDomainModel> findById(Long storyId);

    void save(StoryDomainModel story);
}
