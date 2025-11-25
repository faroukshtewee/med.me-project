package com.example.medmeproject.Model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.util.List;

@Document(collection ="ClinicTable")
public class ClinicTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @Field("clinicAddress")
    private String clinicAddress;
    @Field("clinicPhoneNumber")
    private String clinicPhoneNumber;
    private List<LocalDate> listClinicSchedule;

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

    public List<LocalDate> getListClinicSchedule() {
        return listClinicSchedule;
    }

    public void setListClinicSchedule(List<LocalDate> listClinicSchedule) {
        this.listClinicSchedule = listClinicSchedule;
    }
}
