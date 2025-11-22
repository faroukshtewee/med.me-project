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
private Set<Long> appointmentIds= new HashSet<>();

    public Doctor() {
    }

    public Doctor(Long id, String firstName, String lastName, String email, String phoneNumber, LocalDate birthDate, String age, String address, String password, String gender, String role, String healthFund, String identityCard, String specialization, String licenseNumber, Integer yearsOfExperience) {
        super(id, firstName, lastName, email, phoneNumber, birthDate, age, address, password, gender, role, healthFund, identityCard);
        this.specialization = specialization;
        this.licenseNumber = licenseNumber;
        this.yearsOfExperience = yearsOfExperience;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public Integer getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(Integer yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public List<LocalDateTime> getListSchedule() {
        return listSchedule;
    }

    public void setListSchedule(List<LocalDateTime> listSchedule) {
        this.listSchedule = listSchedule;
    }

    public Set<Long> getAppointmentIds() {
        return appointmentIds;
    }

    public void setAppointmentIds(Set<Long> appointmentIds) {
        this.appointmentIds = appointmentIds;
    }
}
