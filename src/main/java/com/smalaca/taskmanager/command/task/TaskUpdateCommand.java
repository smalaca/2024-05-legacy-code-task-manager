package com.smalaca.taskmanager.command.task;

import com.smalaca.taskamanager.dto.TaskDto;
import com.smalaca.taskamanager.exception.TaskDoesNotExistException;
import com.smalaca.taskamanager.model.embedded.EmailAddress;
import com.smalaca.taskamanager.model.embedded.Owner;
import com.smalaca.taskamanager.model.embedded.PhoneNumber;
import com.smalaca.taskamanager.model.entities.Task;
import com.smalaca.taskamanager.model.entities.User;
import com.smalaca.taskamanager.model.enums.ToDoItemStatus;
import com.smalaca.taskamanager.repository.TaskRepository;
import com.smalaca.taskamanager.repository.UserRepository;
import com.smalaca.taskamanager.service.ToDoItemService;

class TaskUpdateCommand {
    private ToDoItemService toDoItemService;
    private TaskRepository taskRepository;
    private UserRepository userRepository;

    TaskUpdateCommand(ToDoItemService toDoItemService, TaskRepository taskRepository, UserRepository userRepository) {
        this.toDoItemService = toDoItemService;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
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
            Owner o = new Owner();
            o.setFirstName(task.getOwner().getFirstName());
            o.setLastName(task.getOwner().getLastName());

            if (dto.getOwnerPhoneNumberPrefix() != null && dto.getOwnerPhoneNumberNumber() != null) {
                PhoneNumber pno = new PhoneNumber();
                pno.setNumber(dto.getOwnerPhoneNumberNumber());
                pno.setPrefix(dto.getOwnerPhoneNumberPrefix());
                o.setPhoneNumber(pno);
            }

            if (dto.getOwnerEmailAddress() != null) {
                EmailAddress email = new EmailAddress();
                email.setEmailAddress(dto.getOwnerEmailAddress());
                o.setEmailAddress(email);
            }

            task.setOwner(o);

        } else {
            if (dto.getOwnerId() != null) {
                boolean userExists = userRepository.existsById(dto.getOwnerId());

                if (userExists) {
                    User user = userRepository.findById(dto.getOwnerId()).get();
                    Owner ownr = new Owner();

                    if (user.getPhoneNumber() != null) {
                        PhoneNumber number = new PhoneNumber();
                        number.setNumber(user.getPhoneNumber().getNumber());
                        number.setPrefix(user.getPhoneNumber().getPrefix());
                        ownr.setPhoneNumber(number);
                    }

                    ownr.setLastName(user.getUserName().getLastName());
                    ownr.setFirstName(user.getUserName().getFirstName());

                    if (user.getEmailAddress() != null) {
                        EmailAddress eAdd = new EmailAddress();
                        eAdd.setEmailAddress(user.getEmailAddress().getEmailAddress());
                        ownr.setEmailAddress(eAdd);
                    }

                    task.setOwner(ownr);
                } else {
                    return UpdateStatus.USER_NOT_FOUND;
                }
            }
        }
        taskRepository.save(task);
        if (service) {
            toDoItemService.processTask(task.getId());
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
