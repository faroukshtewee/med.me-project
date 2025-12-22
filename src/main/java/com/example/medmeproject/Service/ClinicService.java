package com.example.medmeproject.Service;

import com.example.medmeproject.Dto.TimeSlot;
import com.example.medmeproject.Exception.ResourceNotFoundException;
import com.example.medmeproject.Model.ClinicTable;
import com.example.medmeproject.repository.ClinicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class ClinicService {
    @Autowired
    ClinicRepository clinicRepository;



    public ClinicTable searchClinicByAddress(String clinicAddress){
        return clinicRepository.findClinicByClinicAddress(clinicAddress);
    }
    public ClinicTable searchClinicByPhoneNumber(String clinicPhoneNumber){
        return clinicRepository.findClinicByClinicPhoneNumber(clinicPhoneNumber);
    }
    public List<ClinicTable> fetchAllClinics(){
        return clinicRepository.findAll();
    }
    public ClinicTable fillListClinicSchedule(String id,List<Map<LocalDate, List<TimeSlot>>>  schedule){
        ClinicTable clinic= clinicRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Clinic not found with ID: " + id));
        clinic.setListClinicSchedule(schedule);
        return clinicRepository.save(clinic);
    }

}
