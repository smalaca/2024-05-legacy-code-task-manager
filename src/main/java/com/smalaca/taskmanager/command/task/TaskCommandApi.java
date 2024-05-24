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
import com.smalaca.taskamanager.repository.TaskRepository;
import com.smalaca.taskamanager.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public class TaskCommandApi {

    public TaskCommandApi(UserRepository userRepository, StoryRepository storyRepository, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.storyRepository = storyRepository;
        this.taskRepository = taskRepository;
    }

    private UserRepository userRepository;
    private StoryRepository storyRepository;
    private TaskRepository taskRepository;

    public ResponseEntity<Long> create(TaskDto dto) {
        Task t = new Task();
        t.setTitle(dto.getTitle());
        t.setDescription(dto.getDescription());
        t.setStatus(ToDoItemStatus.valueOf(dto.getStatus()));

        if (dto.getOwnerId() != null) {
            Optional<User> found = userRepository.findById(dto.getOwnerId());

            if (found.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
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
            try {
                if (!storyRepository.existsById(dto.getStoryId())) {
                    throw new ProjectNotFoundException();
                }

                str = storyRepository.findById(dto.getStoryId()).get();
            } catch (ProjectNotFoundException exception) {
                return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
            }

            t.setStory(str);
            str.addTask(t);
            storyRepository.save(str);
        }

        Task saved = taskRepository.save(t);

        return ResponseEntity.ok(saved.getId());
    }
}
