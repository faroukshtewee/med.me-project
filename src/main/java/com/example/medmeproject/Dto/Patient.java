package com.example.medmeproject.Dto;

import com.example.medmeproject.Model.AppointmentTable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Patient extends User {
    private LocalDate favoriteDate;
    private List<Appointment> appointments = new ArrayList<Appointment>();

    public Patient() {
    }

    public Patient(String id, String firstName, String lastName, String email, String phoneNumber, LocalDate birthDate, String age, String address, String password, String gender, String role, String healthFund, String identityCard, LocalDate favoriteDate) {
        super(id, firstName, lastName, email, phoneNumber, birthDate, address, password, gender, role, healthFund, identityCard);
        this.favoriteDate = favoriteDate;
    }

    public LocalDate getFavoriteDate() {
        return favoriteDate;
    }

    public void setFavoriteDate(LocalDate favoriteDate) {
        this.favoriteDate = favoriteDate;
    }

    public List<Appointment> getAppointmentIds() {
        return appointments;
    }

    public void setAppointmentIds(List<Appointment> appointmentIds) {
        this.appointments = appointmentIds;
    }
}
