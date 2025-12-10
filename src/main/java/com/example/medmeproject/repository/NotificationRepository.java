package com.example.medmeproject.repository;

import com.example.medmeproject.Model.NotificationTable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationRepository  extends MongoRepository<NotificationTable,String> {
}
