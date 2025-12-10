package com.example.medmeproject.Controller;

import com.example.medmeproject.Dto.TimeSlot;
import com.example.medmeproject.Model.ClinicTable;
import com.example.medmeproject.Service.ClinicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
    @PostMapping("/{fill-schedule}")
    public ClinicTable fillListClinicSchedule(@RequestParam  String id,@RequestParam List<Map<LocalDate,List<TimeSlot>>>  schedule) {
        return ClinicService.fillListClinicSchedule(id, schedule);
    }
}
