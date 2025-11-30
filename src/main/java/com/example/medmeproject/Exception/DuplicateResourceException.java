package com.example.medmeproject.Exception;

import com.example.medmeproject.Dto.AppointmentCreateDto;
import com.example.medmeproject.Dto.DoctorCreateDto;
import com.example.medmeproject.Dto.PatientCreateDto;

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(DoctorCreateDto dto) {
        super(("A doctor with email " + dto.getEmail() + " already exists."));
    }
    public DuplicateResourceException(PatientCreateDto dto) {
        super(("A patient with email " + dto.getEmail() + " already exists."));
    }

    public DuplicateResourceException(AppointmentCreateDto dto) {
        super(("An AppointmentDto at this time" + dto.getAppointmentDate() +" - "+dto.getAppointmentTime() +" already exists."));

    }
    public DuplicateResourceException(String message) {
        super(message);

    }


}
