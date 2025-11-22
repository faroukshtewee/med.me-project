package com.example.medmeproject.Dto;

import java.time.LocalDate;
import java.util.List;

public class Clinic{
    private Long id;
    private String clinicAddress;
    private String clinicPhoneNumber;
    private List<LocalDate> listClinicSchedule;

    public Clinic() {
    }

    public Clinic(Long id, String clinicAddress, String clinicPhoneNumber) {
        this.id = id;
        this.clinicAddress = clinicAddress;
        this.clinicPhoneNumber = clinicPhoneNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    @Override
    public String toString() {
        return "Clinic{" +
                "id=" + id +
                ", clinicAddress='" + clinicAddress + '\'' +
                ", clinicPhoneNumber='" + clinicPhoneNumber + '\'' +
                ", listClinicSchedule=" + listClinicSchedule +
                '}';
    }
}
