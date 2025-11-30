package com.example.medmeproject.Exception;


import com.example.medmeproject.Dto.AppointmentCreateDto;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String message) {
        super("error: 404 "+message);
    }
    public ResourceNotFoundException(AppointmentCreateDto dto, String val) {
        super("this " + val + " with id " +  ("doctor".equalsIgnoreCase(val) ? String.valueOf(dto.getIdDoctor()) : ("patient".equalsIgnoreCase(val) ? String.valueOf(dto.getIdPateint()) : "unknown") )+ " not exist!!");
    }
}
