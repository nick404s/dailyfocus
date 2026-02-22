package com.nick404s.dailyfocus.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.scheduling.config.Task;

public interface TaskRepository extends CrudRepository<Task, Long> {
    // Stats
    long count();
}
