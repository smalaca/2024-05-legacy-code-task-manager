package com.smalaca.taskmanager.command.task;

import com.smalaca.taskamanager.dto.TaskDto;
import com.smalaca.taskamanager.exception.TaskDoesNotExistException;
import com.smalaca.taskamanager.model.entities.Task;
import com.smalaca.taskamanager.model.enums.ToDoItemStatus;
import com.smalaca.taskamanager.repository.TaskRepository;

import static com.smalaca.taskmanager.command.task.OwnerDomainModel.Builder.owner;

class TaskUpdateCommand {
    private final TaskRepository taskRepository;
    private final TaskUpdateAntiCorruptionLayer taskUpdateAntiCorruptionLayer;

    TaskUpdateCommand(TaskRepository taskRepository, TaskUpdateAntiCorruptionLayer taskUpdateAntiCorruptionLayer) {
        this.taskRepository = taskRepository;
        this.taskUpdateAntiCorruptionLayer = taskUpdateAntiCorruptionLayer;
    }

    UpdateStatus process(long id, TaskDto dto) {
        Task task;

        try {
            task = findById(id);
        } catch (TaskDoesNotExistException exception) {
            return UpdateStatus.TASK_NOT_FOUND;
        }

        if (dto.getDescription() != null) {
            task.setDescription(dto.getDescription());
        }

        boolean service = false;
        if (dto.getStatus() != null) {
            if (ToDoItemStatus.valueOf(dto.getStatus()) != task.getStatus()) {
                service = true;
                task.setStatus(ToDoItemStatus.valueOf(dto.getStatus()));
            }
        }

        if (task.getOwner() != null) {
            OwnerDomainModel.Builder builder = owner(task.getOwner().getFirstName(), task.getOwner().getLastName());

            if (dto.getOwnerPhoneNumberPrefix() != null && dto.getOwnerPhoneNumberNumber() != null) {
                builder.withPhoneNumber(dto.getOwnerPhoneNumberNumber(), dto.getOwnerPhoneNumberPrefix());
            }

            if (dto.getOwnerEmailAddress() != null) {
                builder.withEmailAddress(dto.getOwnerEmailAddress());
            }

            task.setOwner(builder.build().toOwner());

        } else {
            if (dto.getOwnerId() != null) {
                boolean userExists = taskUpdateAntiCorruptionLayer.existsById(dto.getOwnerId());

                if (userExists) {
                    UserDomainModel user = taskUpdateAntiCorruptionLayer.findById(dto.getOwnerId());
                    OwnerDomainModel ownerDomainModel = user.convertToOwner();

                    task.setOwner(ownerDomainModel.toOwner());
                } else {
                    return UpdateStatus.USER_NOT_FOUND;
                }
            }
        }
        taskRepository.save(task);
        if (service) {
            taskUpdateAntiCorruptionLayer.processTask(task.getId());
        }

        return UpdateStatus.SUCCESS;
    }

    private Task findById(long id) {
        if (taskRepository.existsById(id)) {
            return taskRepository.findById(id).get();
        }

        throw new TaskDoesNotExistException();
    }
}
