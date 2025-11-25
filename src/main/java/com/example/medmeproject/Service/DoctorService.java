package com.example.medmeproject.Service;

import com.example.medmeproject.Dto.DoctorCreateDto;
import com.example.medmeproject.Model.DoctorTable;
import com.example.medmeproject.repository.DoctorRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DoctorService extends UserService {
    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<DoctorTable> getAllDoctors(){
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
        DoctorTable findDoctor = doctorRepository.findById(id).orElse(null);
        modelMapper.map(updateDoctor, findDoctor);
        findDoctor.setSpecialization(updateDoctor.getSpecialization().toString());
        findDoctor.setAge(calculateAge(updateDoctor.getBirthDate().toString()).toString());
        doctorRepository.save(findDoctor);
        return findDoctor;
    }
    public Map<String, List<DoctorTable>> getDoctorsBySpecializationMap(){
        List<DoctorTable> allDoctors =getAllDoctors();
        return allDoctors.stream().collect(Collectors.groupingBy(DoctorTable::getSpecialization));
    }
    public Map<Integer, List<DoctorTable>> getDoctorsByYearsOfExperienceMap(){
        List<DoctorTable> allDoctors =getAllDoctors();
        return allDoctors.stream().collect(Collectors.groupingBy(DoctorTable::getYearsOfExperience));
    }
    public List<DoctorTable> getDoctorsByExperienceRange(int minYears, int maxYears){
        List<DoctorTable> doctors =getAllDoctors();
        List<DoctorTable> doctorExperience = doctors.stream().filter(doctorDto -> doctorDto.getYearsOfExperience()>=minYears&&doctorDto.getYearsOfExperience()<=maxYears).collect(Collectors.toList());
        Collections.sort(doctorExperience, Comparator.comparingDouble(DoctorTable::getYearsOfExperience));
        return doctorExperience;
    }
    public List<DoctorTable> getDoctorsWithMostAppointments(int limit){
        List<DoctorTable>doctorsWithMostAppointments=getAllDoctors();
        Collections.sort(doctorsWithMostAppointments, Comparator.comparing((DoctorTable doctor) -> doctor.getAppointments().size()).reversed());
        return doctorsWithMostAppointments.stream().limit(limit).collect(Collectors.toList());
    }
    public List<String> getSpecializations() {
        return Arrays.stream(DoctorCreateDto.Specialization.values()).map(Enum::name).collect(Collectors.toList());
    }
    public List<DoctorTable> searchDoctorsByFirstName(String keyword){
        List<DoctorTable> doctors =getAllDoctors();
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        final String lowerCaseKeyword = keyword.trim().toLowerCase();
        List<DoctorTable> doctorsByFirstName= doctorRepository.getDoctorByFirstName(keyword);
        Collections.sort(doctorsByFirstName, Comparator.comparing(DoctorTable::getFirstName));
        return doctorsByFirstName;
    }
    public List<DoctorTable> searchDoctorsByLastName(String keyword){
        List<DoctorTable> doctors =getAllDoctors();
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        final String lowerCaseKeyword = keyword.trim().toLowerCase();
        List<DoctorTable> doctorsByLastName= doctorRepository.getDoctorByLastName(keyword);
        Collections.sort(doctorsByLastName, Comparator.comparing(DoctorTable::getLastName));
        return doctorsByLastName;
    }
    public List<DoctorTable> searchDoctorsByLicenseNumber(String keyword){
        List<DoctorTable> doctors =getAllDoctors();
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        final String lowerCaseKeyword = keyword.trim().toLowerCase();
        List<DoctorTable> doctorsByLicenseNumber= doctorRepository.getDoctorByLicenseNumber(keyword);
        Collections.sort(doctorsByLicenseNumber, Comparator.comparing(DoctorTable::getLicenseNumber));
        return doctorsByLicenseNumber;
    }

}
