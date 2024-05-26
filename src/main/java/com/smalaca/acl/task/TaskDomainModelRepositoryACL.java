package com.smalaca.acl.task;

import com.smalaca.taskamanager.model.entities.Task;
import com.smalaca.taskamanager.repository.StoryRepository;
import com.smalaca.taskamanager.repository.TaskRepository;
import com.smalaca.taskmanager.command.task.TaskDomainModel;
import com.smalaca.taskmanager.command.task.TaskDomainModelRepository;
import com.smalaca.taskmanager.command.task.TaskReadModel;

import java.util.Optional;

public class TaskDomainModelRepositoryACL implements TaskDomainModelRepository {
    private final TaskRepository taskRepository;
    private final TaskFactory taskFactory;
    private final TaskDomainModelFactory taskDomainModelFactory;

    private TaskDomainModelRepositoryACL(
            TaskRepository taskRepository, TaskFactory taskFactory, TaskDomainModelFactory taskDomainModelFactory) {
        this.taskRepository = taskRepository;
        this.taskFactory = taskFactory;
        this.taskDomainModelFactory = taskDomainModelFactory;
    }

    public static TaskDomainModelRepository taskDomainModelRepositoryACL(TaskRepository taskRepository, StoryRepository storyRepository) {
        return new TaskDomainModelRepositoryACL(taskRepository, new TaskFactory(storyRepository), new TaskDomainModelFactory());
    }

    @Override
    public Long create(TaskDomainModel taskDomainModel) {
        Task task = taskFactory.from(taskDomainModel.asReadModel(), new Task());
        Task saved = taskRepository.save(task);

        return saved.getId();
    }

    @Override
    public void update(TaskDomainModel taskDomainModel) {
        TaskReadModel readModel = taskDomainModel.asReadModel();
        Task found = taskRepository.findById(readModel.getTaskId()).get();
        Task task = taskFactory.from(readModel, found);

        taskRepository.save(task);
    }

    @Override
    public Optional<TaskDomainModel> findById(Long taskId) {
        Optional<Task> found = taskRepository.findById(taskId);
        return found.map(taskDomainModelFactory::from);
    }

    @Override
    public boolean delete(long id) {
        Optional<Task> found = taskRepository.findById(id);
        found.ifPresent(taskRepository::delete);

        return found.isPresent();
    }
}
