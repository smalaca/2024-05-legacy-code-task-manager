package com.smalaca.taskmanager.command.task;

import com.smalaca.taskamanager.model.entities.Story;

public class StoryDomainModel {
    private final Story story;

    public StoryDomainModel(Story story) {
        this.story = story;
    }

    public Story asStory() {
        return story;
    }
}
