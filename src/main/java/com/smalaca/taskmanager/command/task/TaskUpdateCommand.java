package com.smalaca.taskmanager.command.task;

import com.smalaca.taskamanager.dto.TaskDto;
import com.smalaca.taskamanager.model.entities.Task;
import com.smalaca.taskamanager.repository.TaskRepository;

import java.util.Optional;

import static com.smalaca.taskmanager.command.task.OwnerDomainModel.Builder.owner;

class TaskUpdateCommand {
    private final TaskRepository taskRepository;
    private final TaskUpdateAntiCorruptionLayer taskUpdateAntiCorruptionLayer;

    TaskUpdateCommand(TaskRepository taskRepository, TaskUpdateAntiCorruptionLayer taskUpdateAntiCorruptionLayer) {
        this.taskRepository = taskRepository;
        this.taskUpdateAntiCorruptionLayer = taskUpdateAntiCorruptionLayer;
    }

    UpdateStatus process(long id, TaskDto dto) {
        UpdateTaskDto command = new UpdateTaskDto(
                id,
                dto.getStatus(),
                dto.getDescription(),
                dto.getOwnerId(),
                dto.getOwnerPhoneNumberNumber(),
                dto.getOwnerPhoneNumberPrefix(),
                dto.getOwnerEmailAddress());
        Optional<Task> found = taskRepository.findById(command.getTaskId());

        if (found.isEmpty()) {
            return UpdateStatus.TASK_NOT_FOUND;
        } else {
            return update(found.get(), command);
        }
    }

    private UpdateStatus update(Task task, UpdateTaskDto command) {
        TaskDomainModel taskDomainModel = new TaskDomainModel(task);
        if (command.hasDescription()) {
            taskDomainModel.changeDescription(command.getDescription());
        }

        if (taskDomainModel.changeStatusIfNeeded(command)) {
            taskUpdateAntiCorruptionLayer.processTask(taskDomainModel.getId());
        }

        if (task.getOwner() != null) {
            OwnerDomainModel.Builder builder = owner(task.getOwner().getFirstName(), task.getOwner().getLastName());

            if (command.getOwnerPhoneNumber() != null && command.getOwnerPhonePrefix() != null) {
                builder.withPhoneNumber(command.getOwnerPhoneNumber(), command.getOwnerPhonePrefix());
            }

            if (command.getOwnerEmailAddress() != null) {
                builder.withEmailAddress(command.getOwnerEmailAddress());
            }

            task.setOwner(builder.build().toOwner());

        } else {
            if (command.getOwnerId() != null) {
                Optional<UserDomainModel> found = taskUpdateAntiCorruptionLayer.findUserById(command.getOwnerId());

                if (found.isPresent()) {
                    UserDomainModel user = found.get();
                    OwnerDomainModel ownerDomainModel = user.convertToOwner();

                    task.setOwner(ownerDomainModel.toOwner());
                } else {
                    return UpdateStatus.USER_NOT_FOUND;
                }
            }
        }
        taskRepository.save(taskDomainModel.asTask());

        return UpdateStatus.SUCCESS;
    }
}
