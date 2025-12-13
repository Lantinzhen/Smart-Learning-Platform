import { defineStore } from "pinia";
import { ref, computed } from "vue";
import { get, post, put, del } from "@/utils/request";

export const useAdminClubStore = defineStore("adminClub", () => {
  // 获取社团
  const getClubList = async (page?: number, pageSize?: number) => {
    const url = `/school-admin/clubs?page=${page || 1}&pageSize=${pageSize || 10}`;
    const response = await get(url);
    return response.data;
  };

  // 修改社团资料
  const updateClub = async (profile: object) => {
    const url = `/school-admin/clubs`;
    const response = await post(url, { ...profile });
    return response.data;
  };

  // 封禁社团
  const deleteClub = async (id: number) => {
    const url = `/school-admin/clubs/${id}/status`;
    const response = await put(url, { status: 0 });
    return response.data;
  };

  // 获取社团分类
  const getClubCategoris = async (page?: number, pageSize?: number) => {
    const url = `/school-admin/club-categories?page=${page || 1}&pageSize=${pageSize || 10}`;
    const response = await get(url);
    return response.data;
  };

  // 修改社团分类
  const updateClubCategoris = async (profile: object) => {
    const url = `/school-admin/club-categories`;
    const response = await post(url, { ...profile });
    return response.data;
  };

  return {
    getClubList,
    updateClub,
    deleteClub,
    getClubCategoris,
    updateClubCategoris,
  };
});
