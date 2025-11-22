package com.example.medmeproject.Dto;

import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.time.LocalTime;

public class PatientCreateDto extends UserCreateDto{
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}" ,message = "Appointment Date must be in (YYYY-MM-DD) format ")
    private LocalDate favoriteDate;
    @Pattern(regexp = "\\d{2}:\\d{2}" ,message = "Appointment Time must be in (HH:MM) format ")
    private LocalTime favoriteTime;

    public LocalDate getFavoriteDate() {
        return favoriteDate;
    }

    public void setFavoriteDate(LocalDate favoriteDate) {
        this.favoriteDate = favoriteDate;
    }

    public LocalTime getFavoriteTime() {
        return favoriteTime;
    }

    public void setFavoriteTime(LocalTime favoriteTime) {
        this.favoriteTime = favoriteTime;
    }
}
