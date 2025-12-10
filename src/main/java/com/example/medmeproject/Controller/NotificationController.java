package com.example.medmeproject.Controller;

import com.example.medmeproject.Dto.NotificationCreateDto;
import com.example.medmeproject.Exception.ResourceNotFoundException;
import com.example.medmeproject.Model.NotificationTable;
import com.example.medmeproject.Service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notification")
@CrossOrigin
public class NotificationController {
    @Autowired
    NotificationService notificationService;
    @GetMapping("")
    public List<NotificationTable> fetchNotifications(){
        return notificationService.fetchNotifications();
    }
    @PostMapping("")
    public NotificationTable addNotification(@Valid @RequestBody NotificationCreateDto notification){
        return notificationService.addNotification(notification);
    }
    @DeleteMapping("/delete/{id}")
    public void deleteNotification(@PathVariable String id){
         notificationService.deleteNotification(id);
    }
    @PostMapping("/update/{id}")
    public NotificationTable updateNotification(@PathVariable String id, @Valid @RequestBody NotificationCreateDto updateNotification){
        return notificationService.updateNotification(id,updateNotification);
    }
}
