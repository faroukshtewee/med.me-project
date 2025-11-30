package com.example.medmeproject.Service;

import com.example.medmeproject.Dto.PatientCreateDto;
import com.example.medmeproject.Exception.ResourceNotFoundException;
import com.example.medmeproject.Model.PatientTable;
import com.example.medmeproject.repository.PatientRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.RuntimeErrorException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PatientService extends UserService{
    @Autowired
    PatientRepository patientRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<PatientTable> fetchPatients(){
        return patientRepository.findAll();
    }
    public PatientTable addPatient(PatientCreateDto patient){
        PatientTable patientTable= modelMapper.map(patient, PatientTable.class);
        patientTable.setAge(calculateAge(patient.getBirthDate().toString()).toString());
        patientRepository.save(patientTable);
        return patientTable;
    }
    public PatientTable deletePatient(String identityCard){
        return patientRepository.deletePatientById(identityCard);
    }
    public PatientTable getPatientByIdentityCard(String identityCard){
        return patientRepository.getPatientByIdentityCard(identityCard);
    }
    public PatientTable updatePatient(String id,PatientCreateDto updatePatient){
        PatientTable findPatient = patientRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + id));
        modelMapper.map(updatePatient, findPatient);
        findPatient.setAge(calculateAge(updatePatient.getBirthDate().toString()).toString());
        patientRepository.save(findPatient);
        return findPatient;
    }
    public List<PatientTable> getPatientsWithMostAppointments(int limit){
        List<PatientTable>patientsWithMostAppointments= fetchPatients();
        Collections.sort(patientsWithMostAppointments, Comparator.comparing((PatientTable patient) -> patient.getAppointments().size()).reversed());
        return patientsWithMostAppointments.stream().limit(limit).collect(Collectors.toList());
    }
    public List<PatientTable> searchPatientsByFirstName(String keyword){
        List<PatientTable> patients = fetchPatients();
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        final String lowerCaseKeyword = keyword.trim().toLowerCase();
        List<PatientTable> patientsByFirstName= patientRepository.getPatientByFirstName(keyword);
        Collections.sort(patientsByFirstName, Comparator.comparing(PatientTable::getFirstName));
        return patientsByFirstName;
    }
    public List<PatientTable> searchPatientsByByLastName(String keyword){
        List<PatientTable> patients = fetchPatients();
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        final String lowerCaseKeyword = keyword.trim().toLowerCase();
        List<PatientTable> patientsByLastName= patientRepository.getPatientByLastName(keyword);
        Collections.sort(patientsByLastName, Comparator.comparing(PatientTable::getLastName));
        return patientsByLastName;
    }
    public Map<String, Object> getPatientAgeStatistics(){
        List<PatientTable> patients= fetchPatients();
        Map<String, Object> results = new HashMap<>();
        IntSummaryStatistics stats = patients.stream().mapToInt(patient -> Integer.parseInt(patient.getAge())).summaryStatistics();
        if (stats.getCount() > 0) {
            results.put("minAge", stats.getMin());
            results.put("maxAge", stats.getMax());
            results.put("averageAge", stats.getAverage());
        } else {
            results.put("minAge", 0);
            results.put("maxAge", 0);
            results.put("averageAge", 0.0);
        }
        results.put("totalPatients", stats.getCount());
        return results;
    }
    public void approvePatientRegistration(String id) throws Exception {
        PatientTable patient = patientRepository.findById(id) .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + id));
        if(patient == null){
            throw new Exception("patient doesn't exist");
        }
        patient.setApproved(true);
    }
}
