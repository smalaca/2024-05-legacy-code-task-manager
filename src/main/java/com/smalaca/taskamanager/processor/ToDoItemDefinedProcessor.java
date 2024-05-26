package com.smalaca.taskamanager.processor;

import com.smalaca.taskamanager.exception.UnsupportedToDoItemType;
import com.smalaca.taskamanager.model.entities.Epic;
import com.smalaca.taskamanager.model.entities.Story;
import com.smalaca.taskamanager.model.entities.Task;
import com.smalaca.taskamanager.model.interfaces.ToDoItem;
import com.smalaca.taskamanager.registry.EventsRegistry;
import com.smalaca.taskamanager.service.CommunicationService;
import com.smalaca.taskamanager.service.ProjectBacklogService;
import com.smalaca.taskamanager.service.SprintBacklogService;

class ToDoItemDefinedProcessor {
    private ProjectBacklogService projectBacklogService;
    private CommunicationService communicationService;
    private SprintBacklogService sprintBacklogService;
    private EventsRegistry eventsRegistry;

    ToDoItemDefinedProcessor(ProjectBacklogService projectBacklogService, CommunicationService communicationService, SprintBacklogService sprintBacklogService, EventsRegistry eventsRegistry) {
        this.projectBacklogService = projectBacklogService;
        this.communicationService = communicationService;
        this.sprintBacklogService = sprintBacklogService;
        this.eventsRegistry = eventsRegistry;
    }

    void extracted(ToDoItem toDoItem) {
        if (toDoItem instanceof Story) {
            new StoryDefined(projectBacklogService, communicationService).process((Story) toDoItem);
        } else if (toDoItem instanceof Task) {
            new TaskDefined(sprintBacklogService).process((Task) toDoItem);
        } else if (toDoItem instanceof Epic) {
            new EpicDefined(projectBacklogService, eventsRegistry, communicationService).process((Epic) toDoItem);
        } else {
            throw new UnsupportedToDoItemType();
        }
    }
}
