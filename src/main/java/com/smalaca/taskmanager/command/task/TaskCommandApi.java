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
import com.smalaca.taskamanager.service.ToDoItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public class TaskCommandApi {

    private ToDoItemService toDoItemService;

    public TaskCommandApi(ToDoItemService toDoItemService, UserRepository userRepository, StoryRepository storyRepository, TaskRepository taskRepository) {
        this.toDoItemService = toDoItemService;
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

    public ResponseEntity<Void> update(long id, TaskDto dto) {
        Task task;

        try {
            task = findById(id);
        } catch (TaskDoesNotExistException exception) {
            return ResponseEntity.notFound().build();
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
                    return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
                }
            }
        }
        taskRepository.save(task);
        if (service) {
            toDoItemService.processTask(task.getId());
        }

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> delete(long id) {
        try {
            Optional<Task> found = taskRepository.findById(id);

            if (found.isEmpty()) {
                throw new TaskDoesNotExistException();
            }

            taskRepository.delete(found.get());

            return ResponseEntity.ok().build();
        } catch (TaskDoesNotExistException exception) {
            return ResponseEntity.notFound().build();
        }
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

    private Task findById(long id) {
        if (taskRepository.existsById(id)) {
            return taskRepository.findById(id).get();
        }

        throw new TaskDoesNotExistException();
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
