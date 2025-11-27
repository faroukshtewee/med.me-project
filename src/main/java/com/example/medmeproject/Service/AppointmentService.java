package com.example.medmeproject.Service;

import com.example.medmeproject.Dto.AppointmentCreateDto;
import com.example.medmeproject.Model.AppointmentTable;
import com.example.medmeproject.Model.DoctorTable;
import com.example.medmeproject.Model.PatientTable;
import com.example.medmeproject.repository.AppointmentRepository;
import com.example.medmeproject.repository.DoctorRepository;
import com.example.medmeproject.repository.PatientRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class AppointmentService {
    @Autowired
    AppointmentRepository appointmentRepository;

    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<AppointmentTable> fetchAppointments(){
        return appointmentRepository.findAll();
    }
    public AppointmentTable createAppointment(AppointmentCreateDto appointment) {
        String doctorId = appointment.getIdDoctor();
        String patientId = appointment.getIdPateint();
        AppointmentTable appointmentTable = modelMapper.map(appointment, AppointmentTable.class);
        appointmentTable.setPriority(appointment.getPriority().toString());
        appointmentTable.setDuration(String.valueOf(appointment.getDuration().getMinutes()));
        appointmentRepository.save(appointmentTable);
        DoctorTable doctor = doctorRepository.findById(doctorId).orElse(null);
        PatientTable patient = patientRepository.findById(patientId).orElse(null);
        doctor.getAppointments().add(appointmentTable);
        patient.getAppointments().add(appointmentTable);

        LocalTime appointmentStartTime = appointment.getAppointmentTime();
        int durationInMinutes = appointment.getDuration().getMinutes();
        LocalTime appointmentEndTime = appointmentStartTime.plusMinutes(durationInMinutes);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime currentSlotTime = appointmentStartTime;

        List<String> slotsToRemove = new ArrayList<>();

        // Generate the 15-minute segments covering the appointment duration
        while (currentSlotTime.isBefore(appointmentEndTime)) {
            LocalTime currentSlotEnd = currentSlotTime.plusMinutes(15);

            // Ensure we don't exceed the total duration
            if (currentSlotEnd.isAfter(appointmentEndTime)) {
                break;
            }

            String slotString = String.format(
                    "%s - %s",
                    currentSlotTime.format(formatter),
                    currentSlotEnd.format(formatter)
            );

            slotsToRemove.add(slotString);
            currentSlotTime = currentSlotEnd; // Move to the next 15-minute interval
        }

        // Retrieve and modify the doctor's list
        List<String> doctorGeneratedSlots = doctor.getListGenerated15MinSlots();
        doctorGeneratedSlots.removeAll(slotsToRemove);

        doctorRepository.save(doctor);
        return appointmentTable;
    }
    public AppointmentTable deleteAppointment(String appointmentId) {
        AppointmentTable appointmentToDelete = appointmentRepository.findById(appointmentId).orElse(null);

        String doctorId = appointmentToDelete.getIdDoctor();
        String patientId = appointmentToDelete.getIdPatient();
        DoctorTable doctor = doctorRepository.findById(doctorId).orElse(null);
        PatientTable patient = patientRepository.findById(patientId).orElse(null);

        doctor.getAppointments().remove(appointmentToDelete);
        patient.getAppointments().remove(appointmentToDelete);


        LocalTime appointmentStartTime = appointmentToDelete.getAppointmentTime();
        // You MUST store the duration (e.g., 15, 30, 45, or 60) in the AppointmentTable
        // or calculate it based on a field. Assuming a getDurationInMinutes() exists.
        int durationInMinutes = Integer.parseInt(appointmentToDelete.getDuration());
        LocalTime appointmentEndTime = appointmentStartTime.plusMinutes(durationInMinutes);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalTime currentSlotTime = appointmentStartTime;
        List<String> slotsToRestore = new ArrayList<>();

        while (currentSlotTime.isBefore(appointmentEndTime)) {
            LocalTime currentSlotEnd = currentSlotTime.plusMinutes(15);
            if (currentSlotEnd.isAfter(appointmentEndTime)) {
                break;
            }
            String slotString = String.format(
                    "%s - %s",
                    currentSlotTime.format(formatter),
                    currentSlotEnd.format(formatter)
            );

            slotsToRestore.add(slotString);
            currentSlotTime = currentSlotEnd;
        }

        List<String> doctorGeneratedSlots = doctor.getListGenerated15MinSlots();
        doctorGeneratedSlots.addAll(slotsToRestore);
        Collections.sort(doctorGeneratedSlots);
        doctor.setListGenerated15MinSlots(doctorGeneratedSlots);

        doctorRepository.save(doctor);
        patientRepository.save(patient);
        appointmentRepository.deleteById(appointmentId);
        return appointmentToDelete;
    }
    public AppointmentTable updateAppointment(String id,AppointmentCreateDto updateAppointment){
        AppointmentTable findAppointment = appointmentRepository.findById(id).orElse(null);
        modelMapper.map(updateAppointment, findAppointment);
        findAppointment.setDuration(String.valueOf(updateAppointment.getDuration().getMinutes()));
        appointmentRepository.save(findAppointment);
        return findAppointment;
    }
    public AppointmentTable searchAppointmentByIdDoctor(String idDoctor){
        return appointmentRepository.getAppointmentByIdDoctor(idDoctor);
    }
    public AppointmentTable searchAppointmentByIdPatient(String idPatient){
        return appointmentRepository.getAppointmentByIdPatient(idPatient);
    }
    public AppointmentTable searchAppointmentByStatus(String status){
        return appointmentRepository.getAppointmentByStatus(status);
    }
    public AppointmentTable searchAppointmentByCreatedBy(String createdBy){
        return appointmentRepository.getAppointmentByCreatedBy(createdBy);
    }
    public AppointmentTable searchAppointmentByDuration(String duration){
        return appointmentRepository.getAppointmentByDuration(duration);
    }
    public AppointmentTable searchAppointmentByAppointmentDate(LocalDate appointmentDate){
        return appointmentRepository.getAppointmentByAppointmentDate(appointmentDate);
    }
    public AppointmentTable searchAppointmentByPriority(String priority){
        return appointmentRepository.getAppointmentByPriority(priority);
    }

}
