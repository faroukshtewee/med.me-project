package com.example.medmeproject.repository;

import com.example.medmeproject.Dto.DoctorCreateDto;
import com.example.medmeproject.Model.DoctorTable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DoctorRepository extends MongoRepository<DoctorTable,String> {
     DoctorTable deleteDoctorById(String identityCard);
     DoctorTable getDoctorByIdentityCard(String identityCard);
    List<DoctorTable> getDoctorByFirstName(String firstName);
    List<DoctorTable> getDoctorByLastName(String lastName);
    List<DoctorTable> getDoctorByLicenseNumber(String licenseNumber);

    }
