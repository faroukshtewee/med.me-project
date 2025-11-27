package com.example.medmeproject.Controller;

import com.example.medmeproject.Dto.DoctorCreateDto;
import com.example.medmeproject.Model.DoctorTable;
import com.example.medmeproject.Service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctor")
@CrossOrigin
public class DoctorController {
    @Autowired
    DoctorService doctorService;

    @GetMapping("")
    public List<DoctorTable> getAllDoctors(){
        return doctorService.fetchDoctors();
    }

    @PostMapping("")
    public DoctorTable addDoctor(@Valid @RequestBody DoctorCreateDto doctor){
        return doctorService.addDoctor(doctor);

    }
    @DeleteMapping("/delete/{id}")
    public DoctorTable deleteDoctor(@PathVariable String identityCard){
        return doctorService.deleteDoctor(identityCard);
    }
    @GetMapping("/{identityCard}")
    public DoctorTable getDoctorByIdentityCard(@PathVariable String identityCard){
        return doctorService.getDoctorByIdentityCard(identityCard);
    }
    @PostMapping("/update/{id}")
    public DoctorTable updateDoctor(@PathVariable String id,@Valid @RequestBody DoctorCreateDto updateDoctor){
        return doctorService.updateDoctor(id, updateDoctor);
    }
    @GetMapping("/by-specialization-map")
    public Map<String, List<DoctorTable>> getDoctorsBySpecializationMap(){
        return doctorService.getDoctorsBySpecializationMap();
    }
    @GetMapping("/by-years-of-experience-map")
    public Map<Integer, List<DoctorTable>> getDoctorsByYearsOfExperienceMap(){
        return doctorService.getDoctorsByYearsOfExperienceMap();
    }
    @GetMapping("/experience-range")
    public List<DoctorTable> getDoctorsByExperienceRange(@RequestParam int min,@RequestParam int max) {
        return doctorService.getDoctorsByExperienceRange(min, max) ;
    }
    @GetMapping("/most-appointments")
    public List<DoctorTable> getDoctorsWithMostAppointments(@RequestParam int limit) {
        return doctorService.getDoctorsWithMostAppointments(limit);
    }
    @GetMapping("/search-first-name")
    public List<DoctorTable> searchDoctorsByFirstName(@RequestParam String keyword) {
        return doctorService.searchDoctorsByFirstName(keyword);
    }
    @GetMapping("/search-last-name")
    public List<DoctorTable> searchDoctorsByLastName(@RequestParam String keyword) {
        return doctorService.searchDoctorsByLastName(keyword);
    }
    @GetMapping("/search-license-number")
    public List<DoctorTable> searchDoctorsByLicenseNumber(@RequestParam String keyword) {
        return doctorService.searchDoctorsByLicenseNumber(keyword);
    }
    @GetMapping("/specializations")
    public List<String> getSpecializations() {
        return doctorService.getSpecializations();
    }
    @GetMapping("/age-statistics")
    public Map<String, Object> getDoctorAgeStatistics(){
        return doctorService.getDoctorAgeStatistics();
    }
}
