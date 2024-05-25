package com.smalaca.acl;

import com.smalaca.taskamanager.model.entities.Story;
import com.smalaca.taskamanager.repository.StoryRepository;
import com.smalaca.taskmanager.command.task.StoryDomainModel;
import com.smalaca.taskmanager.command.task.StoryDomainModelRepository;

import java.util.Optional;

public class StoryDomainModelRepositoryACL implements StoryDomainModelRepository {
    private final StoryRepository storyRepository;

    public StoryDomainModelRepositoryACL(StoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    @Override
    public Optional<StoryDomainModel> findById(Long storyId) {
        Optional<Story> foundStory = storyRepository.findById(storyId);
        return foundStory.map(StoryDomainModel::new);
    }

    @Override
    public void save(StoryDomainModel story) {
        storyRepository.save(story.asStory());
    }
}
