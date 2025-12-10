package com.example.medmeproject.Service;

import com.example.medmeproject.Dto.DoctorCreateDto;
import com.example.medmeproject.Dto.NotificationCreateDto;
import com.example.medmeproject.Exception.ResourceNotFoundException;
import com.example.medmeproject.Model.DoctorTable;
import com.example.medmeproject.Model.NotificationTable;
import com.example.medmeproject.repository.NotificationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    @Autowired
    NotificationRepository notificationRepository;
    @Autowired
    private ModelMapper modelMapper;
    public List<NotificationTable> fetchNotifications(){
        return notificationRepository.findAll();
    }
    public NotificationTable addNotification(NotificationCreateDto notification){
        NotificationTable notificationTable= modelMapper.map(notification, NotificationTable.class);
        notificationRepository.save(notificationTable);
        return notificationTable;
    }
    public void deleteNotification(String id){
         notificationRepository.deleteById(id);
    }
    public NotificationTable updateNotification(String id, NotificationCreateDto updateNotification){
        NotificationTable findNotification = notificationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Notification not found with ID: " + id));
        modelMapper.map(updateNotification, findNotification);
        notificationRepository.save(findNotification);
        return findNotification;
    }
}
