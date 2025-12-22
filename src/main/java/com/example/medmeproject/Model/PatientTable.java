package com.example.medmeproject.Model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Document(collection ="PatientTable")
public class PatientTable extends UserTable{
    @Field("approved")
    private boolean approved=false;
    @Field("patientIdMedMe")
    private String patientIdMedMe;
    @Field("listAppointmentIdsMedMe")
    //key DateTime value appointment id from medme api (GBooking server)
    private List<Map<String,String>> listMapAppointmentIdsMedMe=new ArrayList<Map<String, String>>();

    public boolean isApproved() {
        return approved;
    }
    public void setApproved(boolean approved) {
        this.approved = approved;
    }
    public String getPatientIdMedMe() {
        return patientIdMedMe;
    }
    public void setPatientIdMedMe(String patientIdMedMe) {
        this.patientIdMedMe = patientIdMedMe;
    }

    public List<Map<String, String>> getListMapAppointmentIdsMedMe() {
        return listMapAppointmentIdsMedMe;
    }

    public void setListMapAppointmentIdsMedMe(List<Map<String, String>> listMapAppointmentIdsMedMe) {
        this.listMapAppointmentIdsMedMe = listMapAppointmentIdsMedMe;
    }
}
