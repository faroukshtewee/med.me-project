package com.example.medmeproject.Model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection ="SecretaryTable")
public class SecretaryTable extends UserTable {
    @Field("mainSecretary")
    private boolean mainSecretary;
    @Field("yearsOfExperience")
    private Integer yearsOfExperience;

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
