package com.example.demo.controller.clubadmin;

import com.example.demo.common.BusinessException;
import com.example.demo.common.Response;
import com.example.demo.dto.admin.ClubMemberDTO;
import com.example.demo.entity.ClubAdmin;
import com.example.demo.entity.ClubMember;
import com.example.demo.entity.Student;
import com.example.demo.repository.ClubAdminRepository;
import com.example.demo.repository.ClubMemberRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.service.admin.ClubMemberService;
import com.example.demo.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 社团成员管理控制器
 */
@RestController
@RequestMapping("/api/v1/club-admin/members")
public class ClubMemberController {

    @Autowired
    private ClubAdminRepository clubAdminRepository;

    @Autowired
    private ClubMemberRepository clubMemberRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ClubMemberService clubMemberService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 获取社团成员列表
     * 
     * @param authHeader Authorization头
     * @param keyword 搜索关键词
     * @param role 成员角色
     * @param page 页码
     * @param pageSize 每页数量
     * @return 成员列表
     */
    @GetMapping
    public Response<Page<ClubMemberDTO>> getMembers(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {

        // 解析token获取管理员信息
        ClubAdmin clubAdmin = getClubAdminFromToken(authHeader);

        // 构建分页请求
        Pageable pageable = PageRequest.of(page - 1, pageSize);

        // 查询该社团的所有成员关系（物理删除后不需要status过滤）
        List<ClubMember> clubMembers = clubMemberRepository.findByClubId(Integer.valueOf(clubAdmin.getClubId()))
                .stream()
                .collect(Collectors.toList());

        // 过滤数据
        List<ClubMemberDTO> filteredMembers = clubMembers.stream()
                .map(this::convertToDTO)
                .filter(dto -> {
                    // 关键词过滤
                    if (keyword != null && !keyword.isEmpty()) {
                        boolean matchKeyword = (dto.getName() != null && dto.getName().contains(keyword)) ||
                                (dto.getStudentId() != null && dto.getStudentId().contains(keyword)) ||
                                (dto.getMajor() != null && dto.getMajor().contains(keyword));
                        if (!matchKeyword) return false;
                    }

                    // 角色过滤
                    if (role != null && !role.isEmpty() && !role.equals(dto.getRole())) {
                        return false;
                    }

                    return true;
                })
                .collect(Collectors.toList());

        // 手动分页
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredMembers.size());
        List<ClubMemberDTO> pagedMembers = filteredMembers.subList(start, end);
        Page<ClubMemberDTO> resultPage = new PageImpl<>(pagedMembers, pageable, filteredMembers.size());

        return Response.success(resultPage);
    }

    /**
     * 修改成员角色
     * 
     * @param authHeader Authorization头
     * @param id 成员关系ID
     * @param role 新角色
     * @return 响应结果
     */
    @PutMapping("/{id}/role")
    public Response<Void> updateMemberRole(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Integer id,
            @RequestBody RoleUpdateRequest request) {

        // 解析token获取管理员信息
        ClubAdmin clubAdmin = getClubAdminFromToken(authHeader);

        // 查找成员关系
        ClubMember clubMember = clubMemberRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "成员关系不存在"));
        
        // 物理删除后不再需要检查成员状态（不存在已退出但记录仍保留的情况）
        // 直接进行权限验证

        // 验证成员是否属于当前管理员的社团
        if (!clubMember.getClubId().equals(Integer.valueOf(clubAdmin.getClubId()))) {
            throw new BusinessException(403, "无权限操作此成员");
        }

        // 更新角色
        clubMember.setRole(request.getRole());
        clubMemberRepository.save(clubMember);

        return Response.success(null);
    }

    /**
     * 移除社团成员
     * 
     * @param authHeader Authorization头
     * @param id 成员关系ID
     * @return 响应结果
     */
    @DeleteMapping("/{id}")
    public Response<Void> removeMember(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Integer id) {

        // 解析token获取管理员信息
        ClubAdmin clubAdmin = getClubAdminFromToken(authHeader);

        // 查找成员关系
        ClubMember clubMember = clubMemberRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "成员关系不存在"));

        // 验证成员是否属于当前管理员的社团
        if (!clubMember.getClubId().equals(Integer.valueOf(clubAdmin.getClubId()))) {
            throw new BusinessException(403, "无权限操作此成员");
        }

        // 调用服务层方法移除成员
        boolean success = clubMemberService.removeMember(id);
        if (!success) {
            throw new BusinessException(500, "移除成员失败");
        }

        return Response.success(null);
    }

    /**
     * 从token中获取社团管理员信息
     * 
     * @param authHeader Authorization头
     * @return 社团管理员实体
     */
    private ClubAdmin getClubAdminFromToken(String authHeader) {
        // 解析Authorization头，获取token
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.replace("Bearer ", "");
        } else if (authHeader != null) {
            token = authHeader;
        }

        // 检查token是否为空
        if (token == null) {
            throw new BusinessException(401, "缺少有效token");
        }

        // 从token中获取管理员ID
        String adminId = jwtUtil.getUserIdFromToken(token);
        if (adminId == null || adminId.isEmpty()) {
            throw new BusinessException(401, "无效的token，无法获取管理员ID");
        }

        // 验证管理员存在
        ClubAdmin clubAdmin = clubAdminRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException(404, "社团管理员不存在"));

        return clubAdmin;
    }

    /**
     * 将ClubMember实体转换为ClubMemberDTO
     * 
     * @param clubMember 社团成员关系实体
     * @return ClubMemberDTO
     */
    private ClubMemberDTO convertToDTO(ClubMember clubMember) {
        ClubMemberDTO dto = new ClubMemberDTO();
        dto.setId(clubMember.getId());
        dto.setStudentId(clubMember.getStudentId());
        dto.setRole(clubMember.getRole());
        dto.setJoinDate(clubMember.getJoinDate());
        dto.setStatus(clubMember.getStatus());

        // 获取学生信息
        Student student = studentRepository.findByStudentId(clubMember.getStudentId());
        if (student != null) {
            dto.setName(student.getName());
            dto.setMajor(student.getMajor());
            dto.setGrade(student.getGrade());
        }

        return dto;
    }

    /**
     * 角色更新请求DTO
     */
    public static class RoleUpdateRequest {
        private String role;

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }
}