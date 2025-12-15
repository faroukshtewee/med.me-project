package com.example.medmeproject.repository;

import com.example.medmeproject.Model.ClinicTable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClinicRepository extends MongoRepository<ClinicTable,String> {
    ClinicTable findClinicByClinicAddress(String clinicAddress);
    ClinicTable findClinicByClinicPhoneNumber(String clinicPhoneNumber);
}
