package com.example.demo.service;

import com.example.demo.entity.ClubApplication;
import com.example.demo.entity.Student;
import com.example.demo.repository.ClubApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClubApplicationService {

    @Autowired
    private ClubApplicationRepository clubApplicationRepository;

    @Transactional(readOnly = true)
    public long countJoinedClubs(Student student) {
        return clubApplicationRepository.findByStudentAndStatus(student, ClubApplication.ApplicationStatus.APPROVED).size();
    }

    @Transactional(readOnly = true)
    public long countPendingApplications(Student student) {
        return clubApplicationRepository.findByStudentAndStatus(student, ClubApplication.ApplicationStatus.PENDING).size();
    }
}
