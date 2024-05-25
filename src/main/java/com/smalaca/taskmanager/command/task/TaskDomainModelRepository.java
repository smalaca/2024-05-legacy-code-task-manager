package com.smalaca.taskmanager.command.task;

import java.util.Optional;

public interface TaskDomainModelRepository {
    boolean delete(long id);


    Optional<TaskDomainModel> findById(Long taskId);

    void save(TaskDomainModel taskDomainModel);
}