package com.nick404s.dailyfocus.repository;

import com.nick404s.dailyfocus.model.DailyPlan;
import com.nick404s.dailyfocus.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DailyPlanRepository extends CrudRepository<DailyPlan, Long> {

    List<DailyPlan> findByUser(User user);
}
