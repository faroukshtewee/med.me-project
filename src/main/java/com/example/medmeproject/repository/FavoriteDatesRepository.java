package com.example.medmeproject.repository;

import com.example.medmeproject.Model.FavoriteDatesTable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface FavoriteDatesRepository extends MongoRepository<FavoriteDatesTable,String> {
    @Query("{ 'date' : ?0, 'time' : ?1 }")
    List<FavoriteDatesTable> getAllFavoriteDates(LocalDate favoriteDate, LocalTime favoriteTime);
}
