package com.example.medmeproject.Service;

import com.example.medmeproject.Dto.AppointmentCreateDto;
import com.example.medmeproject.Exception.ResourceNotFoundException;
import com.example.medmeproject.Model.AppointmentTable;
import com.example.medmeproject.Model.DoctorTable;
import com.example.medmeproject.Model.FavoriteDatesTable;
import com.example.medmeproject.Model.PatientTable;
import com.example.medmeproject.repository.AppointmentRepository;
import com.example.medmeproject.repository.DoctorRepository;
import com.example.medmeproject.repository.FavoriteDatesRepository;
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
import java.util.Map;

@Service
public class AppointmentService {
    @Autowired
    AppointmentRepository appointmentRepository;

    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    FavoriteDatesRepository favoriteDatesRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    SmsService smsService;

    public List<AppointmentTable> fetchAppointments(){
        return appointmentRepository.findAll();
    }
    public AppointmentTable createAppointment(AppointmentCreateDto appointment) {
        String doctorId = appointment.getIdDoctor();
        String patientId = appointment.getIdPateint();
        LocalTime favoriteTime = appointment.getFavoriteTime();
        LocalDate favoriteDate = appointment.getFavoriteDate();
        AppointmentTable appointmentTable = modelMapper.map(appointment, AppointmentTable.class);
        appointmentTable.setPriority(appointment.getPriority().toString());
        appointmentTable.setDuration(String.valueOf(appointment.getDuration().getMinutes()));
        appointmentRepository.save(appointmentTable);
        DoctorTable doctor = doctorRepository.findById(doctorId).orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + doctorId));
        PatientTable patient = patientRepository.findById(patientId).orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + patientId));
        doctor.getAppointments().add(appointmentTable);
        patient.getAppointments().add(appointmentTable);

        LocalDate appointmentDate = appointmentTable.getAppointmentDate();
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
        if (!isSlotAvailable(doctor, appointmentDate, slotsToRemove)) {
            throw new ResourceNotFoundException("The requested appointment slot is no longer available or conflicts with the doctor's schedule.");
        }
        appointmentRepository.save(appointmentTable);
        doctor.getAppointments().add(appointmentTable);
        patient.getAppointments().add(appointmentTable);


        FavoriteDatesTable favoriteDatesTable=new FavoriteDatesTable();
        favoriteDatesTable.setFavoriteDate(favoriteDate);
        favoriteDatesTable.setFavoriteTime(favoriteTime);
        favoriteDatesTable.setIdDoctor(doctorId);
        favoriteDatesTable.setIdPatient(patientId);
        favoriteDatesRepository.save(favoriteDatesTable);

        Map<LocalDate, List<String>> doctorSlotsMap = doctor.getGeneratedSlotsByDate();
        List<String> dailySlots = doctorSlotsMap.get(appointmentDate);
        if (dailySlots != null) {
            dailySlots.removeAll(slotsToRemove);
        } else {
            throw new ResourceNotFoundException("Warning: Appointment date " + appointmentDate + " not found in doctor's generated schedule.");
        }

        doctorRepository.save(doctor);
        patientRepository.save(patient);
        return appointmentTable;
    }
    public AppointmentTable deleteAppointment(String appointmentId) {
        AppointmentTable appointmentToDelete = appointmentRepository.findById(appointmentId).orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));
        String doctorId = appointmentToDelete.getIdDoctor();
        String patientId = appointmentToDelete.getIdPatient();
        DoctorTable doctor = doctorRepository.findById(doctorId).orElseThrow(() -> new ResourceNotFoundException("Doctor linked to appointment not found with ID: " + doctorId));
        PatientTable patient = patientRepository.findById(patientId).orElseThrow(() -> new ResourceNotFoundException("Patient linked to appointment not found with ID: " + patientId));

        doctor.getAppointments().remove(appointmentToDelete);
        patient.getAppointments().remove(appointmentToDelete);
        LocalDate appointmentDate = appointmentToDelete.getAppointmentDate();
        LocalTime appointmentStartTime = appointmentToDelete.getAppointmentTime();
        int durationInMinutes = 0;
        try {
            durationInMinutes = Integer.parseInt(appointmentToDelete.getDuration());
        } catch (NumberFormatException e) {
            System.err.println("Error parsing duration: " + appointmentToDelete.getDuration());
        }
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

        Map<LocalDate, List<String>> doctorSlotsMap = doctor.getGeneratedSlotsByDate();
        List<String> dailySlots = doctorSlotsMap.get(appointmentDate);
        if (dailySlots != null) {
            dailySlots.addAll(slotsToRestore);
            Collections.sort(dailySlots);
        } else {
            throw new ResourceNotFoundException("Warning: Appointment date " + appointmentDate + " not found in doctor's generated schedule during deletion.");
        }

        doctorRepository.save(doctor);
        patientRepository.save(patient);
        appointmentRepository.deleteById(appointmentId);
        //return all rows eqyals to appointmentDate and appointmentStartTime
        List<FavoriteDatesTable> favoriteDatesList =favoriteDatesRepository.getAllFavoriteDates(appointmentDate,appointmentStartTime);
        if(!favoriteDatesList.isEmpty()) {
            for (FavoriteDatesTable element : favoriteDatesList) {
                String doctId = element.getIdDoctor();
                String patId = element.getIdPatient();
                PatientTable patientTable = patientRepository.findById(patId).orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + patId));
                DoctorTable doctorTable=doctorRepository.findById(doctId).orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + doctId));
                String message="\"Hello,\n" +
                        "\n" +
                        "We are pleased to inform you that a preferred appointment slot has just become available.\n" +
                        "\n" +
                        "The new availability is "+appointmentStartTime +" on "+ appointmentDate+ " with Dr."+doctorTable.getFirstName()+" "+doctorTable.getLastName() +".\n" +
                        "\n" +
                        "If you wish to secure this time, please confirm your choice by booking immediately through our system. This slot will be available on a first-come, first-served basis.\n" +
                        "\n" +
                        "Thank you.\"";
                smsService.sendUpdateSms(patientTable.getIdentityCard(), message, patientTable.getPhoneNumber());

            }
        }
        return appointmentToDelete;
    }
    public AppointmentTable updateAppointment(String id, AppointmentCreateDto updateAppointment) {
        AppointmentTable findAppointment = appointmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + id));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        DoctorTable doctor = doctorRepository.findById(findAppointment.getIdDoctor()).orElseThrow(() -> new ResourceNotFoundException("Doctor not found for appointment with ID: " + findAppointment.getIdDoctor()));

        LocalDate oldDate = findAppointment.getAppointmentDate();
        LocalTime oldTime = findAppointment.getAppointmentTime();
        int oldDuration = Integer.parseInt(findAppointment.getDuration());

        LocalDate newDate = updateAppointment.getAppointmentDate();
        LocalTime newTime = updateAppointment.getAppointmentTime();
        int newDuration = updateAppointment.getDuration().getMinutes();

        boolean timeOrDateChanged = !oldDate.equals(newDate) || !oldTime.equals(newTime) || oldDuration != newDuration;

        if (timeOrDateChanged) {

            LocalTime newAppointmentEndTime = newTime.plusMinutes(newDuration);
            LocalTime newCurrentSlotTime = newTime;
            List<String> newRequiredSlots = new ArrayList<>();

            while (newCurrentSlotTime.isBefore(newAppointmentEndTime)) {
                LocalTime currentSlotEnd = newCurrentSlotTime.plusMinutes(15);

                if (currentSlotEnd.isAfter(newAppointmentEndTime)) {
                    break;
                }

                String slotString = String.format(
                        "%s - %s",
                        newCurrentSlotTime.format(formatter),
                        currentSlotEnd.format(formatter)
                );

                newRequiredSlots.add(slotString);
                newCurrentSlotTime = currentSlotEnd;
            }

            LocalTime oldAppointmentEndTime = oldTime.plusMinutes(oldDuration);
            LocalTime oldCurrentSlotTime = oldTime;
            List<String> oldOccupiedSlots = new ArrayList<>();

            while (oldCurrentSlotTime.isBefore(oldAppointmentEndTime)) {
                LocalTime currentSlotEnd = oldCurrentSlotTime.plusMinutes(15);

                if (currentSlotEnd.isAfter(oldAppointmentEndTime)) {
                    break;
                }

                String slotString = String.format(
                        "%s - %s",
                        oldCurrentSlotTime.format(formatter),
                        currentSlotEnd.format(formatter)
                );

                oldOccupiedSlots.add(slotString);
                oldCurrentSlotTime = currentSlotEnd;
            }

            Map<LocalDate, List<String>> doctorSlotsMap = doctor.getGeneratedSlotsByDate();
            List<String> oldDailySlots = doctorSlotsMap.get(oldDate);

            if (oldDailySlots != null && oldDate.equals(newDate)) {
                oldDailySlots.addAll(oldOccupiedSlots);
                Collections.sort(oldDailySlots);
            }

            if (!isSlotAvailable(doctor, newDate, newRequiredSlots)) {
                if (oldDailySlots != null && oldDate.equals(newDate)) {
                    oldDailySlots.removeAll(oldOccupiedSlots);
                }
                throw new IllegalArgumentException("The new time slot conflicts with another appointment or the doctor's schedule. Check if the doctor is working on " + newDate + " or if the time is already booked.");
            }

            if (oldDailySlots != null) {
                oldDailySlots.removeAll(oldOccupiedSlots);
            }

            List<String> newDailySlots = doctorSlotsMap.get(newDate);
            if (newDailySlots != null) {
                newDailySlots.removeAll(newRequiredSlots);
            } else {
                System.err.println("new date was available but its list was null during reservation.");
            }
        }
        modelMapper.map(updateAppointment, findAppointment);
        findAppointment.setDuration(String.valueOf(newDuration));
        appointmentRepository.save(findAppointment);
        doctorRepository.save(doctor);
        //return all rows eqyals to appointmentDate and appointmentStartTime
        List<FavoriteDatesTable> favoriteDatesList =favoriteDatesRepository.getAllFavoriteDates(oldDate,oldTime);
        if(!favoriteDatesList.isEmpty()) {
            for (FavoriteDatesTable element : favoriteDatesList) {
                String doctId = element.getIdDoctor();
                String patId = element.getIdPatient();
                PatientTable patientTable = patientRepository.findById(patId).orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + patId));
                DoctorTable doctorTable=doctorRepository.findById(doctId).orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + doctId));
                String message="\"Hello,\n" +
                        "\n" +
                        "We are pleased to inform you that a preferred appointment slot has just become available.\n" +
                        "\n" +
                        "The new availability is "+oldTime +" on "+ oldDate+ " with Dr."+doctorTable.getFirstName()+" "+doctorTable.getLastName() +".\n" +
                        "\n" +
                        "If you wish to secure this time, please confirm your choice by booking immediately through our system. This slot will be available on a first-come, first-served basis.\n" +
                        "\n" +
                        "Thank you.\"";
                smsService.sendUpdateSms(patientTable.getIdentityCard(), message, patientTable.getPhoneNumber());

            }
        }
        return findAppointment;
    }
    public List<AppointmentTable> searchAppointmentByIdDoctor(String idDoctor){
        return appointmentRepository.getAppointmentByIdDoctor(idDoctor);
    }
    public List<AppointmentTable> searchAppointmentByIdPatient(String idPatient){
        return appointmentRepository.getAppointmentByIdPatient(idPatient);
    }
    public List<AppointmentTable> searchAppointmentByStatus(String status){
        return appointmentRepository.getAppointmentByStatus(status);
    }
    public List<AppointmentTable> searchAppointmentByCreatedBy(String createdBy){
        return appointmentRepository.getAppointmentByCreatedBy(createdBy);
    }
    public List<AppointmentTable> searchAppointmentByDuration(String duration){
        return appointmentRepository.getAppointmentByDuration(duration);
    }
    public List<AppointmentTable> searchAppointmentByAppointmentDate(LocalDate appointmentDate){
        return appointmentRepository.getAppointmentByAppointmentDate(appointmentDate);
    }
    public List<AppointmentTable> searchAppointmentByPriority(String priority){
        return appointmentRepository.getAppointmentByPriority(priority);
    }
    private boolean isSlotAvailable(DoctorTable doctor, LocalDate date, List<String> requiredSlots) {
        Map<LocalDate, List<String>> doctorSlotsMap = doctor.getGeneratedSlotsByDate();
        List<String> availableDailySlots = doctorSlotsMap.getOrDefault(date, Collections.emptyList());
        return availableDailySlots.containsAll(requiredSlots);
    }


    public List<AppointmentTable> getAppointmentsDueForReminder() {
        LocalDate reminderDate = LocalDate.now().plusDays(3);
        return appointmentRepository.getAppointmentByAppointmentDate(reminderDate);
    }
}
