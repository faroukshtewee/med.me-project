package com.example.medmeproject.Dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment {
    private Long Id;
    private Long IdDoctor;
    private Long idPateint;
    private String status;
    private String createdBy;
    private String duration;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String priority;
    private String notes;

    public Appointment() {
    }

    public Appointment(Long id, Long idDoctor, Long idPateint, String status, String createdBy, String duration, LocalDate appointmentDate, LocalTime appointmentTime, String priority, String notes) {
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

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public Long getIdDoctor() {
        return IdDoctor;
    }

    public void setIdDoctor(Long idDoctor) {
        IdDoctor = idDoctor;
    }

    public Long getIdPateint() {
        return idPateint;
    }

    public void setIdPateint(Long idPateint) {
        this.idPateint = idPateint;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
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

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "Id=" + Id +
                ", IdDoctor=" + IdDoctor +
                ", idPateint=" + idPateint +
                ", status='" + status + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", duration='" + duration + '\'' +
                ", appointmentDate=" + appointmentDate +
                ", appointmentTime=" + appointmentTime +
                ", priority='" + priority + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}
