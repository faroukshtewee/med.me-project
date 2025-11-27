package com.example.medmeproject.repository;

import com.example.medmeproject.Model.AppointmentTable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;

public interface AppointmentRepository extends MongoRepository<AppointmentTable,String> {
    AppointmentTable getAppointmentByIdDoctor(String idDoctor);

    AppointmentTable getAppointmentByIdPatient(String idPatient);

    AppointmentTable getAppointmentByStatus(String status);

    AppointmentTable getAppointmentByCreatedBy(String createdBy);

    AppointmentTable getAppointmentByDuration(String duration);

    AppointmentTable getAppointmentByAppointmentDate(LocalDate appointmentDate);

    AppointmentTable getAppointmentByPriority(String priority);
}
