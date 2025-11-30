package com.example.medmeproject.Controller;

import com.example.medmeproject.Service.SmsService;
import com.example.medmeproject.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000") // IMPORTANT for local React development
public class UserController {

    @Autowired
    private SmsService smsService;

    @Autowired
    private UserService userService;

    // --- 1. SSE Connection Endpoint (Used by React's EventSource) ---
    @GetMapping(path = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@RequestParam String identityCard) {
        //identityCard types:
        //1)Dr-123456789 2)Sec-123456789 3)123456789
        // You would typically use the authenticated user's ID here, but for this demo,
        // we'll use the identityCard from the request parameter.
        String []splitted=identityCard.split("-");
        String sseKey;
        if(splitted.length == 1){
            //if patient
            sseKey = splitted[0];        }
        //if doctor or secretary
        else{
            sseKey = splitted[1];
        }
        return smsService.addEmitter(sseKey);
    }
    @PostMapping("/login")
    public boolean logIn(@RequestBody String identityCard) {
        return userService.logIn(identityCard);
    }

    @PostMapping("/check-auth")
    public boolean checkAuthCode(@RequestParam String identityCard,@RequestParam  String code,@RequestParam String phoneNumber){
        return userService.checkAuthCode(identityCard, code,phoneNumber);
    }


}