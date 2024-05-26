package com.smalaca.acl.story;

import com.smalaca.taskamanager.repository.StoryRepository;
import com.smalaca.taskmanager.command.task.StoryDomainModelRepository;

public class StoryDomainModelRepositoryACL implements StoryDomainModelRepository {
    private final StoryRepository storyRepository;

    public StoryDomainModelRepositoryACL(StoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    @Override
    public boolean existById(Long storyId) {
        return storyRepository.existsById(storyId);
    }
}
