package com.nick404s.dailyfocus.repository;

import com.nick404s.dailyfocus.model.DailyPlan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyPlanRepository extends CrudRepository<DailyPlan, Long> {

}
