package com.smalaca.taskamanager.processor;

import com.smalaca.taskamanager.events.EpicReadyToPrioritize;
import com.smalaca.taskamanager.model.entities.Epic;
import com.smalaca.taskamanager.model.entities.ProductOwner;
import com.smalaca.taskamanager.model.entities.Project;
import com.smalaca.taskamanager.model.interfaces.ToDoItem;
import com.smalaca.taskamanager.registry.EventsRegistry;
import com.smalaca.taskamanager.service.CommunicationService;
import com.smalaca.taskamanager.service.ProjectBacklogService;
import com.smalaca.taskamanager.service.SprintBacklogService;
import com.smalaca.taskamanager.service.StoryService;
import org.junit.jupiter.api.Test;

import static com.smalaca.taskamanager.model.enums.ToDoItemStatus.DEFINED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class ToDoItemProcessorImplementationOrientedTest {

    @Test
    void shouldProcessToDoItemDefinedWhenToDoItemIsEpic() {
        StoryService storyService = mock(StoryService.class);
        EventsRegistry eventsRegistry = mock(EventsRegistry.class);
        ProjectBacklogService projectBacklogService = mock(ProjectBacklogService.class);
        CommunicationService communicationService = mock(CommunicationService.class);
        SprintBacklogService sprintBacklogService = mock(SprintBacklogService.class);
        ToDoItemProcessor toDoItemProcessor = new ToDoItemProcessor(
                storyService,
                eventsRegistry,
                projectBacklogService,
                communicationService,
                sprintBacklogService);
        ToDoItem epic = mock(Epic.class);
        given(epic.getStatus()).willReturn(DEFINED);
        Project project = mock(Project.class);
        given(epic.getProject()).willReturn(project);
        ProductOwner productOwner = mock(ProductOwner.class);
        given(project.getProductOwner()).willReturn(productOwner);

        toDoItemProcessor.processFor(epic);

        then(epic).should().getStatus();
        then(epic).should().getId();
        then(epic).should().getProject();
        then(project).should().getProductOwner();
        then(projectBacklogService).should().putOnTop((Epic) epic);
        then(eventsRegistry).should().publish(any(EpicReadyToPrioritize.class));
        then(communicationService).should().notify(epic, productOwner);
        verifyNoMoreInteractions(
                storyService,
                eventsRegistry,
                projectBacklogService,
                communicationService,
                sprintBacklogService,

                epic, project, productOwner);
    }
}