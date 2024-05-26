package com.smalaca.taskmanager.command.story;

import com.smalaca.taskamanager.model.entities.Story;

public class StoryDomainModel {
    private final Story story;

    public StoryDomainModel(Story story) {
        this.story = story;
    }

    public Long getId() {
        return story.getId();
    }

    public Story asStory() {
        return story;
    }
}
