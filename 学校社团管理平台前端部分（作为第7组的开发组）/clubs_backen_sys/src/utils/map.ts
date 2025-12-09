import type{ ActivityItem,ActivityDetail } from "@/types/activity";
// 状态映射
export const getStatusTagType = (status: ActivityItem["status"] | ActivityDetail ["status"] ) => {
  const typeMap = {
    已取消: "info",
    待审批: "warning",
    已批准: "success",
    已拒绝: "danger",
  };
  return typeMap[status];
}