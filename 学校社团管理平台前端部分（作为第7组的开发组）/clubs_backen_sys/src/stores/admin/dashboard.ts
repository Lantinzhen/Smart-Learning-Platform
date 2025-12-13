import { defineStore } from "pinia";
import { get } from "@/utils/request";

export const useAdminDashboardStore = defineStore("adminDashboard", () => {
  // 获取社团活跃度统计
  const getClubActivityStatistics = async (params?: {
    start_date?: string;
    end_date?: string;
    category_id?: number;
  }) => {
    const queryParams = new URLSearchParams();
    if (params?.start_date) queryParams.append("start_date", params.start_date);
    if (params?.end_date) queryParams.append("end_date", params.end_date);
    if (params?.category_id)
      queryParams.append("category_id", params.category_id.toString());

    const url = `/school-admin/statistics/club-activity${
      queryParams.toString() ? `?${queryParams.toString()}` : ""
    }`;
    const response = await get(url);
    return response.data;
  };

  // 获取学生活动参与统计
  const getStudentActivityStatistics = async (params?: {
    start_date?: string;
    end_date?: string;
    major?: string;
    grade?: string;
  }) => {
    const queryParams = new URLSearchParams();
    if (params?.start_date) queryParams.append("start_date", params.start_date);
    if (params?.end_date) queryParams.append("end_date", params.end_date);
    if (params?.major) queryParams.append("major", params.major);
    if (params?.grade) queryParams.append("grade", params.grade);

    const url = `/school-admin/statistics/student-activity${
      queryParams.toString() ? `?${queryParams.toString()}` : ""
    }`;
    const response = await get(url);
    return response.data;
  };

  // 获取活动参与率统计
  const getActivityParticipationStatistics = async (params?: {
    start_date?: string;
    end_date?: string;
    club_id?: number;
  }) => {
    const queryParams = new URLSearchParams();
    if (params?.start_date) queryParams.append("start_date", params.start_date);
    if (params?.end_date) queryParams.append("end_date", params.end_date);
    if (params?.club_id)
      queryParams.append("club_id", params.club_id.toString());

    const url = `/school-admin/statistics/activity-participation${
      queryParams.toString() ? `?${queryParams.toString()}` : ""
    }`;
    const response = await get(url);
    return response.data;
  };

  return {
    getClubActivityStatistics,
    getStudentActivityStatistics,
    getActivityParticipationStatistics,
  };
});

