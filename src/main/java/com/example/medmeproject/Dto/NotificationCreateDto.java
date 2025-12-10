package com.example.medmeproject.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class NotificationCreateDto {
    private String id;
    @NotBlank(message = "Message cannot be empty.")
    @Size(min = 10, max = 1000, message = "Message length is invalid should be between 10 and 1000 chars.")
    private String message;
    @NotBlank(message = "Message End Date cannot be empty")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}" ,message = "Message End Date must be in (YYYY-MM-DD) format ")
    private LocalDate EndDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDate getEndDate() {
        return EndDate;
    }

    public void setEndDate(LocalDate endDate) {
        EndDate = endDate;
    }
}
