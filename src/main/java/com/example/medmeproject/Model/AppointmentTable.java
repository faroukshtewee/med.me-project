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
    private String idDoctor;
    @Field("idPatient")
    private String idPatient;
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
    @Field("favoriteDate")
    private LocalDate favoriteDate;
    @Field("favoriteTime")
    private LocalTime favoriteTime;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getIdDoctor() {
        return idDoctor;
    }

    public void setIdDoctor(String idDoctor) {
        this.idDoctor = idDoctor;
    }

    public String getIdPatient() {
        return idPatient;
    }

    public void setIdPatient(String idPatient) {
        this.idPatient = idPatient;
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
