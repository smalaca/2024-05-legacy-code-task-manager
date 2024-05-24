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
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import static com.smalaca.taskamanager.model.enums.ToDoItemStatus.DEFINED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
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
        long epicId = 123;
        ToDoItem epic = mock(Epic.class);
        given(epic.getStatus()).willReturn(DEFINED);
        given(epic.getId()).willReturn(epicId);
        Project project = mock(Project.class);
        given(epic.getProject()).willReturn(project);
        ProductOwner productOwner = mock(ProductOwner.class);
        given(project.getProductOwner()).willReturn(productOwner);

        toDoItemProcessor.processFor(epic);

        InOrder inOrder = inOrder(
                storyService,
                eventsRegistry,
                projectBacklogService,
                communicationService,
                sprintBacklogService,
                epic, project, productOwner);

        inOrder.verify(epic).getStatus();
        inOrder.verify(projectBacklogService).putOnTop((Epic) epic);
        inOrder.verify(epic).getId();

        ArgumentCaptor<EpicReadyToPrioritize> captor = ArgumentCaptor.forClass(EpicReadyToPrioritize.class);
        inOrder.verify(eventsRegistry).publish(captor.capture());
        EpicReadyToPrioritize event = captor.getValue();
        assertThat(event).extracting("epicId").isEqualTo(epicId);

        inOrder.verify(epic).getProject();
        inOrder.verify(project).getProductOwner();
        inOrder.verify(communicationService).notify(epic, productOwner);
        verifyNoMoreInteractions(
                storyService,
                eventsRegistry,
                projectBacklogService,
                communicationService,
                sprintBacklogService,

                epic, project, productOwner);
    }
}