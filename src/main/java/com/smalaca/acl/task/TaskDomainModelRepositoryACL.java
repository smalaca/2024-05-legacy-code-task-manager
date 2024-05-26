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
import com.smalaca.taskmanager.command.owner.OwnerReadModel;
import com.smalaca.taskmanager.command.task.TaskDomainModel;
import com.smalaca.taskmanager.command.task.TaskDomainModelRepository;
import com.smalaca.taskmanager.command.task.TaskReadModel;
import com.smalaca.taskmanager.command.watcher.WatcherReadModel;

import java.util.Optional;

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
        taskRepository.save(taskDomainModel.asTask());
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
        }

        return task;
    }

    @Override
    public Optional<TaskDomainModel> findById(Long taskId) {
        Optional<Task> found = taskRepository.findById(taskId);
        return found.map(TaskDomainModel::new);
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
