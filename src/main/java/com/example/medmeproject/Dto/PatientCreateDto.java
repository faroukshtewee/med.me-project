package com.example.medmeproject.Dto;

import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.time.LocalTime;

public class PatientCreateDto extends UserCreateDto{

    private boolean approved=false;


    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }
}
