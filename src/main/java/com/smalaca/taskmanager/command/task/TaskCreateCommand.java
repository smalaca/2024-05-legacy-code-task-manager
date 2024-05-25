package com.smalaca.taskmanager.command.task;

import com.smalaca.taskamanager.dto.TaskDto;
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

    Long process(TaskDto dto) {
        Task t = new Task();
        TaskDomainModel taskDomainModel = new TaskDomainModel(t);
        t.setTitle(dto.getTitle());
        t.setDescription(dto.getDescription());
        t.setStatus(ToDoItemStatus.valueOf(dto.getStatus()));

        if (dto.getOwnerId() != null) {
            Optional<OwnerDomainModel> found = ownerDomainModelRepository.findById(dto.getOwnerId());

            if (found.isEmpty()) {
                throw new OwnerDomainModelNotFoundException(dto.getOwnerId());
            } else {
                taskDomainModel.setOwner(found.get());
            }
        }

        if (dto.getStoryId() != null) {
            Story str;
            if (!storyRepository.existsById(dto.getStoryId())) {
                throw new StoryDomainModelNotFoundException(dto.getStoryId());
            }

            str = storyRepository.findById(dto.getStoryId()).get();

            t.setStory(str);
            str.addTask(t);
            storyRepository.save(str);
        }

        return taskDomainModelRepository.create(taskDomainModel);
    }
}
