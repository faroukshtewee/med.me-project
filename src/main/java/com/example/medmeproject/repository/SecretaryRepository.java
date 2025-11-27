package com.example.medmeproject.repository;

import com.example.medmeproject.Model.DoctorTable;
import com.example.medmeproject.Model.SecretaryTable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SecretaryRepository extends MongoRepository<SecretaryTable,String> {
    SecretaryTable deleteSecretaryById(String identityCard);

    SecretaryTable getSecretaryByIdentityCard(String identityCard);

    List<SecretaryTable> getSecretaryByFirstName(String firstName);

    List<SecretaryTable> getSecretaryByLastName(String lastName);
}
