package com.example.ratecsstaff.repository;

import com.example.ratecsstaff.model.StaffRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StaffRatingRepository extends JpaRepository<StaffRating, Long> {

    /** Newest ratings first, for the index page. */
    List<StaffRating> findAllByOrderByCreatedAtDesc();
}
