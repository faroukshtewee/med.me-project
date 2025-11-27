package com.example.medmeproject.Model;

import com.example.medmeproject.Dto.TimeSlot;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Document(collection ="DoctorTable")
public class DoctorTable extends UserTable{
    @Field("specialization")
    private String  specialization;
    @Field("licenseNumber")
    private String licenseNumber;
    @Field("yearsOfExperience")
    private Integer yearsOfExperience;
    //the map key is the date and each date contain list of start time and end time
    private Map<LocalDate, List<TimeSlot>> listSchedule = new HashMap<>();
    private List<String> listGenerated15MinSlots= new ArrayList<>();
    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public Integer getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(Integer yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }
    public List<String> generate15MinSlots(Map<LocalDate, List<TimeSlot>> scheduleMap) {

        List<String> finalSlotList = new ArrayList<>();

        // Define the formatter for the output string (e.g., 09:00)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        //  Iterate over every LocalDate (key) and its List<TimeSlot> (value)
        for (Map.Entry<LocalDate, List<TimeSlot>> entry : scheduleMap.entrySet()) {
            List<TimeSlot> daySlots = entry.getValue();
            for (TimeSlot timeSlot : daySlots) {
                LocalTime currentStartTime = timeSlot.getStartTime();
                LocalTime slotEndTime = timeSlot.getEndTime();
                if (currentStartTime.isAfter(slotEndTime) || currentStartTime.equals(slotEndTime)) {
                    continue;
                }
                //Generate 15-minute intervals within the TimeSlot
                // Loop while the current start time is before the slot's end time
                while (currentStartTime.isBefore(slotEndTime)) {
                    // The end time for the current 15-minute interval
                    LocalTime currentEndTime = currentStartTime.plusMinutes(15);
                    // Check if the 15-minute interval exceeds the doctor's available time
                    if (currentEndTime.isAfter(slotEndTime)) {
                        break; // Stop generating slots for this TimeSlot
                    }
                    // Format and add to the final list
                    String slotString = String.format(
                            "%s - %s",
                            currentStartTime.format(formatter),
                            currentEndTime.format(formatter)
                    );
                    finalSlotList.add(slotString);
                    // Move the starting point for the next iteration
                    currentStartTime = currentEndTime;
                }
            }
        }
        return finalSlotList;
    }

    public List<String> getListGenerated15MinSlots() {
        if (this.listGenerated15MinSlots.isEmpty() && !this.listSchedule.isEmpty()) {
            this.listGenerated15MinSlots = generate15MinSlots(this.listSchedule);
        }
        return this.listGenerated15MinSlots;
    }
    public void setListGenerated15MinSlots(List<String> listGenerated15MinSlots) {
        this.listGenerated15MinSlots = listGenerated15MinSlots;
    }
}
