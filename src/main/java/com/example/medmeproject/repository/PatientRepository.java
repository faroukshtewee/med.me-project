package com.example.medmeproject.repository;

import com.example.medmeproject.Model.PatientTable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PatientRepository extends MongoRepository<PatientTable,String> {
}
