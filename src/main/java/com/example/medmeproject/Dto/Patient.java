package com.example.medmeproject.Dto;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Patient extends User {
    private LocalDate favoriteDate;
    private Set<Long> appointmentIds = new HashSet<Long>();

    public Patient() {
    }

    public Patient(Long id, String firstName, String lastName, String email, String phoneNumber, LocalDate birthDate, String age, String address, String password, String gender, String role, String healthFund, String identityCard, LocalDate favoriteDate) {
        super(id, firstName, lastName, email, phoneNumber, birthDate, age, address, password, gender, role, healthFund, identityCard);
        this.favoriteDate = favoriteDate;
    }

    public LocalDate getFavoriteDate() {
        return favoriteDate;
    }

    public void setFavoriteDate(LocalDate favoriteDate) {
        this.favoriteDate = favoriteDate;
    }

    public Set<Long> getAppointmentIds() {
        return appointmentIds;
    }

    public void setAppointmentIds(Set<Long> appointmentIds) {
        this.appointmentIds = appointmentIds;
    }
}
