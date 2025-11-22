package com.example.demo.repository;

import com.example.demo.entity.ClubAdmin;
import com.example.demo.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface ClubAdminRepository extends JpaRepository<ClubAdmin, Long> {
    Optional<ClubAdmin> findByUser_Id(Long userId);
    
    @Query("SELECT ca FROM ClubAdmin ca JOIN ca.clubAdminAssignments caa WHERE caa.club = :club")
    Optional<ClubAdmin> findByClub(@Param("club") Club club);
}
