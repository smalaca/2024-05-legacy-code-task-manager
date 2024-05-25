package com.smalaca.taskmanager.command.task;

import java.util.Optional;

class TaskUpdateCommand {
    private final TaskDomainModelRepository taskDomainModelRepository;
    private final TaskUpdateAntiCorruptionLayer taskUpdateAntiCorruptionLayer;

    TaskUpdateCommand(TaskDomainModelRepository taskDomainModelRepository, TaskUpdateAntiCorruptionLayer taskUpdateAntiCorruptionLayer) {
        this.taskDomainModelRepository = taskDomainModelRepository;
        this.taskUpdateAntiCorruptionLayer = taskUpdateAntiCorruptionLayer;
    }

    UpdateStatus process(UpdateTaskDto dto) {
        Optional<TaskDomainModel> found = taskDomainModelRepository.findById(dto.getTaskId());

        if (found.isEmpty()) {
            return UpdateStatus.TASK_NOT_FOUND;
        } else {
            return update(found.get(), dto);
        }
    }

    private UpdateStatus update(TaskDomainModel taskDomainModel, UpdateTaskDto dto) {
        if (dto.hasDescription()) {
            taskDomainModel.changeDescription(dto.getDescription());
        }

        if (taskDomainModel.changeStatusIfNeeded(dto)) {
            taskUpdateAntiCorruptionLayer.processTask(taskDomainModel.getId());
        }

        if (taskDomainModel.hasOwner()) {
            taskDomainModel.updateOwner(dto);
        } else {
            if (dto.hasOwnerId()) {
                Optional<OwnerDomainModel> found = taskUpdateAntiCorruptionLayer.findOwnerById(dto.getOwnerId());

                if (found.isPresent()) {
                    taskDomainModel.setOwner(found.get());
                } else {
                    return UpdateStatus.USER_NOT_FOUND;
                }
            }
        }

        taskDomainModelRepository.save(taskDomainModel);

        return UpdateStatus.SUCCESS;
    }

}
