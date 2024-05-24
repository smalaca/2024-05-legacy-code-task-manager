package com.smalaca.taskamanager.processor;

import com.smalaca.taskamanager.model.embedded.Assignee;
import com.smalaca.taskamanager.model.embedded.Owner;
import com.smalaca.taskamanager.model.embedded.Stakeholder;
import com.smalaca.taskamanager.model.embedded.Watcher;
import com.smalaca.taskamanager.model.entities.Project;
import com.smalaca.taskamanager.model.enums.ToDoItemStatus;
import com.smalaca.taskamanager.model.interfaces.ToDoItem;

import java.util.List;

public class FakeToDoItem implements ToDoItem {
    @Override
    public ToDoItemStatus getStatus() {
        return null;
    }

    @Override
    public Project getProject() {
        return null;
    }

    @Override
    public List<Watcher> getWatchers() {
        return null;
    }

    @Override
    public Owner getOwner() {
        return null;
    }

    @Override
    public boolean isAssigned() {
        return false;
    }

    @Override
    public Assignee getAssignee() {
        return null;
    }

    @Override
    public List<Stakeholder> getStakeholders() {
        return null;
    }

    @Override
    public Long getId() {
        return null;
    }
}
