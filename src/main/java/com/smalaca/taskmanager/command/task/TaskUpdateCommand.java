package com.smalaca.taskmanager.command.task;

import java.util.Optional;

class TaskUpdateCommand {
    private final TaskDomainModelRepository taskDomainModelRepository;
    private final OwnerDomainModelRepository ownerDomainModelRepository;
    private final StatusChangeService statusChangeService;

    TaskUpdateCommand(TaskDomainModelRepository taskDomainModelRepository, OwnerDomainModelRepository ownerDomainModelRepository, StatusChangeService statusChangeService) {
        this.taskDomainModelRepository = taskDomainModelRepository;
        this.ownerDomainModelRepository = ownerDomainModelRepository;
        this.statusChangeService = statusChangeService;
    }

    CommandStatus process(UpdateTaskDto dto) {
        Optional<TaskDomainModel> found = taskDomainModelRepository.findById(dto.getTaskId());

        if (found.isEmpty()) {
            return CommandStatus.TASK_NOT_FOUND;
        } else {
            return update(found.get(), dto);
        }
    }

    private CommandStatus update(TaskDomainModel taskDomainModel, UpdateTaskDto dto) {
        if (dto.hasDescription()) {
            taskDomainModel.changeDescription(dto.getDescription());
        }

        if (taskDomainModel.changeStatusIfNeeded(dto)) {
            statusChangeService.processTask(taskDomainModel.getId());
        }

        if (taskDomainModel.hasOwner()) {
            taskDomainModel.updateOwner(dto);
        } else {
            if (dto.hasOwnerId()) {
                Optional<OwnerDomainModel> found = ownerDomainModelRepository.findById(dto.getOwnerId());

                if (found.isPresent()) {
                    taskDomainModel.setOwner(found.get());
                } else {
                    return CommandStatus.OWNER_NOT_FOUND;
                }
            }
        }

        taskDomainModelRepository.update(taskDomainModel);

        return CommandStatus.SUCCESS;
    }

}
