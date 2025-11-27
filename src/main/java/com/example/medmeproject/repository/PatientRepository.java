package com.example.medmeproject.repository;

import com.example.medmeproject.Model.PatientTable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PatientRepository extends MongoRepository<PatientTable,String> {
    PatientTable deletePatientById(String identityCard);

    PatientTable getPatientByIdentityCard(String identityCard);

    List<PatientTable> getPatientByFirstName(String firstName);

    List<PatientTable> getPatientByLastName(String lastName);
}
