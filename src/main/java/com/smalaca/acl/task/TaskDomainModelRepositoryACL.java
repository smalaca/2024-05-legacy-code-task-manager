package com.smalaca.acl.task;

import com.smalaca.taskamanager.model.embedded.EmailAddress;
import com.smalaca.taskamanager.model.embedded.Owner;
import com.smalaca.taskamanager.model.embedded.PhoneNumber;
import com.smalaca.taskamanager.model.embedded.Watcher;
import com.smalaca.taskamanager.model.entities.Story;
import com.smalaca.taskamanager.model.entities.Task;
import com.smalaca.taskamanager.model.enums.ToDoItemStatus;
import com.smalaca.taskamanager.repository.StoryRepository;
import com.smalaca.taskamanager.repository.TaskRepository;
import com.smalaca.taskmanager.command.owner.OwnerDomainModel;
import com.smalaca.taskmanager.command.owner.OwnerReadModel;
import com.smalaca.taskmanager.command.task.TaskDomainModel;
import com.smalaca.taskmanager.command.task.TaskDomainModelRepository;
import com.smalaca.taskmanager.command.task.TaskReadModel;
import com.smalaca.taskmanager.command.watcher.WatcherDomainModel;
import com.smalaca.taskmanager.command.watcher.WatcherReadModel;

import java.util.List;
import java.util.Optional;

import static com.smalaca.taskmanager.command.owner.OwnerDomainModel.Builder.owner;
import static com.smalaca.taskmanager.command.watcher.WatcherDomainModel.Builder.watcher;
import static java.util.stream.Collectors.toList;

public class TaskDomainModelRepositoryACL implements TaskDomainModelRepository {
    private final TaskRepository taskRepository;
    private final StoryRepository storyRepository;

    public TaskDomainModelRepositoryACL(TaskRepository taskRepository, StoryRepository storyRepository) {
        this.taskRepository = taskRepository;
        this.storyRepository = storyRepository;
    }

    @Override
    public Long create(TaskDomainModel taskDomainModel) {
        Task task = mapFromTo(taskDomainModel.asReadModel(), new Task());
        Task saved = taskRepository.save(task);
        return saved.getId();
    }

    @Override
    public void update(TaskDomainModel taskDomainModel) {
        TaskReadModel readModel = taskDomainModel.asReadModel();
        Task found = taskRepository.findById(readModel.getTaskId()).get();
        Task task = mapFromTo(readModel, found);

        taskRepository.save(task);
    }

    private Task mapFromTo(TaskReadModel taskReadModel, Task task) {
        task.setTitle(taskReadModel.getTitle());
        task.setDescription(taskReadModel.getDescription());
        task.setStatus(ToDoItemStatus.valueOf(taskReadModel.getStatus()));

        if (taskReadModel.getOwner() != null) {
            task.setOwner(asOwner(taskReadModel.getOwner()));
        }

        taskReadModel.getWatchers().forEach(watcher -> {
            task.addWatcher(asWatcher(watcher));
        });

        if (taskReadModel.getStoryId() != null) {
            Story story = storyRepository.findById(taskReadModel.getStoryId()).get();
            task.setStory(story);
            story.addTask(task);
            storyRepository.save(story);
        }

        return task;
    }

    @Override
    public Optional<TaskDomainModel> findById(Long taskId) {
        Optional<Task> found = taskRepository.findById(taskId);
        return found.map(task -> asTaskDomainModel(task));
    }

    private TaskDomainModel asTaskDomainModel(Task task) {
        OwnerDomainModel owner = task.getOwner() == null ? null : asOwner(task.getOwner());
        List<WatcherDomainModel> watchers = task.getWatchers().stream()
                .map(this::asWatcher)
                .collect(toList());

        return new TaskDomainModel(
                task.getId(), task.getTitle(), task.getDescription(), task.getStatus().name(), owner, watchers);
    }

    private OwnerDomainModel asOwner(Owner owner) {
        OwnerDomainModel.Builder builder = owner(owner.getFirstName(), owner.getLastName());

        if (owner.getPhoneNumber() != null) {
            builder.withPhoneNumber(owner.getPhoneNumber().getNumber(), owner.getPhoneNumber().getPrefix());
        }

        if (owner.getEmailAddress() != null) {
            builder.withEmailAddress(owner.getEmailAddress().getEmailAddress());
        }

        return builder.build();
    }

    private WatcherDomainModel asWatcher(Watcher watcher) {
        WatcherDomainModel.Builder builder = watcher(watcher.getFirstName(), watcher.getLastName());

        if (watcher.getPhoneNumber() != null) {
            builder.withPhoneNumber(watcher.getPhoneNumber().getNumber(), watcher.getPhoneNumber().getPrefix());
        }

        if (watcher.getEmailAddress() != null) {
            builder.withEmailAddress(watcher.getEmailAddress().getEmailAddress());
        }

        return builder.build();
    }

    @Override
    public boolean delete(long id) {
        Optional<Task> found = taskRepository.findById(id);
        found.ifPresent(taskRepository::delete);

        return found.isPresent();
    }


    private Watcher asWatcher(WatcherReadModel readModel) {
        Watcher watcher = new Watcher();
        watcher.setFirstName(readModel.getFirstName());
        watcher.setLastName(readModel.getLastName());
        if (readModel.getEmailAddress() != null) {
            watcher.setEmailAddress(asEmailAddress(readModel.getEmailAddress()));
        }
        if (readModel.getPhoneNumber() != null) {
            watcher.setPhoneNumber(asPhoneNumber(readModel.getPhonePrefix(), readModel.getPhoneNumber()));
        }
        return watcher;
    }

    public Owner asOwner(OwnerReadModel readModel) {
        Owner owner = new Owner();
        owner.setFirstName(readModel.getFirstName());
        owner.setLastName(readModel.getLastName());
        if (readModel.getEmailAddress() != null) {
            owner.setEmailAddress(asEmailAddress(readModel.getEmailAddress()));
        }
        if (readModel.getPhoneNumber() != null) {
            owner.setPhoneNumber(asPhoneNumber(readModel.getPhonePrefix(), readModel.getPhoneNumber()));
        }
        return owner;
    }

    private PhoneNumber asPhoneNumber(String prefix, String number) {
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setNumber(number);
        phoneNumber.setPrefix(prefix);
        return phoneNumber;
    }

    private EmailAddress asEmailAddress(String email) {
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.setEmailAddress(email);
        return emailAddress;
    }
}
