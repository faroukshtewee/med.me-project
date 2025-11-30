package com.example.medmeproject.Model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalTime;

@Document(collection ="FavoriteDatesTable")
public class FavoriteDatesTable {
    @org.springframework.data.annotation.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String Id;
    @Field("IdDoctor")
    private String idDoctor;
    @Field("idPatient")
    private String idPatient;
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
