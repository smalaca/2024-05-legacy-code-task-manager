package com.smalaca.taskmanager.command.task;

import com.smalaca.taskmanager.command.owner.OwnerDomainModel;
import com.smalaca.taskmanager.command.owner.OwnerDomainModelNotFoundException;
import com.smalaca.taskmanager.command.owner.OwnerDomainModelRepository;
import com.smalaca.taskmanager.command.story.StoryDomainModelNotFoundException;
import com.smalaca.taskmanager.command.story.StoryDomainModelRepository;

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
        TaskDomainModel task = new TaskDomainModel(dto.getTitle(), dto.getDescription(), dto.getStatus());

        if (dto.hasOwnerId()) {
            Optional<OwnerDomainModel> found = ownerDomainModelRepository.findById(dto.getOwnerId());

            if (found.isEmpty()) {
                throw new OwnerDomainModelNotFoundException(dto.getOwnerId());
            } else {
                task.setOwner(found.get());
            }
        }

        if (dto.hasStoryId()) {
            if (storyDomainModelRepository.existById(dto.getStoryId())) {
                task.setStoryId(dto.getStoryId());
            } else {
                throw new StoryDomainModelNotFoundException(dto.getStoryId());
            }

        }

        return taskDomainModelRepository.create(task);
    }
}
