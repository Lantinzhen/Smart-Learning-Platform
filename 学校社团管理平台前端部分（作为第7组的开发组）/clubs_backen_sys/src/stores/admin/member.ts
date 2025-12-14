import { defineStore } from "pinia";
import { ref, computed } from "vue";
import { get, post, put, del } from "@/utils/request";

export const useAdminMemberStore = defineStore("adminMember", () => {
  // 获取学生列表
  const getStudentList = async (page?: number, pageSize?: number) => {
    const url = `/school-admin/students?page=${page || 1}&pageSize=${pageSize || 10}`;
    const response = await get(url);
    return response.data;
  };

  // 修改学生资料
  const updateStudent = async (profile: object) => {
    const url = `/school-admin/students`;
    const response = await post(url, { ...profile });
    return response.data;
  };

  // 封禁学生
  const deleteStudent = async (id: number) => {
    const url = `/school-admin/students/${id}/status`;
    const response = await put(url, { status: 0 });
    return response.data;
  };

  // 获取社团管理员列表
  const getClubAdminList = async (page?: number, pageSize?: number) => {
    const url = `/school-admin/club-admins?page=${page || 1}&pageSize=${pageSize || 10}`;
    const response = await get(url);
    return response.data;
  };

  // 修改社团管理员资料
  const updateClubAdmin = async (profile: object) => {
    const url = `/school-admin/club-admins`;
    const response = await post(url, { ...profile });
    return response.data;
  };

  // 封禁社团管理员
  const deleteClubAdmin = async (id: number) => {
    const url = `/school-admin/club-admins/${id}/status`;
    const response = await put(url, { status: 0 });
    return response.data;
  };

  return {
    getStudentList,
    updateStudent,
    deleteStudent,
    getClubAdminList,
    updateClubAdmin,
    deleteClubAdmin,
  };
});
