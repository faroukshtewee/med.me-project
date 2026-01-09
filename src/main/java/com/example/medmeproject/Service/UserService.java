package com.example.medmeproject.Service;
import com.example.medmeproject.Dto.AuthUtils;
import com.example.medmeproject.Model.DoctorTable;
import com.example.medmeproject.Model.PatientTable;
import com.example.medmeproject.Model.SecretaryTable;
import com.example.medmeproject.repository.DoctorRepository;
import com.example.medmeproject.repository.PatientRepository;
import com.example.medmeproject.repository.SecretaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
public class UserService {
    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    SecretaryRepository secretaryRepository;

    @Autowired
    SmsService smsService;

    //to remember the user in this session/logged in
    private String userSession="315123456";

    //function to login user
    public boolean logIn(String identityCard){
        String []splitted=identityCard.split("-");
        String phoneNumber="";
        boolean doc=false;
        boolean sec=false;
        boolean pat=false;
        String authCode = AuthUtils.generateAuthCode();
        DoctorTable doctor = null;
        SecretaryTable secretary = null;
        PatientTable patient = null;
        switch(splitted[0]) {
            case "Dr":
                doctor= doctorRepository.getDoctorByIdentityCard(splitted[1]);
                if (doctor!=null){
                    phoneNumber=doctor.getPhoneNumber();
                    doc=true;
                }

                break;
            case "Sec":
                 secretary =secretaryRepository.getSecretaryByIdentityCard(splitted[1]);
                if (secretary!=null){
                    phoneNumber=secretary.getPhoneNumber();
                    sec=true;
                }

                break;
            default:
                 patient=patientRepository.getPatientByIdentityCard(splitted[0]);
                if(patient!=null && patient.isApproved()){
                    phoneNumber=patient.getPhoneNumber();
                    pat=true;
                }

        }
        if (phoneNumber == "" || phoneNumber.isEmpty()) {
            return false;
        }
        else {
            if(doc){
                doctor.setAuthCode(authCode);
                doctorRepository.save(doctor);
            }
            else if(sec){
                secretary.setAuthCode(authCode);
                secretaryRepository.save(secretary);
            }
            else{
                patient.setAuthCode(authCode);
                patientRepository.save(patient);
            }
            if(splitted.length == 1){
                smsService.sendSms(splitted[0], authCode, phoneNumber);
            }
            else{
                smsService.sendSms(splitted[1], authCode, phoneNumber);
            }
        }
        return true;
    }
    //if identityCard starts with Dr search in doctorTable,if starts with Sec search in SecretaryTable else search in patientTable
    //and check the authcode in table if equal to the inserted authcode
    public boolean checkAuthCode(String identityCard, String code, String phoneNumber) {
        String[] splitted = identityCard.split("-");
        DoctorTable doctor = null;
        SecretaryTable secretary = null;
        PatientTable patient = null;
        String authCode = "";
        switch (splitted[0]) {
            case "Dr":
                doctor = doctorRepository.getDoctorByIdentityCard(splitted[1]);
                if (doctor != null) {
                    authCode = doctor.getAuthCode();
                    if (authCode.equals(code)) {
                        return true;
                    }
                }
                break;
            case "Sec":
                secretary = secretaryRepository.getSecretaryByIdentityCard(splitted[1]);
                if (secretary != null) {
                    authCode = secretary.getAuthCode();
                    if (authCode.equals(code)) {
                        return true;
                    }
                }
                break;
            default:
                patient = patientRepository.getPatientByIdentityCard(splitted[0]);
                if (patient != null && patient.isApproved()) {
                    authCode = patient.getAuthCode();
                    if (authCode.equals(code)) {
                        userSession=splitted[0];
                        return true;
                    }
                }
                break;
        }
        return false;
    }
    public Integer calculateAge(String dateOfBirth) {
        LocalDate currentDate=LocalDate.now();
        LocalDate localdateOfBirth = LocalDate.parse(dateOfBirth);;
        Period period = Period.between(localdateOfBirth, currentDate);
        return  period.getYears();
    }

    public String getUserSession() {
        return userSession;
    }

    public void setUserSession(String userSession) {
        this.userSession = userSession;
    }
}
