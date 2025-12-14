// 转换为CSV格式的辅助函数
export const convertToCSV = (data: any[]) => {
  // 获取列标题
  const headers = Object.keys(data[0]).join(",");

  // 获取数据行
  const rows = data.map((row) =>
    Object.values(row)
      .map((field) => {
        // 如果是日期类型，转换为 Excel 可识别的格式
        if (
          typeof field === "string" &&
          /^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/.test(field)
        ) {
          return `"${field}"`; // 用双引号包裹日期字符串
        }
        // 处理包含逗号、引号等特殊字符的字段
        return `"${String(field).replace(/"/g, '""')}"`;
      })
      .join(",")
  );

  // 组合标题和数据行
  return [headers, ...rows].join("\n");
};

// 下载CSV文件的辅助函数
export const downloadCSV = (csvContent: string, filename: string) => {
  // 创建Blob对象
  const blob = new Blob(["\uFEFF" + csvContent], {
    type: "text/csv;charset=utf-8;",
  });

  // 创建下载链接
  const link = document.createElement("a");
  const url = URL.createObjectURL(blob);

  // 设置下载属性
  link.setAttribute("href", url);
  link.setAttribute("download", filename);

  // 触发下载
  link.style.visibility = "hidden";
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
};