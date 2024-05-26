package com.smalaca.acl.task;

import com.smalaca.taskamanager.model.embedded.Owner;
import com.smalaca.taskamanager.model.embedded.Watcher;
import com.smalaca.taskamanager.model.entities.Task;
import com.smalaca.taskmanager.command.owner.OwnerDomainModel;
import com.smalaca.taskmanager.command.task.TaskDomainModel;
import com.smalaca.taskmanager.command.watcher.WatcherDomainModel;

import static com.smalaca.taskmanager.command.owner.OwnerDomainModel.Builder.owner;
import static com.smalaca.taskmanager.command.task.TaskDomainModel.Builder.taskDomainModel;
import static com.smalaca.taskmanager.command.watcher.WatcherDomainModel.Builder.watcher;

class TaskDomainModelFactory {
    TaskDomainModel from(Task task) {
        OwnerDomainModel owner = task.getOwner() == null ? null : asOwner(task.getOwner());
        TaskDomainModel taskDomainModel = taskDomainModel(
                    task.getId(), task.getTitle(), task.getDescription(), task.getStatus().name())
                .withOwner(owner)
                .build();

        task.getWatchers().forEach(watcher -> {
            taskDomainModel.addWatcher(asWatcher(watcher));
        });

        return taskDomainModel;
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
}
