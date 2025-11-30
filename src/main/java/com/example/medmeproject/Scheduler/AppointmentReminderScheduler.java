package com.example.medmeproject.Scheduler;

import com.example.medmeproject.Model.AppointmentTable;
import com.example.medmeproject.Model.DoctorTable;
import com.example.medmeproject.Model.PatientTable;
import com.example.medmeproject.Service.AppointmentService;
import com.example.medmeproject.Service.SmsService;
import com.example.medmeproject.Exception.ResourceNotFoundException;
import com.example.medmeproject.repository.PatientRepository;
import com.example.medmeproject.repository.DoctorRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class AppointmentReminderScheduler {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private SmsService smsService;

    /**
     * Scheduled task to run every day at 8:00 AM.
     * Checks for appointments 3 days in the future and sends a reminder.
     * Cron expression: second minute hour day-of-month month day-of-week
     * "0 0 8 * * *" -> 8:00:00 AM every day
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void sendAppointmentReminders() {
        List<AppointmentTable> appointments = appointmentService.getAppointmentsDueForReminder();

        if (appointments.isEmpty()) {
            throw new ResourceNotFoundException("No appointments found for reminder date: " + LocalDate.now().plusDays(3));
        }

        for (AppointmentTable appointment : appointments) {
            try {
                PatientTable patient = patientRepository.findById(appointment.getIdPatient()).orElseThrow(() -> new ResourceNotFoundException("Patient not found for reminder: " + appointment.getIdPatient()));
                DoctorTable doctor = doctorRepository.findById(appointment.getIdDoctor()).orElseThrow(() -> new ResourceNotFoundException("Doctor not found for reminder: " + appointment.getIdDoctor()));
                String doctorFullName = doctor.getFirstName() + " " + doctor.getLastName();
                String reminderMessage = String.format("REMINDER: Your appointment is scheduled for 3 days from now. " +
                                "Details: %s on %s with Dr. %s. Please ensure you are prepared for your visit.",
                        appointment.getAppointmentTime(),
                        appointment.getAppointmentDate(),
                        doctorFullName
                );
                smsService.sendUpdateSms(patient.getIdentityCard(), reminderMessage, patient.getPhoneNumber());
            } catch (ResourceNotFoundException e) {
                throw new ResourceNotFoundException("Skipping reminder for appointment " + appointment.getId() + ": " + e.getMessage());
            } catch (Exception e) {
                throw new ResourceNotFoundException("Failed to send reminder for appointment " + appointment.getId() + ". Error: " + e.getMessage());
            }
        }
    }
}