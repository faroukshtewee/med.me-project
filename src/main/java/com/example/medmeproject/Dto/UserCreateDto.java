package com.example.medmeproject.Dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class UserCreateDto {
    private String id;
    @NotBlank(message = "The First Name shouldn't be empty.")
    @Size(min = 2, max = 50,message = "First Name must be between 2 to 50 characters.")
    private String firstName;
    @NotBlank(message = "The Last Name shouldn't be empty")
    @Size(min = 2, max = 50,message = "Last Name must be between 2 to 50 characters.")
    private String lastName;
    @NotNull(message = "Email name cannot be null")
    @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",message = "Enter valid email format.")
    @NotBlank(message = "The email shouldn't be empty.")
    private String email;
    @NotNull(message = "Phone Number cannot be null.")
    @NotBlank(message = "Phone Number cannot be empty.")
    @Pattern(regexp = "^05\\d-\\d{7}$",message = "please enter a valid phone number format!!")
    private String phoneNumber;
    @NotNull(message = "Birth date is required.")
    @Past(message = "Birth date must be in the past.")
    private LocalDate birthDate;
    @Pattern(regexp = "^(0|[1-9]|[1-9][0-9]|1[01][0-9]|120)$", message = "Age must be a whole number between 0 and 120.")
    private String age;
    @NotBlank(message = "Address cannot be empty.")
    @Size(min = 10, max = 255, message = "Address length is invalid should be between 10 and 255 chars.")
    private String address;
    @Pattern(regexp = "Male|Female", flags = Pattern.Flag.CASE_INSENSITIVE, message = "Gender must be Male or Female.")
    private String gender;
    @NotBlank(message = "Role must be specified.")
    @Pattern(regexp = "Doctor|Secretary|Patient", flags = Pattern.Flag.CASE_INSENSITIVE, message = "Role must be Doctor,Secretary or Patient.")
    private String role;
    @NotBlank(message = "Health fund is required.")
    @Size(min = 2, max = 50, message = "Health fund name length is invalid.")
    private String healthFund;
    @Pattern(regexp = "^\\d{9}$", message = "Identity Card must be a 9-digit number.")
    private String identityCard;


    @NotBlank(message = "Auth Code is required.")
    @Size(min = 6, max = 6, message = "Auth Code length is invalid.")
    private String authCode;

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

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }
}
