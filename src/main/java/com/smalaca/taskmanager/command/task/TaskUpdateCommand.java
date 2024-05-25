package com.smalaca.taskmanager.command.task;

import java.util.Optional;

class TaskUpdateCommand {
    private final TaskUpdateAntiCorruptionLayer taskUpdateAntiCorruptionLayer;

    TaskUpdateCommand(TaskUpdateAntiCorruptionLayer taskUpdateAntiCorruptionLayer) {
        this.taskUpdateAntiCorruptionLayer = taskUpdateAntiCorruptionLayer;
    }

    UpdateStatus process(UpdateTaskDto dto) {
        Optional<TaskDomainModel> found = taskUpdateAntiCorruptionLayer.findTaskById(dto.getTaskId());

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

        taskUpdateAntiCorruptionLayer.save(taskDomainModel);

        return UpdateStatus.SUCCESS;
    }

}
