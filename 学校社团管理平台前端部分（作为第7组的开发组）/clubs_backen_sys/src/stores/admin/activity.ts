import { defineStore } from "pinia";
import { ref, computed } from "vue";
import { get, post, put, del } from "@/utils/request";

export const useAdminActivityStore = defineStore("adminActivity", () => {
  // 获取待审批活动列表
  const getPendingApprovalActivities = async (params?: {
    club_id?: number;
    keyword?: string;
    page?: number;
    page_size?: number;
  }) => {
    const queryParams = new URLSearchParams();
    if (params?.club_id) queryParams.append("club_id", params.club_id.toString());
    if (params?.keyword) queryParams.append("keyword", params.keyword);
    queryParams.append("page", (params?.page || 1).toString());
    queryParams.append("page_size", (params?.page_size || 10).toString());

    const url = `/school-admin/activities/pending-approval?${queryParams.toString()}`;
    const response = await get(url);
    return response.data.data;
  };

  // 获取活动详情用于审批
  const getActivityApprovalDetail = async (activityId: number) => {
    const url = `/school-admin/activities/${activityId}/approval`;
    const response = await get(url);
    return response.data;
  };

  // 审批活动
  const approveActivity = async (
    activityId: number,
    data: { status: string; comments?: string }
  ) => {
    const url = `/school-admin/activities/${activityId}/approval`;
    const response = await put(url, data);
    return response.data;
  };

  // 获取活动分类列表
  const getActivityCategories = async () => {
    const url = `/school-admin/activity-categories`;
    const response = await get(url);
    console.log("获取活动分类 store", response.data.data);
    return response.data.data;
  };

  // 创建/更新活动分类
  const updateActivityCategories = async (profile: {
    category_id?: number;
    name: string;
    description: string;
  }) => {
    const url = `/school-admin/activity-categories`;
    const response = await post(url, { ...profile });
    return response.data;
  };

  return {
    getPendingApprovalActivities,
    getActivityApprovalDetail,
    approveActivity,
    getActivityCategories,
    updateActivityCategories,
  };
});
