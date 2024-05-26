package com.smalaca.taskamanager.processor;

import com.smalaca.taskamanager.model.entities.Story;
import com.smalaca.taskamanager.service.CommunicationService;
import com.smalaca.taskamanager.service.ProjectBacklogService;

class StoryDefined {
    private final ProjectBacklogService projectBacklogService;
    private final CommunicationService communicationService;

    StoryDefined(ProjectBacklogService projectBacklogService, CommunicationService communicationService) {
        this.projectBacklogService = projectBacklogService;
        this.communicationService = communicationService;
    }

    void process(Story story) {
        if (story.getTasks().isEmpty()) {
            projectBacklogService.moveToReadyForDevelopment(story, story.getProject());
        } else {
            if (!story.isAssigned()) {
                communicationService.notifyTeamsAbout(story, story.getProject());
            }
        }
    }
}
