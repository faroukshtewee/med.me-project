package com.example.medmeproject.repository;

import com.example.medmeproject.Model.AppointmentTable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends MongoRepository<AppointmentTable,String> {
    List<AppointmentTable> getAppointmentByIdDoctor(String idDoctor);

    List<AppointmentTable> getAppointmentByIdPatient(String idPatient);

    List<AppointmentTable> getAppointmentByStatus(String status);

    List<AppointmentTable> getAppointmentByCreatedBy(String createdBy);

    List<AppointmentTable> getAppointmentByDuration(String duration);

    List<AppointmentTable> getAppointmentByAppointmentDate(LocalDate appointmentDate);

    List<AppointmentTable> getAppointmentByPriority(String priority);
}
