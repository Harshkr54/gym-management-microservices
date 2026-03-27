package com.workout_service.controller;

import com.workout_service.dto.WorkoutPlanDto;
import com.workout_service.entity.WorkoutPlan;
import com.workout_service.service.WorkoutService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workouts")
public class WorkoutController {

    private final WorkoutService workoutService;

    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    // ONLY Trainers and Owners can create workout plans
    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('ROLE_OWNER', 'ROLE_TRAINER')")
    public ResponseEntity<WorkoutPlan> createPlan(
            @Valid @RequestBody WorkoutPlanDto request,
            @RequestHeader("loggedInUserEmail") String trainerEmail) {

        WorkoutPlan savedPlan = workoutService.createWorkoutPlan(request, trainerEmail);
        return new ResponseEntity<>(savedPlan, HttpStatus.CREATED);
    }

    // ALL logged-in users (Members, Trainers, Owners) can view all plans
    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ROLE_OWNER', 'ROLE_TRAINER', 'ROLE_MEMBER')")
    public ResponseEntity<List<WorkoutPlan>> getAllPlans() {
        return ResponseEntity.ok(workoutService.getAllPlans());
    }

    // Trainers can view just the plans they specifically created
    @GetMapping("/my-plans")
    @PreAuthorize("hasAnyAuthority('ROLE_TRAINER')")
    public ResponseEntity<List<WorkoutPlan>> getMyPlans(
            @RequestHeader("loggedInUserEmail") String trainerEmail) {
        return ResponseEntity.ok(workoutService.getPlansByTrainer(trainerEmail));
    }
}