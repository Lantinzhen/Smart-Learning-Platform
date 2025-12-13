import axios from "axios";
import type { AxiosRequestConfig } from "axios";
import { ElMessage } from "element-plus";

type method =
  | "get"
  | "post"
  | "put"
  | "delete"
  | "patch"
  | "options"
  | "head"
  | "trace"
  | "connect";

// 1.创建axios实例
const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api/v1",
  timeout: 10000, //请求超时时间
  headers: {
    "Content-Type": "application/json;charset=utf-8",
  },
});

// 2.请求拦截器
service.interceptors.request.use(
  (config) => {
    // 在发送请求之前做些什么
    const token = localStorage.getItem("admin_token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    console.error("请求拦截器错误", error);
    return Promise.reject(error);
  }
);

// 3.响应拦截器
service.interceptors.response.use(
  (response) => {
    // 对响应数据做点什么
    const res = response.data;

    console.log("响应数据", res);
    if (res.code === 200) {
      // 成功时，直接返回数据体 (res)
      return response;
    } else {
      // 失败时，显示错误信息并 reject
      const errorMessage = res.message || "请求失败";
      ElMessage.error(errorMessage);
      return Promise.reject(new Error(errorMessage));
    }
  },
  (error) => {
    // 对响应错误做点什么
    console.error("响应拦截器错误", error);
    let errorMessage = "网络异常，请稍后重试";

    if (error.response) {
      // 请求已发出，服务器返回状态码不是 2xx
      const status = error.response.status;
      switch (status) {
        case 401:
          errorMessage = "登录已过期，请重新登录";
          // 可以在这里添加跳转登录页的逻辑
          setTimeout(() => {
            window.location.href = "/login";
          }, 1000);
          break;
        case 403:
          errorMessage = "没有权限访问该资源";
          setTimeout(() => {
            window.location.href = "/login";
          }, 1000);
          break;
        case 404:
          errorMessage = "请求的接口不存在";
          break;
        case 500:
          errorMessage = "服务器内部错误";
          break;
        default:
          errorMessage = error.response.data?.message || errorMessage;
          break;
      }
    } else {
      // 请求已发出，但没有收到响应
      errorMessage = "请求超时，请检查网络连接";
    }
    ElMessage.error(errorMessage);
    return Promise.reject(error);
  }
);

// 4.常用的封装方法
const request = async (method: method, url: string, data?: any, config?: AxiosRequestConfig) => {
  try {
    const response = await service({
      method,
      url,
      // get请求使用params
      params: method === "get" ? data : config?.params,
      // 非get请求使用data
      data: method !== "get" ? data : undefined,
      ...config,
    });
    return response;
  } catch (error) {
    throw error;
  }
};

// 5. 导出便捷的请求函数
export const get = (url: string, params?: any, config?: AxiosRequestConfig) =>
  request("get", url, params, config);

export const post = (url: string, data?: any, config?: AxiosRequestConfig) =>
  request("post", url, data, config);

export const put = (url: string, data?: any, config?: AxiosRequestConfig) =>
  request("put", url, data, config);

export const del = (url: string, params?: any, config?: AxiosRequestConfig) =>
  request("delete", url, params, config);

export default service;
