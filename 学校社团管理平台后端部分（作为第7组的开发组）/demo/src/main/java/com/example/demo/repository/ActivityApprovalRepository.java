package com.example.demo.repository;

import com.example.demo.entity.ActivityApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 活动审批记录仓库接口
 */
@Repository
public interface ActivityApprovalRepository extends JpaRepository<ActivityApproval, Integer> {
    
    /**
     * 根据活动ID查找审批记录
     */
    ActivityApproval findByActivityId(Integer activityId);
    
    /**
     * 根据审批状态查找审批记录
     */
    List<ActivityApproval> findByStatus(String status);
    
    /**
     * 根据审批人ID查找审批记录
     */
    List<ActivityApproval> findByApproverId(String approverId);
}
