package com.example.demo.repository;

import com.example.demo.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.time.LocalDateTime;
import org.springframework.data.domain.Sort;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    // 只保留基本查询方法
    List<Activity> findByStatus(Activity.ActivityStatus status);
    List<Activity> findByClubId(Long clubId);
    List<Activity> findByType(Activity.ActivityType type);
}
