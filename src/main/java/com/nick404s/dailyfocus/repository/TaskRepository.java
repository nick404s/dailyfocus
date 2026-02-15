package com.nick404s.dailyfocus.repository;

import com.nick404s.dailyfocus.model.PlanTask;
import org.springframework.data.repository.CrudRepository;

public interface TaskRepository extends CrudRepository<PlanTask, Long> {
}
