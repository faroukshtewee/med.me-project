package com.example.medmeproject.Controller;

import com.example.medmeproject.Dto.SecretaryCreateDto;
import com.example.medmeproject.Model.DoctorTable;
import com.example.medmeproject.Model.SecretaryTable;
import com.example.medmeproject.Service.SecretaryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/secretary")
@CrossOrigin
public class SecretaryController {
    @Autowired
    SecretaryService secretaryService;

    @GetMapping("")
    public List<SecretaryTable> getAllSecretaries(){
        return secretaryService.getAllSecretaries();
    }

    @PostMapping("")
    public SecretaryTable createSecretary(@Valid @RequestBody SecretaryCreateDto secretary){
        return secretaryService.createSecretary(secretary);

    }

    @DeleteMapping("/delete/{id}")
    public SecretaryTable deleteSecretary(@PathVariable String identityCard){
        return secretaryService.deleteSecretary(identityCard);

    }

    @GetMapping("/{identityCard}")
    public SecretaryTable getSecretaryByIdentityCard(@PathVariable String identityCard){
        return secretaryService.getSecretaryByIdentityCard(identityCard);

    }

    @PostMapping("/update/{id}")
    public SecretaryTable updateSecretary(@PathVariable String id, @Valid @RequestBody SecretaryCreateDto editSecretary){
        return secretaryService.updateSecretary(id,editSecretary);
    }

    @GetMapping("/by-years-of-experience-map")
    public Map<Integer, List<SecretaryTable>> getSecretariesByYearsOfExperienceMap(){
        return secretaryService.getSecretariesByYearsOfExperienceMap();
    }

    @GetMapping("/experience-range")
    public List<SecretaryTable> getSecretariesByExperienceRange(@RequestParam int min,@RequestParam int max) {
        return secretaryService.getSecretariesByExperienceRange(min, max) ;
    }

    @GetMapping("/search-first-name")
    public List<SecretaryTable> searchSecretariesByFirstName(@RequestParam String keyword) {
        return secretaryService.searchSecretariesByFirstName(keyword);
    }

    @GetMapping("/search-last-name")
    public List<SecretaryTable> searchSecretariesByLastName(@RequestParam String keyword) {
        return secretaryService.searchSecretariesByLastName(keyword);
    }
}
