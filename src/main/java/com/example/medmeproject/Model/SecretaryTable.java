package com.example.medmeproject.Model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Document(collection ="SecretaryTable")
public class SecretaryTable extends UserTable {
    @Field("mainSecretary")
    private boolean mainSecretary;
    @Field("yearsOfExperience")
    private Integer yearsOfExperience;
    private List<AppointmentTable> requestToApprove=new ArrayList<AppointmentTable>();

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

    public List<AppointmentTable> getRequestToApprove() {
        return requestToApprove;
    }

    public void setRequestToApprove(List<AppointmentTable> requestToApprove) {
        this.requestToApprove = requestToApprove;
    }
}
