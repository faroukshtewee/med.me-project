package com.example.medmeproject.Service;

import com.example.medmeproject.Dto.DoctorCreateDto;
import com.example.medmeproject.Dto.SecretaryCreateDto;
import com.example.medmeproject.Exception.ResourceNotFoundException;
import com.example.medmeproject.Model.SecretaryTable;
import com.example.medmeproject.Model.SecretaryTable;
import com.example.medmeproject.repository.SecretaryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SecretaryService extends UserService{
    @Autowired
    SecretaryRepository secretaryRepository;
    @Autowired
    private ModelMapper modelMapper;

    public List<SecretaryTable> getAllSecretaries(){
        return secretaryRepository.findAll();
    }
    public SecretaryTable createSecretary(SecretaryCreateDto secretary){
        SecretaryTable secretaryTable= modelMapper.map(secretary, SecretaryTable.class);
        secretaryTable.setAge(calculateAge(secretary.getBirthDate().toString()).toString());
        secretaryRepository.save(secretaryTable);
        return secretaryTable;
    }
    public SecretaryTable deleteSecretary(String identityCard){
        return secretaryRepository.deleteSecretaryById(identityCard);
    }
    public SecretaryTable getSecretaryByIdentityCard(String identityCard){
        return secretaryRepository.getSecretaryByIdentityCard(identityCard);
    }
    public SecretaryTable updateSecretary(String id, SecretaryCreateDto editSecretary){
        SecretaryTable findSecretary = secretaryRepository.findById(id) .orElseThrow(() -> new ResourceNotFoundException("Secretary not found with ID: " + id));
        modelMapper.map(editSecretary, findSecretary);
        findSecretary.setAge(calculateAge(editSecretary.getBirthDate().toString()).toString());
        secretaryRepository.save(findSecretary);
        return findSecretary;
    }

    public Map<Integer, List<SecretaryTable>> getSecretariesByYearsOfExperienceMap(){
        List<SecretaryTable> allSecretaries =getAllSecretaries();
        return allSecretaries.stream().collect(Collectors.groupingBy(SecretaryTable::getYearsOfExperience));
    }
    public List<SecretaryTable> getSecretariesByExperienceRange(int minYears, int maxYears){
        List<SecretaryTable> allSecretaries =getAllSecretaries();
        List<SecretaryTable> secretaryExperience = allSecretaries.stream().filter(secretaryDto -> secretaryDto.getYearsOfExperience()>=minYears&&secretaryDto.getYearsOfExperience()<=maxYears).collect(Collectors.toList());
        Collections.sort(secretaryExperience, Comparator.comparingDouble(SecretaryTable::getYearsOfExperience));
        return secretaryExperience;
    }

    public List<SecretaryTable> searchSecretariesByFirstName(String keyword){
        List<SecretaryTable> Secretaries =getAllSecretaries();
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        final String lowerCaseKeyword = keyword.trim().toLowerCase();
        List<SecretaryTable> SecretariesByFirstName= secretaryRepository.getSecretaryByFirstName(keyword);
        Collections.sort(SecretariesByFirstName, Comparator.comparing(SecretaryTable::getFirstName));
        return SecretariesByFirstName;
    }
    public List<SecretaryTable> searchSecretariesByLastName(String keyword){
        List<SecretaryTable> Secretaries =getAllSecretaries();
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        final String lowerCaseKeyword = keyword.trim().toLowerCase();
        List<SecretaryTable> SecretariesByLastName= secretaryRepository.getSecretaryByLastName(keyword);
        Collections.sort(SecretariesByLastName, Comparator.comparing(SecretaryTable::getLastName));
        return SecretariesByLastName;
    }

}
