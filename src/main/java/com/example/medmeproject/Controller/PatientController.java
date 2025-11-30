package com.example.medmeproject.Controller;


import com.example.medmeproject.Dto.PatientCreateDto;
import com.example.medmeproject.Model.PatientTable;
import com.example.medmeproject.Service.PatientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patient")
@CrossOrigin
public class PatientController {
    @Autowired
    PatientService patientService;

    @GetMapping("")
    public List<PatientTable> getAllPatients(){
        return patientService.fetchPatients();
    }
    @PostMapping("")
    public PatientTable addPatient(@Valid @RequestBody PatientCreateDto patient){
        return patientService.addPatient(patient);
    }
    @DeleteMapping("/delete/{id}")
    public PatientTable deletePatient(@PathVariable String identityCard){
        return patientService.deletePatient(identityCard);
    }
    @GetMapping("/{identityCard}")
    public PatientTable getPatientByIdentityCard(@PathVariable String identityCard){
        return patientService.getPatientByIdentityCard(identityCard);
    }
    @PostMapping("/update/{id}")
    public PatientTable updatePatient(@PathVariable String id,@Valid @RequestBody PatientCreateDto updatePatient){
        return patientService.updatePatient(id,updatePatient);
    }
    @GetMapping("/most-appointments")
    public List<PatientTable> getPatientsWithMostAppointments(@RequestParam int limit){
        return patientService.getPatientsWithMostAppointments(limit);
    }
    @GetMapping("/search-first-name")
    public List<PatientTable> searchPatientsByFirstName(@RequestParam String keyword){
        return patientService.searchPatientsByFirstName(keyword);
    }
    @GetMapping("/search-last-name")
    public List<PatientTable> searchPatientsByByLastName(@RequestParam String keyword){
        return patientService.searchPatientsByByLastName(keyword);

    }
    @GetMapping("/age-statistics")
    public Map<String, Object> getPatientAgeStatistics(){
        return patientService.getPatientAgeStatistics();
    }
    @PostMapping("/approve/{id}")
    public void approvePatient(@PathVariable String id) throws Exception {
        patientService.approvePatientRegistration(id);
    }
}
