package com.example.medmeproject.Dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class SecretaryCreateDto extends UserCreateDto{
    @NotNull(message = "Main Secretary status must be explicitly set to true or false.")
    private boolean mainSecretary;
    @Min(value=0,message = "min years of experience is 0")
    @Max(value=50,message = "max years of experience is 50")
    private Integer yearsOfExperience;
    public Integer getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(Integer yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public boolean isMainSecretary() {
        return mainSecretary;
    }

    public void setMainSecretary(boolean mainSecretary) {
        this.mainSecretary = mainSecretary;
    }
}
