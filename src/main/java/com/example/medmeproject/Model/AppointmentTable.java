package com.example.medmeproject.Model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalTime;

@Document(collection ="AppointmentTable")
public class AppointmentTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String Id;
    @Field("IdDoctor")
    private Long IdDoctor;
    @Field("idPateint")
    private Long idPateint;
    @Field("status")
    private String status;
    @Field("createdBy")
    private String createdBy;
    @Field("duration")
    private String duration;
    @Field("appointmentDate")
    private LocalDate appointmentDate;
    @Field("appointmentTime")
    private LocalTime appointmentTime;
    @Field("priority")
    private String priority;
    @Field("notes")
    private String notes;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
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
}
