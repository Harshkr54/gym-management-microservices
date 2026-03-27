package com.workout_service.service;

import com.workout_service.dto.WorkoutPlanDto;
import com.workout_service.entity.Exercise;
import com.workout_service.entity.WorkoutPlan;
import com.workout_service.exception.ProfileIncompleteException;
import com.workout_service.repository.WorkoutPlanRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkoutService {

    private final WorkoutPlanRepository workoutPlanRepository;
    // 1. Inject our new resilient Validator instead of the raw Feign Client
    private final MemberProfileValidator memberProfileValidator;

    public WorkoutService(WorkoutPlanRepository workoutPlanRepository, MemberProfileValidator memberProfileValidator) {
        this.workoutPlanRepository = workoutPlanRepository;
        this.memberProfileValidator = memberProfileValidator;
    }

    public WorkoutPlan createWorkoutPlan(WorkoutPlanDto dto, String trainerEmail) {

        // 2. Call the Validator! This forces the code to pass through the Circuit Breaker
        boolean hasProfile = memberProfileValidator.verifyTrainerProfile(trainerEmail);

        if (!hasProfile){
            // 3. Fixed the string message so it displays beautifully in Postman
            throw new ProfileIncompleteException("Access Denied: You must complete your Member Profile before creating workout plans!");
        }

        WorkoutPlan plan = new WorkoutPlan();
        plan.setPlanName(dto.getPlanName());
        plan.setDifficultyLevel(dto.getDifficultyLevel());
        plan.setTrainerEmail(trainerEmail);

        List<Exercise> exercises = dto.getExercises().stream().map(exDto -> {
            Exercise exercise = new Exercise();
            exercise.setName(exDto.getName());
            exercise.setSets(exDto.getSets());
            exercise.setReps(exDto.getReps());
            exercise.setRestTimeSeconds(exDto.getRestTimeSeconds());

            exercise.setWorkoutPlan(plan);
            return exercise;
        }).collect(Collectors.toList());

        plan.setExercises(exercises);

        return workoutPlanRepository.save(plan);
    }

    public List<WorkoutPlan> getAllPlans() {
        return workoutPlanRepository.findAll();
    }

    public List<WorkoutPlan> getPlansByTrainer(String email) {
        return workoutPlanRepository.findByTrainerEmail(email);
    }
}