package com.smalaca.taskamanager.api.rest;


import com.smalaca.acl.story.StoryDomainModelRepositoryACL;
import com.smalaca.acl.owner.OwnerDomainModelRepositoryACL;
import com.smalaca.acl.task.StatusChangeServiceACL;
import com.smalaca.acl.task.TaskDomainModelRepositoryACL;
import com.smalaca.acl.watcher.WatcherDomainModelRepositoryACL;
import com.smalaca.taskamanager.dto.AssigneeDto;
import com.smalaca.taskamanager.dto.StakeholderDto;
import com.smalaca.taskamanager.dto.TaskDto;
import com.smalaca.taskamanager.dto.WatcherDto;
import com.smalaca.taskamanager.exception.TaskDoesNotExistException;
import com.smalaca.taskamanager.exception.TeamNotFoundException;
import com.smalaca.taskamanager.exception.UserNotFoundException;
import com.smalaca.taskamanager.model.embedded.Assignee;
import com.smalaca.taskamanager.model.embedded.EmailAddress;
import com.smalaca.taskamanager.model.embedded.PhoneNumber;
import com.smalaca.taskamanager.model.embedded.Stakeholder;
import com.smalaca.taskamanager.model.embedded.Watcher;
import com.smalaca.taskamanager.model.entities.Task;
import com.smalaca.taskamanager.model.entities.Team;
import com.smalaca.taskamanager.model.entities.User;
import com.smalaca.taskamanager.repository.StoryRepository;
import com.smalaca.taskamanager.repository.TaskRepository;
import com.smalaca.taskamanager.repository.TeamRepository;
import com.smalaca.taskamanager.repository.UserRepository;
import com.smalaca.taskamanager.service.ToDoItemService;
import com.smalaca.taskmanager.command.task.AddTaskWatcherDto;
import com.smalaca.taskmanager.command.task.CommandStatus;
import com.smalaca.taskmanager.command.owner.OwnerDomainModelNotFoundException;
import com.smalaca.taskmanager.command.task.StoryDomainModelNotFoundException;
import com.smalaca.taskmanager.command.task.TaskCommandApi;
import com.smalaca.taskmanager.command.task.TaskDomainModelDoesNotExistException;
import com.smalaca.taskmanager.command.watcher.WatcherDomainModelNotFoundException;
import com.smalaca.taskmanager.query.task.TaskQueryApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/task")
@SuppressWarnings({"checkstyle:ClassFanOutComplexity", "checkstyle:NestedTryDepth", "checkstyle:NestedIfDepth", "PMD.CollapsibleIfStatements"})
public class TaskController {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TaskQueryApi taskQueryApi;
    private final TaskCommandApi taskCommandApi;

    public TaskController(
            TaskRepository taskRepository, UserRepository userRepository, TeamRepository teamRepository,
            StoryRepository storyRepository, ToDoItemService toDoItemService) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        taskQueryApi = new TaskQueryApi(taskRepository);
        taskCommandApi = new TaskCommandApi(
                new StatusChangeServiceACL(toDoItemService),
                new TaskDomainModelRepositoryACL(taskRepository, storyRepository),
                new OwnerDomainModelRepositoryACL(userRepository),
                new StoryDomainModelRepositoryACL(storyRepository),
                new WatcherDomainModelRepositoryACL(userRepository));
    }

    @Transactional
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> findById(@PathVariable Long id) {
        return taskQueryApi.findById(id);
    }

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody TaskDto dto) {
        try {
            Long taskId = taskCommandApi.create(dto.asCreateTaskDto());
            return ResponseEntity.ok(taskId);
        } catch (StoryDomainModelNotFoundException | OwnerDomainModelNotFoundException exception) {
            return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable long id, @RequestBody TaskDto dto) {
        CommandStatus status = taskCommandApi.update(dto.asUpdateTaskDto(id));

        switch (status) {
            case TASK_NOT_FOUND:
                return ResponseEntity.notFound().build();
            case OWNER_NOT_FOUND:
                return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
            case SUCCESS:
                return ResponseEntity.ok().build();
            default:
                throw new RuntimeException("Not supported Task update status.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        if (taskCommandApi.delete(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/watcher")
    public ResponseEntity<Void> addWatcher(@PathVariable long id, @RequestBody WatcherDto dto) {
        try {
            taskCommandApi.addWatcher(new AddTaskWatcherDto(id, dto.getId()));
            return ResponseEntity.ok().build();
        } catch (TaskDomainModelDoesNotExistException exception) {
            return ResponseEntity.notFound().build();
        } catch (WatcherDomainModelNotFoundException exception) {
            return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
        }
    }

    @DeleteMapping("/{taskId}/watcher/{watcherId}")
    @Transactional
    public ResponseEntity<Void> removeWatcher(@PathVariable Long taskId, @PathVariable Long watcherId) {
        try {
            Task task = findTaskBy(taskId);
            User user = findUserBy(watcherId);

            Watcher watcher = new Watcher();

            if (user.getPhoneNumber() != null) {
                PhoneNumber no = new PhoneNumber();
                no.setNumber(user.getPhoneNumber().getNumber());
                no.setPrefix(user.getPhoneNumber().getPrefix());
                watcher.setPhoneNumber(no);
            }

            watcher.setLastName(user.getUserName().getLastName());
            watcher.setFirstName(user.getUserName().getFirstName());

            if (user.getEmailAddress() != null) {
                EmailAddress add = new EmailAddress();
                add.setEmailAddress(user.getEmailAddress().getEmailAddress());
                watcher.setEmailAddress(add);
            }

            task.removeWatcher(watcher);

            taskRepository.save(task);

            return ResponseEntity.ok().build();
        } catch (TaskDoesNotExistException exception) {
            return ResponseEntity.notFound().build();
        } catch (UserNotFoundException exception) {
            return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
        }
    }

    @PutMapping("/{id}/stakeholder")
    public ResponseEntity<Void> addStakeholder(@PathVariable long id, @RequestBody StakeholderDto dto) {
        try {
            Task task = findTaskBy(id);

            try {
                User user = findUserBy(dto.getId());
                Stakeholder stakeholder = new Stakeholder();
                stakeholder.setLastName(user.getUserName().getLastName());
                stakeholder.setFirstName(user.getUserName().getFirstName());

                if (user.getEmailAddress() != null) {
                    EmailAddress address = new EmailAddress();
                    address.setEmailAddress(user.getEmailAddress().getEmailAddress());
                    stakeholder.setEmailAddress(address);
                }

                if (user.getPhoneNumber() != null) {
                    PhoneNumber phNo = new PhoneNumber();
                    phNo.setNumber(user.getPhoneNumber().getNumber());
                    phNo.setPrefix(user.getPhoneNumber().getPrefix());
                    stakeholder.setPhoneNumber(phNo);
                }
                task.addStakeholder(stakeholder);

                taskRepository.save(task);

            } catch (UserNotFoundException exception) {
                return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
            }

            return ResponseEntity.ok().build();
        } catch (TaskDoesNotExistException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{taskId}/stakeholder/{stakeholderId}")
    @Transactional
    public ResponseEntity<Void> removeStakeholder(@PathVariable Long taskId, @PathVariable Long stakeholderId) {
        try {
            Task task = findTaskBy(taskId);
            User user = findUserBy(stakeholderId);

            Stakeholder stkh = new Stakeholder();

            if (user.getPhoneNumber() != null) {
                PhoneNumber phoneNumber = new PhoneNumber();
                phoneNumber.setNumber(user.getPhoneNumber().getNumber());
                phoneNumber.setPrefix(user.getPhoneNumber().getPrefix());
                stkh.setPhoneNumber(phoneNumber);
            }

            stkh.setLastName(user.getUserName().getLastName());
            stkh.setFirstName(user.getUserName().getFirstName());

            if (user.getEmailAddress() != null) {
                EmailAddress emailAddress = new EmailAddress();
                emailAddress.setEmailAddress(user.getEmailAddress().getEmailAddress());
                stkh.setEmailAddress(emailAddress);
            }

            task.removeStakeholder(stkh);

            taskRepository.save(task);

            return ResponseEntity.ok().build();
        } catch (TaskDoesNotExistException exception) {
            return ResponseEntity.notFound().build();
        } catch (UserNotFoundException exception) {
            return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
        }
    }

    @PutMapping("/{id}/assignee")
    public ResponseEntity<Void> addAssignee(@PathVariable long id, @RequestBody AssigneeDto dto) {
        try {
            Task task = findTaskBy(id);

            try {
                User user = findUserBy(dto.getId());
                Assignee asgn = new Assignee();
                asgn.setLastName(user.getUserName().getLastName());
                asgn.setFirstName(user.getUserName().getFirstName());

                try {
                    findTeamBy(dto.getTeamId());
                    asgn.setTeamId(dto.getTeamId());
                    task.setAssignee(asgn);
                    taskRepository.save(task);
                } catch (TeamNotFoundException exception) {
                    return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
                }

            } catch (UserNotFoundException exception) {
                return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
            }

            return ResponseEntity.ok().build();
        } catch (TaskDoesNotExistException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    private void findTeamBy(Long id) {
        Optional<Team> found = teamRepository.findById(id);

        if (found.isEmpty()) {
            throw new TeamNotFoundException();
        }
    }

    @DeleteMapping("/{taskId}/assignee")
    @Transactional
    public ResponseEntity<Void> removeAssignee(@PathVariable Long taskId) {
        try {
            Task task = findTaskBy(taskId);
            task.setAssignee(null);

            taskRepository.save(task);

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
