package com.workout_service.repository;

import com.workout_service.entity.WorkoutPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, Long> {

    // Custom method so a trainer can easily fetch only the plans THEY created!
    List<WorkoutPlan> findByTrainerEmail(String trainerEmail);
}