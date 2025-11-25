package com.example.medmeproject.Service;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
public class UserService {
    //function to login user
    public boolean logIn(){

    return true ;
    }
    public Integer calculateAge(String dateOfBirth) {
        LocalDate currentDate=LocalDate.now();
        LocalDate localdateOfBirth = LocalDate.parse(dateOfBirth);;
        Period period = Period.between(localdateOfBirth, currentDate);
        return  period.getYears();
    }
}
