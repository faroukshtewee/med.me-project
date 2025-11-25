package com.example.medmeproject.repository;

import com.example.medmeproject.Model.UserTable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<UserTable,String> {
}
