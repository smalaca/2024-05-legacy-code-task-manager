package com.smalaca.taskmanager.command.task;

import com.smalaca.taskamanager.dto.TaskDto;
import com.smalaca.taskamanager.exception.ProjectNotFoundException;
import com.smalaca.taskamanager.model.embedded.EmailAddress;
import com.smalaca.taskamanager.model.embedded.Owner;
import com.smalaca.taskamanager.model.embedded.PhoneNumber;
import com.smalaca.taskamanager.model.entities.Story;
import com.smalaca.taskamanager.model.entities.Task;
import com.smalaca.taskamanager.model.entities.User;
import com.smalaca.taskamanager.model.enums.ToDoItemStatus;
import com.smalaca.taskamanager.repository.StoryRepository;
import com.smalaca.taskamanager.repository.UserRepository;

import java.util.Optional;

class TaskCreateCommand {
    private final TaskDomainModelRepository taskDomainModelRepository;
    private final UserRepository userRepository;
    private final StoryRepository storyRepository;

    TaskCreateCommand(TaskDomainModelRepository taskDomainModelRepository, UserRepository userRepository, StoryRepository storyRepository) {
        this.taskDomainModelRepository = taskDomainModelRepository;
        this.userRepository = userRepository;
        this.storyRepository = storyRepository;
    }

    Long process(TaskDto dto) {
        Task t = new Task();
        t.setTitle(dto.getTitle());
        t.setDescription(dto.getDescription());
        t.setStatus(ToDoItemStatus.valueOf(dto.getStatus()));

        if (dto.getOwnerId() != null) {
            Optional<User> found = userRepository.findById(dto.getOwnerId());

            if (found.isEmpty()) {
                throw new OwnerDomainModelNotFoundException(dto.getOwnerId());
            } else {
                User u = found.get();
                Owner o = new Owner();
                o.setLastName(u.getUserName().getLastName());
                o.setFirstName(u.getUserName().getFirstName());

                if (u.getEmailAddress() != null) {
                    EmailAddress ea = new EmailAddress();
                    ea.setEmailAddress(u.getEmailAddress().getEmailAddress());
                    o.setEmailAddress(ea);
                }

                if (u.getPhoneNumber() != null) {
                    PhoneNumber pn = new PhoneNumber();
                    pn.setNumber(u.getPhoneNumber().getNumber());
                    pn.setPrefix(u.getPhoneNumber().getPrefix());
                    o.setPhoneNumber(pn);
                }

                t.setOwner(o);
            }
        }

        if (dto.getStoryId() != null) {
            Story str;
            if (!storyRepository.existsById(dto.getStoryId())) {
                throw new ProjectNotFoundException();
            }

            str = storyRepository.findById(dto.getStoryId()).get();

            t.setStory(str);
            str.addTask(t);
            storyRepository.save(str);
        }

        return taskDomainModelRepository.create(new TaskDomainModel(t));
    }
}
