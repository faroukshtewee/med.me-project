package com.example.medmeproject.Dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;

public class DoctorCreateDto extends UserCreateDto{
    public enum Specialization {Cardiolog , Orthopedics, Pediatrics, Neurology, Dermatology,General}
    @NotNull(message = "specialization cannot be null")
    @Enumerated(EnumType.STRING)
    private Specialization  specialization;
    @NotBlank(message = "License number is mandatory.")
    @Pattern(regexp = "^\\d{5,8}$", message = "License number must be between 5 and 8 digits only.")
    private String licenseNumber;
    @NotNull(message = "years of experience cannot be null")
    @Min(value=0,message = "min years of experience is 0")
    @Max(value=50,message = "max years of experience is 50")
    private Integer yearsOfExperience;

    public Specialization getSpecialization() {
        return specialization;
    }

    public void setSpecialization(Specialization specialization) {
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
}
