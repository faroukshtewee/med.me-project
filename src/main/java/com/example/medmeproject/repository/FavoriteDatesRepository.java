package com.example.medmeproject.repository;

import com.example.medmeproject.Model.FavoriteDatesTable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface FavoriteDatesRepository extends MongoRepository<FavoriteDatesTable,String> {
    List<FavoriteDatesTable> getAllFavoriteDates(LocalDate favoriteDate, LocalTime favoriteTime);
}
