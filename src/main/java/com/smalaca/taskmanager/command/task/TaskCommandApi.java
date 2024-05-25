package com.smalaca.taskmanager.command.task;

import com.smalaca.taskamanager.dto.TaskDto;
import com.smalaca.taskamanager.dto.WatcherDto;
import com.smalaca.taskamanager.exception.ProjectNotFoundException;
import com.smalaca.taskamanager.exception.TaskDoesNotExistException;
import com.smalaca.taskamanager.exception.UserNotFoundException;
import com.smalaca.taskamanager.model.embedded.EmailAddress;
import com.smalaca.taskamanager.model.embedded.Owner;
import com.smalaca.taskamanager.model.embedded.PhoneNumber;
import com.smalaca.taskamanager.model.embedded.Watcher;
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
    private final TaskDeleteCommand taskDeleteCommand;
    private UserRepository userRepository;
    private StoryRepository storyRepository;
    private TaskRepository taskRepository;
    private TaskUpdateCommand taskUpdateCommand;

    public TaskCommandApi(
            UserRepository userRepository, StoryRepository storyRepository, TaskRepository taskRepository,
            TaskUpdateAntiCorruptionLayer taskUpdateAntiCorruptionLayer, TaskDomainModelRepository taskDomainModelRepository) {
        this.userRepository = userRepository;
        this.storyRepository = storyRepository;
        this.taskRepository = taskRepository;
        this.taskUpdateCommand = new TaskUpdateCommand(taskUpdateAntiCorruptionLayer);
        taskDeleteCommand = new TaskDeleteCommand(taskDomainModelRepository);
    }

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

    public UpdateStatus update(UpdateTaskDto updateTaskDto) {
        return taskUpdateCommand.process(updateTaskDto);
    }

    public boolean delete(long id) {
        return taskDeleteCommand.process(id);
    }

    public ResponseEntity<Void> addWatcher(long id, WatcherDto dto) {
        try {
            Task entity1 = findTaskBy(id);

            try {
                User entity2 = findUserBy(dto.getId());
                Watcher entity3 = new Watcher();
                entity3.setFirstName(entity2.getUserName().getFirstName());
                entity3.setLastName(entity2.getUserName().getLastName());

                if (entity2.getEmailAddress() != null) {
                    EmailAddress entity4 = new EmailAddress();
                    entity4.setEmailAddress(entity2.getEmailAddress().getEmailAddress());
                    entity3.setEmailAddress(entity4);
                }

                if (entity2.getPhoneNumber() != null) {
                    PhoneNumber entity5 = new PhoneNumber();
                    entity5.setNumber(entity2.getPhoneNumber().getNumber());
                    entity5.setPrefix(entity2.getPhoneNumber().getPrefix());
                    entity3.setPhoneNumber(entity5);
                }
                entity1.addWatcher(entity3);

                taskRepository.save(entity1);

            } catch (UserNotFoundException exception) {
                return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
            }

            return ResponseEntity.ok().build();
        } catch (TaskDoesNotExistException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    private Task findTaskBy(long id) {
        Optional<Task> found = taskRepository.findById(id);

        if (found.isEmpty()) {
            throw new TaskDoesNotExistException();
        }

        return found.get();
    }

    private User findUserBy(Long id) {
        Optional<User> found = userRepository.findById(id);

        if (found.isEmpty()) {
            throw new UserNotFoundException();
        }

        return found.get();
    }
}
