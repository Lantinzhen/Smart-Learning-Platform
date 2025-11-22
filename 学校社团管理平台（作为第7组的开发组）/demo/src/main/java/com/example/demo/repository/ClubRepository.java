package com.example.demo.repository;

import com.example.demo.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ClubRepository extends JpaRepository<Club, Long> {
    Optional<Club> findByName(String name);
    List<Club> findByStatus(Club.ClubStatus status);
    List<Club> findByType(Club.ClubType type);
    
    @Query("SELECT c FROM Club c WHERE c.status = com.example.demo.entity.Club.ClubStatus.ACTIVE ORDER BY c.memberCount DESC")
    List<Club> findPopularClubs();
    
    @Query("SELECT c FROM Club c WHERE c.status = com.example.demo.entity.Club.ClubStatus.ACTIVE AND (c.name LIKE %:keyword% OR c.description LIKE %:keyword%)")
    List<Club> searchClubs(@Param("keyword") String keyword);
}
