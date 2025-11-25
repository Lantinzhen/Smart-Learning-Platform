package com.example.demo.service.admin;

import com.example.demo.dto.admin.ClubMemberDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


/**
 * 社团成员管理服务接口
 */
public interface ClubMemberService {
    /**
     * 获取社团成员列表
     * @param clubId 社团ID
     * @param keyword 搜索关键词（可选）
     * @param role 角色筛选（可选）
     * @param pageable 分页参数
     * @return 成员列表
     */
    Page<ClubMemberDTO> getClubMembers(Integer clubId, String keyword, String role, Pageable pageable);

    /**
     * 修改成员角色
     * @param memberId 成员ID
     * @param role 新角色
     * @return 是否修改成功
     */
    boolean updateMemberRole(Integer memberId, String role);

    /**
     * 移除社团成员
     * @param memberId 成员ID
     * @return 是否移除成功
     */
    boolean removeMember(Integer memberId);
}