package com.example.medmeproject.Controller;

import com.example.medmeproject.Dto.AppointmentCreateDto;
import com.example.medmeproject.Model.AppointmentTable;
import com.example.medmeproject.Service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/appointment")
@CrossOrigin
public class AppointmentController {
    @Autowired
    AppointmentService appointmentService;

    @GetMapping("")
    public List<AppointmentTable> getAllAppointments(){
        return appointmentService.fetchAppointments();
    }
    @PostMapping("")
    public AppointmentTable addAppointment(@Valid @RequestBody AppointmentCreateDto appointment){
        return appointmentService.createAppointment(appointment);

    }
    @DeleteMapping("/delete/{id}")
    public AppointmentTable deleteAppointment(@PathVariable String appointmentId){
        return appointmentService.deleteAppointment(appointmentId);
    }
    @PostMapping("/update/{id}")
    public AppointmentTable updateAppointment(@PathVariable String id,@Valid @RequestBody AppointmentCreateDto updateAppointment){
        return appointmentService.updateAppointment(id,updateAppointment);
    }
    @GetMapping("/search-id-doctor/{id}")
    public AppointmentTable searchAppointmentByIdDoctor(@PathVariable String idDoctor){
        return appointmentService.searchAppointmentByIdDoctor(idDoctor);
    }
    @GetMapping("/search-id-patient/{id}")
    public AppointmentTable searchAppointmentByIdPatient(@PathVariable String idPatient){
        return appointmentService.searchAppointmentByIdPatient(idPatient);
    }
    @GetMapping("/search-status/{status}")
    public AppointmentTable searchAppointmentByStatus(@PathVariable String status){
        return appointmentService.searchAppointmentByStatus(status);
    }
    @GetMapping("/search-created-by/{createdBy}")
    public AppointmentTable searchAppointmentByCreatedBy(@PathVariable String createdBy){
        return appointmentService.searchAppointmentByCreatedBy(createdBy);
    }
    @GetMapping("/search-duration/{duration}")
    public AppointmentTable searchAppointmentByDuration(@PathVariable String duration){
        return appointmentService.searchAppointmentByDuration(duration);

    }
    @GetMapping("/search-appointment-date/{appointmentDate}")
    public AppointmentTable searchAppointmentByAppointmentDate(@PathVariable LocalDate appointmentDate){
        return appointmentService.searchAppointmentByAppointmentDate(appointmentDate);

    }
    @GetMapping("/search-priority/{priority}")
    public AppointmentTable searchAppointmentByPriority(@PathVariable String priority){
        return appointmentService.searchAppointmentByPriority(priority);

    }

}
