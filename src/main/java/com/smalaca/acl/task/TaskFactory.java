package com.smalaca.acl.task;

import com.smalaca.taskamanager.model.embedded.EmailAddress;
import com.smalaca.taskamanager.model.embedded.Owner;
import com.smalaca.taskamanager.model.embedded.PhoneNumber;
import com.smalaca.taskamanager.model.embedded.Watcher;
import com.smalaca.taskamanager.model.entities.Story;
import com.smalaca.taskamanager.model.entities.Task;
import com.smalaca.taskamanager.model.enums.ToDoItemStatus;
import com.smalaca.taskamanager.repository.StoryRepository;
import com.smalaca.taskmanager.command.owner.OwnerReadModel;
import com.smalaca.taskmanager.command.task.TaskReadModel;
import com.smalaca.taskmanager.command.watcher.WatcherReadModel;

class TaskFactory {
    private final StoryRepository storyRepository;

    TaskFactory(StoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    Task from(TaskReadModel taskReadModel, Task task) {
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
