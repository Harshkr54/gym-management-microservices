package com.workout_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class WorkoutPlanDto {

    @NotBlank(message = "Plan name is required")
    private String planName;

    @NotBlank(message = "Difficulty level is required")
    private String difficultyLevel;

    @NotEmpty(message = "Workout plan must have at least one exercise")
    @Valid // Validates the exercises inside the list!
    private List<ExerciseDto> exercises;

    // Getters and Setters
    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }
    public String getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }
    public List<ExerciseDto> getExercises() { return exercises; }
    public void setExercises(List<ExerciseDto> exercises) { this.exercises = exercises; }
}