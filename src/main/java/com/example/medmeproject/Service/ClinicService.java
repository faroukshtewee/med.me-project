package com.example.medmeproject.Service;

import com.example.medmeproject.Model.ClinicTable;
import com.example.medmeproject.repository.ClinicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClinicService {
    @Autowired
    ClinicRepository clinicRepository;

    public ClinicTable searchClinicByAddress(String clinicAddress){
        return clinicRepository.findClinicByAddress(clinicAddress);
    }
    public ClinicTable searchClinicByPhoneNumber(String clinicPhoneNumber){
        return clinicRepository.findClinicByPhoneNumber(clinicPhoneNumber);
    }
}
