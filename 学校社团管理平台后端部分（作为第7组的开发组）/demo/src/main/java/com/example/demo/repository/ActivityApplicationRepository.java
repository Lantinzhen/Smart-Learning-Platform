package com.example.demo.repository;

import com.example.demo.entity.ActivityApplication;
import com.example.demo.entity.Student;
import com.example.demo.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ActivityApplicationRepository extends JpaRepository<ActivityApplication, Long> {
    Optional<ActivityApplication> findByStudentAndActivity(Student student, Activity activity);
    List<ActivityApplication> findByStudent(Student student);
    List<ActivityApplication> findByActivity(Activity activity);
    List<ActivityApplication> findByStatus(ActivityApplication.ApplicationStatus status);
    List<ActivityApplication> findByActivityAndStatus(Activity activity, ActivityApplication.ApplicationStatus status);
    List<ActivityApplication> findByStudentAndStatus(Student student, ActivityApplication.ApplicationStatus status);
}
