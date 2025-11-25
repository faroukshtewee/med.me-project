package com.example.medmeproject.repository;

import com.example.medmeproject.Model.AppointmentTable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AppointmentRepository extends MongoRepository<AppointmentTable,String> {
}
