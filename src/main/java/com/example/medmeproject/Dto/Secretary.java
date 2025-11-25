package com.example.medmeproject.Dto;

import java.time.LocalDate;

public class Secretary extends User {
    private boolean mainSecretary;
    private Integer yearsOfExperience;

    public Secretary() {
    }

    public Secretary(String id, String firstName, String lastName, String email, String phoneNumber, LocalDate birthDate, String age, String address, String password, String gender, String role, String healthFund, String identityCard, boolean mainSecretary, Integer yearsOfExperience) {
        super(id, firstName, lastName, email, phoneNumber, birthDate, address, password, gender, role, healthFund, identityCard);
        this.mainSecretary = mainSecretary;
        this.yearsOfExperience = yearsOfExperience;
    }

    public boolean isMainSecretary() {
        return mainSecretary;
    }

    public void setMainSecretary(boolean mainSecretary) {
        this.mainSecretary = mainSecretary;
    }

    public Integer getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(Integer yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }
}
