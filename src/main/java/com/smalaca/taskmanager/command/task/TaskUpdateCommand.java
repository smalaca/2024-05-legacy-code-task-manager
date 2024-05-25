package com.smalaca.taskmanager.command.task;

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

    UpdateStatus process(UpdateTaskDto dto) {
        Optional<Task> found = taskRepository.findById(dto.getTaskId());

        if (found.isEmpty()) {
            return UpdateStatus.TASK_NOT_FOUND;
        } else {
            return update(found.get(), dto);
        }
    }

    private UpdateStatus update(Task task, UpdateTaskDto dto) {
        TaskDomainModel taskDomainModel = new TaskDomainModel(task);
        if (dto.hasDescription()) {
            taskDomainModel.changeDescription(dto.getDescription());
        }

        if (taskDomainModel.changeStatusIfNeeded(dto)) {
            taskUpdateAntiCorruptionLayer.processTask(taskDomainModel.getId());
        }

        if (task.getOwner() != null) {
            OwnerDomainModel.Builder builder = owner(task.getOwner().getFirstName(), task.getOwner().getLastName());

            if (dto.hasOwnerPhoneNumber()) {
                builder.withPhoneNumber(dto.getOwnerPhoneNumber(), dto.getOwnerPhonePrefix());
            }

            if (dto.hasOwnerEmailAddress()) {
                builder.withEmailAddress(dto.getOwnerEmailAddress());
            }

            task.setOwner(builder.build().toOwner());

        } else {
            if (hasOwnerId(dto)) {
                Optional<UserDomainModel> found = taskUpdateAntiCorruptionLayer.findUserById(dto.getOwnerId());

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

    private boolean hasOwnerId(UpdateTaskDto dto) {
        return dto.getOwnerId() != null;
    }
}
