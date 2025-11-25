package com.example.medmeproject.Model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Document(collection ="DoctorTable")
public class DoctorTable extends UserTable{
    @Field("specialization")
    private String  specialization;
    @Field("licenseNumber")
    private String licenseNumber;
    @Field("yearsOfExperience")
    private Integer yearsOfExperience;
    private List<LocalDateTime> listSchedule=new ArrayList<>();

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
}
