package com.example.medmeproject.Service;

import com.example.medmeproject.Dto.DoctorCreateDto;
import com.example.medmeproject.Dto.TimeSlot;
import com.example.medmeproject.Exception.ResourceNotFoundException;
import com.example.medmeproject.Model.DoctorTable;
import com.example.medmeproject.Model.PatientTable;
import com.example.medmeproject.repository.DoctorRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DoctorService extends UserService {
    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<DoctorTable> fetchDoctors(){
        return doctorRepository.findAll();
    }
    public DoctorTable addDoctor(DoctorCreateDto doctor){
            DoctorTable doctorTable= modelMapper.map(doctor, DoctorTable.class);
            doctorTable.setSpecialization(doctor.getSpecialization().toString());
            doctorTable.setAge(calculateAge(doctor.getBirthDate().toString()).toString());
            doctorRepository.save(doctorTable);
            return doctorTable;
    }
    public DoctorTable deleteDoctor(String identityCard){
            return doctorRepository.deleteDoctorById(identityCard);
    }
    public DoctorTable getDoctorByIdentityCard(String identityCard){
            return doctorRepository.getDoctorByIdentityCard(identityCard);
    }
    public DoctorTable updateDoctor(String id,DoctorCreateDto updateDoctor){
        DoctorTable findDoctor = doctorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + id));
        modelMapper.map(updateDoctor, findDoctor);
        findDoctor.setSpecialization(updateDoctor.getSpecialization().toString());
        findDoctor.setAge(calculateAge(updateDoctor.getBirthDate().toString()).toString());
        doctorRepository.save(findDoctor);
        return findDoctor;
    }
    public Map<String, List<DoctorTable>> getDoctorsBySpecializationMap(){
        List<DoctorTable> allDoctors = fetchDoctors();
        return allDoctors.stream().collect(Collectors.groupingBy(DoctorTable::getSpecialization));
    }
    public Map<Integer, List<DoctorTable>> getDoctorsByYearsOfExperienceMap(){
        List<DoctorTable> allDoctors = fetchDoctors();
        return allDoctors.stream().collect(Collectors.groupingBy(DoctorTable::getYearsOfExperience));
    }
    public List<DoctorTable> getDoctorsByExperienceRange(int minYears, int maxYears){
        List<DoctorTable> doctors = fetchDoctors();
        List<DoctorTable> doctorExperience = doctors.stream().filter(doctorDto -> doctorDto.getYearsOfExperience()>=minYears&&doctorDto.getYearsOfExperience()<=maxYears).collect(Collectors.toList());
        Collections.sort(doctorExperience, Comparator.comparingDouble(DoctorTable::getYearsOfExperience));
        return doctorExperience;
    }
    public List<DoctorTable> getDoctorsWithMostAppointments(int limit){
        List<DoctorTable>doctorsWithMostAppointments= fetchDoctors();
        Collections.sort(doctorsWithMostAppointments, Comparator.comparing((DoctorTable doctor) -> doctor.getAppointments().size()).reversed());
        return doctorsWithMostAppointments.stream().limit(limit).collect(Collectors.toList());
    }
    public List<String> getSpecializations() {
        return Arrays.stream(DoctorCreateDto.Specialization.values()).map(Enum::name).collect(Collectors.toList());
    }
    public List<DoctorTable> searchDoctorsByFirstName(String keyword){
        List<DoctorTable> doctors = fetchDoctors();
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        final String lowerCaseKeyword = keyword.trim().toLowerCase();
        List<DoctorTable> doctorsByFirstName= doctorRepository.getDoctorByFirstName(keyword);
        Collections.sort(doctorsByFirstName, Comparator.comparing(DoctorTable::getFirstName));
        return doctorsByFirstName;
    }
    public List<DoctorTable> searchDoctorsByLastName(String keyword){
        List<DoctorTable> doctors = fetchDoctors();
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        final String lowerCaseKeyword = keyword.trim().toLowerCase();
        List<DoctorTable> doctorsByLastName= doctorRepository.getDoctorByLastName(keyword);
        Collections.sort(doctorsByLastName, Comparator.comparing(DoctorTable::getLastName));
        return doctorsByLastName;
    }
    public List<DoctorTable> searchDoctorsByLicenseNumber(String keyword){
        List<DoctorTable> doctors = fetchDoctors();
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        final String lowerCaseKeyword = keyword.trim().toLowerCase();
        List<DoctorTable> doctorsByLicenseNumber= doctorRepository.getDoctorByLicenseNumber(keyword);
        Collections.sort(doctorsByLicenseNumber, Comparator.comparing(DoctorTable::getLicenseNumber));
        return doctorsByLicenseNumber;
    }
    public Map<String, Object> getDoctorAgeStatistics(){
        List<DoctorTable> doctors= fetchDoctors();
        Map<String, Object> results = new HashMap<>();
        IntSummaryStatistics stats = doctors.stream().mapToInt(doctor -> Integer.parseInt(doctor.getAge())).summaryStatistics();
        if (stats.getCount() > 0) {
            results.put("minAge", stats.getMin());
            results.put("maxAge", stats.getMax());
            results.put("averageAge", stats.getAverage());
        } else {
            results.put("minAge", 0);
            results.put("maxAge", 0);
            results.put("averageAge", 0.0);
        }
        results.put("totalDoctors", stats.getCount());
        return results;
    }
    public DoctorTable fillDoctorSchedule(String doctorId, Map<LocalDate, List<TimeSlot>> newSchedule) {
        DoctorTable doctor = doctorRepository.findById(doctorId).orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + doctorId));
        doctor.setListSchedule(newSchedule);
        return doctorRepository.save(doctor);
    }

}
