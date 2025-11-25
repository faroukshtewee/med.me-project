package com.example.medmeproject.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ClinicCreateDto {
    private String id;
    @NotBlank(message = "Clinic Address cannot be empty.")
    @Size(min = 10, max = 255, message = "Clinic Address length is invalid should be between 10 and 255 chars.")
    private String clinicAddress;
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9\\s]).{8,20}$", message = "Password must be 8-20 chars long and include: uppercase, lowercase, number, and special character.")
    @NotNull(message = "Clinic Phone Number cannot be null.")
    @NotBlank(message = "Clinic Phone Number cannot be empty.")
    @Pattern(regexp = "^05\\d-\\d{7}$",message = "please enter a valid phone number format!!")
    private String clinicPhoneNumber;

    public ClinicCreateDto() {
    }

    public ClinicCreateDto(String id, String clinicAddress, String clinicPhoneNumber) {
        this.id = id;
        this.clinicAddress = clinicAddress;
        this.clinicPhoneNumber = clinicPhoneNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClinicAddress() {
        return clinicAddress;
    }

    public void setClinicAddress(String clinicAddress) {
        this.clinicAddress = clinicAddress;
    }

    public String getClinicPhoneNumber() {
        return clinicPhoneNumber;
    }

    public void setClinicPhoneNumber(String clinicPhoneNumber) {
        this.clinicPhoneNumber = clinicPhoneNumber;
    }
}
