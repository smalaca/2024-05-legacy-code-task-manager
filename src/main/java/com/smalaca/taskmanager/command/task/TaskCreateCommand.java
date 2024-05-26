package com.smalaca.taskmanager.command.task;

import com.smalaca.taskmanager.command.owner.OwnerDomainModel;
import com.smalaca.taskmanager.command.owner.OwnerDomainModelNotFoundException;
import com.smalaca.taskmanager.command.owner.OwnerDomainModelRepository;

import java.util.Optional;

import static com.smalaca.taskmanager.command.task.TaskDomainModel.Builder.taskDomainModel;

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
        TaskDomainModel.Builder task = taskDomainModel(dto.getTitle(), dto.getDescription(), dto.getStatus());

        if (dto.hasOwnerId()) {
            Optional<OwnerDomainModel> found = ownerDomainModelRepository.findById(dto.getOwnerId());

            if (found.isEmpty()) {
                throw new OwnerDomainModelNotFoundException(dto.getOwnerId());
            } else {
                task.withOwner(found.get());
            }
        }

        if (dto.hasStoryId()) {
            if (storyDomainModelRepository.existById(dto.getStoryId())) {
                task.withStory(dto.getStoryId());
            } else {
                throw new StoryDomainModelNotFoundException(dto.getStoryId());
            }
        }

        return taskDomainModelRepository.create(task.build());
    }
}
