package com.nick404s.dailyfocus.repository;

import com.nick404s.dailyfocus.model.DailyPlan;
import com.nick404s.dailyfocus.model.PlanTask;
import com.nick404s.dailyfocus.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyPlanRepository extends CrudRepository<DailyPlan, Long> {

    List<DailyPlan> findByUser(User user);

    Optional<DailyPlan> findByUserAndDate(User user, LocalDate date);

//    @Query("""
//     SELECT p FROM DailyPlan p
//          LEFT JOIN FETCH p.planTasks
//               WHERE p.user = :user AND p.date = :date
//     """)
//    Optional<DailyPlan> findByUserAndDateWithTasks(User user, LocalDate date);

    Optional<DailyPlan> findByIdAndUser(long id, User user);
}
