
// 格式化 ISO 时间为 "YYYY-MM-DD HH:mm:ss"（北京时间）
export const formatIsoTime = (isoTime: string) => {
  const date = new Date(isoTime);

  // 处理无效时间
  if (isNaN(date.getTime())) {
    return "无效时间";
  }

  // 北京时间 = UTC 时间 + 8 小时
  const offset = 8 * 60 * 60 * 1000; // 8小时毫秒数
  const beijingDate = new Date(date.getTime() + offset);

  // 补零函数（确保数字是两位数，如 1→01）
  const padZero = (num: number) => num.toString().padStart(2, "0");

  const year = beijingDate.getFullYear();
  const month = padZero(beijingDate.getMonth() + 1); // 月份从0开始，需+1
  const day = padZero(beijingDate.getDate());
  const hour = padZero(beijingDate.getHours());
  const minute = padZero(beijingDate.getMinutes());
  const second = padZero(beijingDate.getSeconds());

  return `${year}-${month}-${day} ${hour}:${minute}:${second}`;
}