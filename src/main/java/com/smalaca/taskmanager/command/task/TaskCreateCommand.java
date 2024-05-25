package com.smalaca.taskmanager.command.task;

import com.smalaca.taskamanager.model.entities.Task;
import com.smalaca.taskamanager.model.enums.ToDoItemStatus;

import java.util.Optional;

class TaskCreateCommand {
    private final TaskDomainModelRepository taskDomainModelRepository;
    private final OwnerDomainModelRepository ownerDomainModelRepository;
    private final StoryDomainModelRepository storyDomainModelRepository;

    TaskCreateCommand(
            TaskDomainModelRepository taskDomainModelRepository, OwnerDomainModelRepository ownerDomainModelRepository,
            StoryDomainModelRepository storyDomainModelRepository) {
        this.taskDomainModelRepository = taskDomainModelRepository;
        this.ownerDomainModelRepository = ownerDomainModelRepository;
        this.storyDomainModelRepository = storyDomainModelRepository;
    }

    Long process(CreateTaskDto dto) {
        Task t = new Task();
        TaskDomainModel task = new TaskDomainModel(t);
        t.setTitle(dto.getTitle());
        t.setDescription(dto.getDescription());
        t.setStatus(ToDoItemStatus.valueOf(dto.getStatus()));

        if (dto.hasOwnerId()) {
            Optional<OwnerDomainModel> found = ownerDomainModelRepository.findById(dto.getOwnerId());

            if (found.isEmpty()) {
                throw new OwnerDomainModelNotFoundException(dto.getOwnerId());
            } else {
                task.setOwner(found.get());
            }
        }

        if (dto.hasStoryId()) {
            Optional<StoryDomainModel> found = storyDomainModelRepository.findById(dto.getStoryId());

            if (found.isEmpty()) {
                throw new StoryDomainModelNotFoundException(dto.getStoryId());
            }

            StoryDomainModel story = found.get();
            task.setStory(story);

            storyDomainModelRepository.save(story);
        }

        return taskDomainModelRepository.create(task);
    }
}
