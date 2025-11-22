package com.example.medmeproject.Dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class UserCreateDto {
    private Long id;
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
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9\\s]).{8,20}$", message = "Password must be 8-20 chars long and include: uppercase, lowercase, number, and special character.")
    private String password;
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

}
