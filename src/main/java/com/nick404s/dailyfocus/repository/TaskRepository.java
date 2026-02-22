package com.nick404s.dailyfocus.repository;

import com.nick404s.dailyfocus.model.PlanTask;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends CrudRepository<PlanTask, Long> {
    // Stats
    long count();
}
