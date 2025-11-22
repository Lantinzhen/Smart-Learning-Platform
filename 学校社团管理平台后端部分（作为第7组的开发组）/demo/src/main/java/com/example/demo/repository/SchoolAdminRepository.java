package com.example.demo.repository;

import com.example.demo.entity.SchoolAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SchoolAdminRepository extends JpaRepository<SchoolAdmin, Long> {
    Optional<SchoolAdmin> findByUser_Id(Long userId);
}
