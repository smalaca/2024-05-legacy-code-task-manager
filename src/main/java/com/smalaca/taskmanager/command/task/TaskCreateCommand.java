package com.smalaca.taskmanager.command.task;

import com.smalaca.taskamanager.model.entities.Story;
import com.smalaca.taskamanager.model.entities.Task;
import com.smalaca.taskamanager.model.enums.ToDoItemStatus;
import com.smalaca.taskamanager.repository.StoryRepository;

import java.util.Optional;

class TaskCreateCommand {
    private final TaskDomainModelRepository taskDomainModelRepository;
    private final OwnerDomainModelRepository ownerDomainModelRepository;
    private final StoryRepository storyRepository;

    TaskCreateCommand(TaskDomainModelRepository taskDomainModelRepository, OwnerDomainModelRepository ownerDomainModelRepository, StoryRepository storyRepository) {
        this.taskDomainModelRepository = taskDomainModelRepository;
        this.ownerDomainModelRepository = ownerDomainModelRepository;
        this.storyRepository = storyRepository;
    }

    Long process(CreateTaskDto createTaskDto) {
        Task t = new Task();
        TaskDomainModel task = new TaskDomainModel(t);
        t.setTitle(createTaskDto.getTitle());
        t.setDescription(createTaskDto.getDescription());
        t.setStatus(ToDoItemStatus.valueOf(createTaskDto.getStatus()));

        if (createTaskDto.hasOwnerId()) {
            Optional<OwnerDomainModel> found = ownerDomainModelRepository.findById(createTaskDto.getOwnerId());

            if (found.isEmpty()) {
                throw new OwnerDomainModelNotFoundException(createTaskDto.getOwnerId());
            } else {
                task.setOwner(found.get());
            }
        }

        if (createTaskDto.hasStoryId()) {
            Optional<Story> foundStory = storyRepository.findById(createTaskDto.getStoryId());
            Optional<StoryDomainModel> found = foundStory.map(StoryDomainModel::new);

            if (found.isEmpty()) {
                throw new StoryDomainModelNotFoundException(createTaskDto.getStoryId());
            }

            StoryDomainModel story = found.get();
            task.setStory(story);

            storyRepository.save(story.asStory());
        }

        return taskDomainModelRepository.create(task);
    }
}
