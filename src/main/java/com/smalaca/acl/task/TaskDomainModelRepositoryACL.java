package com.smalaca.acl.task;

import com.smalaca.taskamanager.model.entities.Task;
import com.smalaca.taskamanager.repository.TaskRepository;
import com.smalaca.taskmanager.command.task.TaskDomainModel;
import com.smalaca.taskmanager.command.task.TaskDomainModelRepository;

import java.util.Optional;

public class TaskDomainModelRepositoryACL implements TaskDomainModelRepository {
    private final TaskRepository taskRepository;

    public TaskDomainModelRepositoryACL(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public Long create(TaskDomainModel taskDomainModel) {
        Task saved = taskRepository.save(taskDomainModel.asTask());
        return saved.getId();
    }

    @Override
    public void update(TaskDomainModel taskDomainModel) {
        taskRepository.save(taskDomainModel.asTask());
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
}
