package com.example.medmeproject.Dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentCreateDto {
    public enum Duration {
        FIFTEEN_MINUTES(15),
        THIRTY_MINUTES(30),
        FORTY_FIVE_MINUTES(45),
        SIXTY_MINUTES(60);

        private final int minutes;

        Duration(int minutes) {
            this.minutes = minutes;
        }

        public int getMinutes() {
            return minutes;
        }
    }
    public enum Priority {
        LOW,
        MEDIUM,
        HIGH,
        URGENT
    }
    public enum Status {
        SCHEDULED,
        COMPLETED,
        CANCELLED
    }

    private String Id;
    @NotNull(message = "Doctor Id cannot be null")
    private String IdDoctor;
    @NotNull(message = "Patient Id cannot be null")
    private String idPateint;
    @NotNull(message = "status cannot be null")
    @Enumerated(EnumType.STRING)
    private Status status;
    @NotNull(message = "CreatedBy cannot be null")
    @NotBlank(message = "CreatedBy cannot be empty")
    private String createdBy;
    @NotNull(message = "duration cannot be null")
    @Enumerated(EnumType.STRING)
    private Duration duration;
    @NotNull(message = "Appointment Date cannot be null")
    @NotBlank(message = "Appointment Date cannot be empty")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}" ,message = "Appointment Date must be in (YYYY-MM-DD) format ")
    private LocalDate appointmentDate;
    @NotNull(message = "Appointment Time cannot be null")
    @NotBlank(message = "Appointment Time cannot be empty")
    @Pattern(regexp = "\\d{2}:\\d{2}" ,message = "Appointment Time must be in (HH:MM) format ")
    private LocalTime appointmentTime;
    @NotNull(message = "Priority cannot be null")
    @Enumerated(EnumType.STRING)
    private Priority priority;
    @Size(max = 500,message = "Max notes should be 500 characters")
    private String notes;

    public AppointmentCreateDto() {
    }

    public AppointmentCreateDto(String id, String idDoctor, String idPateint, Status status, String createdBy, Duration duration, LocalDate appointmentDate, LocalTime appointmentTime, Priority priority, String notes) {
        Id = id;
        IdDoctor = idDoctor;
        this.idPateint = idPateint;
        this.status = status;
        this.createdBy = createdBy;
        this.duration = duration;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.priority = priority;
        this.notes = notes;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getIdDoctor() {
        return IdDoctor;
    }

    public void setIdDoctor(String idDoctor) {
        IdDoctor = idDoctor;
    }

    public String getIdPateint() {
        return idPateint;
    }

    public void setIdPateint(String idPateint) {
        this.idPateint = idPateint;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

}
