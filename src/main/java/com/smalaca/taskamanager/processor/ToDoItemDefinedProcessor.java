package com.smalaca.taskamanager.processor;

import com.smalaca.taskamanager.events.EpicReadyToPrioritize;
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
            Story story = (Story) toDoItem;
            if (story.getTasks().isEmpty()) {
                projectBacklogService.moveToReadyForDevelopment(story, story.getProject());
            } else {
                if (!story.isAssigned()) {
                    communicationService.notifyTeamsAbout(story, story.getProject());
                }
            }
        } else {
            if (toDoItem instanceof Task) {
                Task task = (Task) toDoItem;
                sprintBacklogService.moveToReadyForDevelopment(task, task.getCurrentSprint());
            } else {
                if (toDoItem instanceof Epic) {
                    Epic epic = (Epic) toDoItem;
                    projectBacklogService.putOnTop(epic);
                    publishEpicReadyToPrioritizeEventFor(epic);
                    communicationService.notify(toDoItem, toDoItem.getProject().getProductOwner());
                } else {
                    throw new UnsupportedToDoItemType();
                }
            }
        }
    }

    @Deprecated
    void publishEpicReadyToPrioritizeEventFor(Epic epic) {
        EpicReadyToPrioritize event = new EpicReadyToPrioritize();
        event.setEpicId(epic.getId());
        eventsRegistry.publish(event);
//        EventsRegistry.publishStatic(event);
//        validateEpic();
//        Foo foo = eventsRegistry.publish(event);
//        foo.doSomething();
//        storyService.doSomething(foo);
    }
}
