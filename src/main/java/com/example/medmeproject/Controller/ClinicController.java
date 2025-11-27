package com.example.medmeproject.Controller;

import com.example.medmeproject.Model.ClinicTable;
import com.example.medmeproject.Service.ClinicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clinic")
@CrossOrigin
public class ClinicController {
    @Autowired
    ClinicService ClinicService;
    @GetMapping("/{address}")
    public ClinicTable searchClinicByAddress(@PathVariable String clinicAddress){
        return ClinicService.searchClinicByAddress(clinicAddress);
    }
    @GetMapping("/{phone-number}")
    public ClinicTable searchClinicByPhoneNumber(@PathVariable String clinicPhoneNumber){
        return ClinicService.searchClinicByPhoneNumber(clinicPhoneNumber);
    }
}
