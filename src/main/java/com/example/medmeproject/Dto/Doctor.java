package com.example.medmeproject.Dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Doctor extends User{
private String  specialization;
private String licenseNumber;
private Integer yearsOfExperience;
private List<LocalDateTime> listSchedule=new ArrayList<>();

    public Set<Long> getAppointmentIds() {
        return appointmentIds;
    }

    public void setAppointmentIds(Set<Long> appointmentIds) {
        this.appointmentIds = appointmentIds;
    }

    private Set<Long> appointmentIds= new HashSet<>();

    public Doctor() {
    }

    public Doctor(String id, String firstName, String lastName, String email, String phoneNumber, LocalDate birthDate, String age, String address, String gender, String role, String healthFund, String identityCard, String specialization, String licenseNumber, Integer yearsOfExperience) {
        super(id, firstName, lastName, email, phoneNumber, birthDate, address, gender, role, healthFund, identityCard);
        this.specialization = specialization;
        this.licenseNumber = licenseNumber;
        this.yearsOfExperience = yearsOfExperience;
    }
}
