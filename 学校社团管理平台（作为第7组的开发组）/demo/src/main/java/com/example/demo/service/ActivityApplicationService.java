package com.example.demo.service;

import com.example.demo.entity.ActivityApplication;
import com.example.demo.entity.Student;
import com.example.demo.repository.ActivityApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActivityApplicationService {

    @Autowired
    private ActivityApplicationRepository activityApplicationRepository;

    @Transactional(readOnly = true)
    public long countParticipatedActivities(Student student) {
        return activityApplicationRepository.findByStudentAndStatus(student, ActivityApplication.ApplicationStatus.APPROVED).size();
    }

    @Transactional(readOnly = true)
    public long countPendingApplications(Student student) {
        return activityApplicationRepository.findByStudentAndStatus(student, ActivityApplication.ApplicationStatus.PENDING).size();
    }
}
