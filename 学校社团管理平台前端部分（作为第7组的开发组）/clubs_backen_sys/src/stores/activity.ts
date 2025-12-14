import { defineStore } from "pinia";
import { ref, computed } from "vue";
import { get, post, put, del } from "@/utils/request";
import type { ActivityForm, ActivityAttendance, updateActivityForm } from "@/types/activity";

export const useActivityStore = defineStore("activity", () => {
  // 获取社团活动列表
  const getActivityList = async () => {
    const url = `/club-admin/activities`;
    const response = await get(url);
    return response.data;
  };
  // 获取社团活动详情
  const getActivityDetail = async (id: number) => {
    const url = `/club-admin/activities/${id}`;
    const response = await get(url);
    return response.data;
  };

  // 创建社团活动
  const createActivity = async (data: ActivityForm) => {
    const url = `/club-admin/activities`;
    const response = await post(url, data);
    return response.data;
  };
  // 更新活动信息
  const updateActivity = async (id: number, data: updateActivityForm) => {
    const url = `/club-admin/activities/${id}`;
    const response = await put(url, data);
    return response.data;
  };
  // 提交活动审批
  const submitActivity = async (id: number) => {
    const url = `/club-admin/activities/${id}/submit-for-approval`;
    const response = await put(url);
    return response.data;
  };
  // 获取活动报名列表
  const getActivityEnrollList = async (id: number, page?: number, pageSize?: number) => {
    const url = `/club-admin/activities/${id}/registrations?page=${page || 1}&pageSize=${
      pageSize || 10
    }`;
    // console.log("url",url);
    const response = await get(url);
    return response.data;
  };
  // 确认活动参与后发放积分
  const confirmActivityEnroll = async (id: number, data: ActivityAttendance) => {
    const url = `/club-admin/activities/${id}/attendance`;
    const response = await post(url, data);
    return response.data;
  };
  return {
    getActivityList,
    getActivityDetail,
    createActivity,
    updateActivity,
    submitActivity,
    getActivityEnrollList,
    confirmActivityEnroll,
  };
});
