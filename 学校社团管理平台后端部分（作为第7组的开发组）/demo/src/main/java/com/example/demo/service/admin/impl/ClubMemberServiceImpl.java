package com.example.demo.service.admin.impl;

import com.example.demo.dto.admin.ClubMemberDTO;
import com.example.demo.entity.ClubMember;
import com.example.demo.entity.Student;
import com.example.demo.repository.ClubMemberRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.service.admin.ClubMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 社团成员管理服务实现类
 */
@Service
public class ClubMemberServiceImpl implements ClubMemberService {
    
    @Autowired
    private ClubMemberRepository clubMemberRepository;
    
    @Autowired
    private StudentRepository studentRepository;

    /**
     * 获取社团成员列表
     * @param clubId 社团ID
     * @param keyword 搜索关键词（可选）
     * @param role 角色筛选（可选）
     * @param pageable 分页参数
     * @return 成员列表
     */
    @Override
    public Page<ClubMemberDTO> getClubMembers(Integer clubId, String keyword, String role, Pageable pageable) {
        // 获取社团成员列表
        List<ClubMember> members;
        
        // 根据是否有筛选条件选择不同的查询方法
        if (keyword != null && !keyword.isEmpty()) {
            // 有关键词搜索（物理删除后不需要status过滤）
            members = clubMemberRepository.findByClubIdAndStudentNameContaining(clubId, keyword)
                    .stream()
                    .collect(Collectors.toList());
        } else if (role != null && !role.isEmpty()) {
            // 有角色筛选（物理删除后不需要status过滤）
            members = clubMemberRepository.findByClubIdAndRole(clubId, role)
                    .stream()
                    .collect(Collectors.toList());
        } else {
            // 无筛选条件（物理删除后不需要status过滤）
            members = clubMemberRepository.findByClubId(clubId)
                    .stream()
                    .collect(Collectors.toList());
        }
        
        // 转换为DTO列表
        List<ClubMemberDTO> memberDTOs = members.stream().map(member -> {
            ClubMemberDTO dto = new ClubMemberDTO();
            dto.setId(member.getId());
            dto.setStudentId(member.getStudentId());
            dto.setRole(member.getRole());
            dto.setJoinDate(member.getJoinDate());
            dto.setStatus(member.getStatus());
            
            // 获取学生信息
            Student student = studentRepository.findByStudentId(member.getStudentId());
            if (student != null) {
                dto.setName(student.getName());
                dto.setMajor(student.getMajor());
                dto.setGrade(student.getGrade());
            }
            
            return dto;
        }).collect(Collectors.toList());
        
        // 分页处理
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), memberDTOs.size());
        
        // 返回分页结果
        return new PageImpl<>(
            memberDTOs.subList(start, end),
            pageable,
            memberDTOs.size()
        );
    }

    /**
     * 修改成员角色
     * @param memberId 成员ID
     * @param role 新角色
     * @return 是否修改成功
     */
    @Override
    public boolean updateMemberRole(Integer memberId, String role) {
        // 查找成员（物理删除后，只需检查成员是否存在即可）
        ClubMember member = clubMemberRepository.findById(memberId).orElse(null);
        if (member == null) {
            return false; // 成员不存在
        }
        
        // 更新角色
        member.setRole(role);
        clubMemberRepository.save(member);
        return true;
    }

    /**
     * 移除社团成员
     * @param memberId 成员ID
     * @return 是否移除成功
     */
    @Override
    public boolean removeMember(Integer memberId) {
        // 查找成员
        ClubMember member = clubMemberRepository.findById(memberId).orElse(null);
        if (member == null) {
            return false;
        }
        
        // 物理硬删除，直接从数据库中删除记录
        clubMemberRepository.delete(member);
        return true;
    }
}