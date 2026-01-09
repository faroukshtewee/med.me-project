package com.example.medmeproject.Model;


import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Document(collection = "UserTable")
public class UserTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @Field("firstName")
    private String firstName;
    @Field("lastName")
    private String lastName;
    @Field("email")
    private String email;
    @Field("phoneNumber")
    private String phoneNumber;
    @Field("birthDate")
    private LocalDate birthDate;
    @Field("age")
    private String age;
    @Field("address")
    private String address;
    @Field("gender")
    private String gender;
    @Field("role")
    private String role;
    @Field("healthFund")
    private String healthFund;
    @Field("identityCard")
    private String identityCard;
    @Field("authCode")
    private String authCode;
    private List<AppointmentTable> appointments=new ArrayList<AppointmentTable>();
    private List<ClinicTable> clinics=new ArrayList<ClinicTable>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getHealthFund() {
        return healthFund;
    }

    public void setHealthFund(String healthFund) {
        this.healthFund = healthFund;
    }

    public String getIdentityCard() {
        return identityCard;
    }

    public void setIdentityCard(String identityCard) {
        this.identityCard = identityCard;
    }

    public List<AppointmentTable> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<AppointmentTable> appointments) {
        this.appointments = appointments;
    }

    public List<ClinicTable> getClinics() {
        return clinics;
    }

    public void setClinics(List<ClinicTable> clinics) {
        this.clinics = clinics;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }
}
