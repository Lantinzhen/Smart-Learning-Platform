package com.example.demo.repository;

import com.example.demo.entity.ClubApplication;
import com.example.demo.entity.Student;
import com.example.demo.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ClubApplicationRepository extends JpaRepository<ClubApplication, Long> {
    Optional<ClubApplication> findByStudentAndClub(Student student, Club club);
    List<ClubApplication> findByStudent(Student student);
    List<ClubApplication> findByClub(Club club);
    List<ClubApplication> findByStatus(ClubApplication.ApplicationStatus status);
    List<ClubApplication> findByClubAndStatus(Club club, ClubApplication.ApplicationStatus status);
    List<ClubApplication> findByStudentAndStatus(Student student, ClubApplication.ApplicationStatus status);
}
