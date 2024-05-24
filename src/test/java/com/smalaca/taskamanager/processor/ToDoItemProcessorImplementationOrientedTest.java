package com.smalaca.taskamanager.processor;

import com.google.common.collect.ImmutableList;
import com.smalaca.taskamanager.events.EpicReadyToPrioritize;
import com.smalaca.taskamanager.model.entities.Epic;
import com.smalaca.taskamanager.model.entities.ProductOwner;
import com.smalaca.taskamanager.model.entities.Project;
import com.smalaca.taskamanager.model.entities.Sprint;
import com.smalaca.taskamanager.model.entities.Story;
import com.smalaca.taskamanager.model.entities.Task;
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

    private final StoryService storyService = mock(StoryService.class);
    private final EventsRegistry eventsRegistry = mock(EventsRegistry.class);
    private final ProjectBacklogService projectBacklogService = mock(ProjectBacklogService.class);
    private final CommunicationService communicationService = mock(CommunicationService.class);
    private final SprintBacklogService sprintBacklogService = mock(SprintBacklogService.class);
    private final ToDoItemProcessor toDoItemProcessor = new ToDoItemProcessor(
            storyService,
            eventsRegistry,
            projectBacklogService,
            communicationService,
            sprintBacklogService);

    @Test
    void shouldProcessToDoItemDefinedWhenToDoItemIsStoryAndTasksAreEmpty() {
        Project project = mock(Project.class);
        Story story = mock(Story.class);

        given(story.getStatus()).willReturn(DEFINED);
        given(story.getProject()).willReturn(project);

        toDoItemProcessor.processFor(story);

        InOrder inOrder = inOrder(
                storyService,
                eventsRegistry,
                projectBacklogService,
                communicationService,
                sprintBacklogService,
                story, project);

        inOrder.verify(story).getStatus();
        inOrder.verify(story).getTasks();
        inOrder.verify(story).getProject();
        inOrder.verify(projectBacklogService).moveToReadyForDevelopment(story,project);


        verifyNoMoreInteractions(
                storyService,
                eventsRegistry,
                projectBacklogService,
                communicationService,
                sprintBacklogService,

                story, project);
    }

    @Test
    void shouldProcessToDoItemDefinedWhenToDoItemIsTask() {
        Task task = mock(Task.class);
        Sprint sprint = mock(Sprint.class);

        given(task.getStatus()).willReturn(DEFINED);
        given(task.getCurrentSprint()).willReturn(sprint);

        toDoItemProcessor.processFor(task);

        InOrder inOrder = inOrder(
                storyService,
                eventsRegistry,
                projectBacklogService,
                communicationService,
                sprintBacklogService,
                task, sprint);

        inOrder.verify(task).getStatus();
        inOrder.verify(task).getCurrentSprint();
        inOrder.verify(sprintBacklogService).moveToReadyForDevelopment(task,sprint);


        verifyNoMoreInteractions(
                storyService,
                eventsRegistry,
                projectBacklogService,
                communicationService,
                sprintBacklogService,

                task, sprint);
    }

    @Test
    void shouldProcessToDoItemDefinedWhenToDoItemIsStoryAndTasksAreNotEmptyAndIsAssigned() {
        Story story = mock(Story.class);
        Task task = mock(Task.class);

        given(story.getStatus()).willReturn(DEFINED);
        given(story.getTasks()).willReturn(ImmutableList.of(task));
        given(story.isAssigned()).willReturn(true);

        toDoItemProcessor.processFor(story);

        InOrder inOrder = inOrder(
                storyService,
                eventsRegistry,
                projectBacklogService,
                communicationService,
                sprintBacklogService,
                story, task);

        inOrder.verify(story).getStatus();
        inOrder.verify(story).getTasks();
        inOrder.verify(story).isAssigned();


        verifyNoMoreInteractions(
                storyService,
                eventsRegistry,
                projectBacklogService,
                communicationService,
                sprintBacklogService,

                story, task);
    }

    @Test
    void shouldProcessToDoItemDefinedWhenToDoItemIsStoryAndTasksAreNotEmptyAndIsNotAssigned() {
        Project project = mock(Project.class);
        Story story = mock(Story.class);
        Task task = mock(Task.class);

        given(story.getStatus()).willReturn(DEFINED);
        given(story.getTasks()).willReturn(ImmutableList.of(task));
        given(story.isAssigned()).willReturn(false);
        given(story.getProject()).willReturn(project);

        toDoItemProcessor.processFor(story);

        InOrder inOrder = inOrder(
                storyService,
                eventsRegistry,
                projectBacklogService,
                communicationService,
                sprintBacklogService,
                story, project, task);

        inOrder.verify(story).getStatus();
        inOrder.verify(story).getTasks();
        inOrder.verify(story).isAssigned();
        inOrder.verify(story).getProject();
        inOrder.verify(communicationService).notifyTeamsAbout(story,project);


        verifyNoMoreInteractions(
                storyService,
                eventsRegistry,
                projectBacklogService,
                communicationService,
                sprintBacklogService,

                story, project, task);
    }

    @Test
    void shouldProcessToDoItemDefinedWhenToDoItemIsEpic() {
//        EventsRegistry.isTest();
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