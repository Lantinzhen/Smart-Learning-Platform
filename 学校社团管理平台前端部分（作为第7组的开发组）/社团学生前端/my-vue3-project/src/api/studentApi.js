// // api/studentApi.js
// import axios from 'axios';

// // 创建axios实例
// const api = axios.create({
//   baseURL: 'http://localhost:8080/api/v1',
//   timeout: 10000,
//   headers: {
//     'Content-Type': 'application/json'
//   }
// });

// // 请求拦截器：添加JWT token
// api.interceptors.request.use(
//   config => {
//     const token = localStorage.getItem('studentToken') || 
//                   localStorage.getItem('clubAdminToken') || 
//                   localStorage.getItem('schoolAdminToken');
//     if (token) {
//       config.headers.Authorization = `Bearer ${token}`;
//     }
//     return config;
//   },
//   error => {
//     return Promise.reject(error);
//   }
// );

// // 响应拦截器：统一处理响应
// api.interceptors.response.use(
//   response => {
//     // 如果响应中有token，保存到localStorage
//     if (response.data.data?.token) {
//       localStorage.setItem('studentToken', response.data.data.token);
//     }
//     return response.data;
//   },
//   error => {
//     if (error.response) {
//       const { status, data } = error.response;
      
//       // 401未授权，清除token并跳转到登录页
//       if (status === 401) {
//         localStorage.removeItem('studentToken');
//         localStorage.removeItem('clubAdminToken');
//         localStorage.removeItem('schoolAdminToken');
//         window.location.href = '/student/login';
//       }
      
//       // 返回错误信息
//       return Promise.reject({
//         code: status,
//         message: data?.message || '请求失败',
//         data: data?.data
//       });
//     }
    
//     return Promise.reject({
//       code: 500,
//       message: '网络错误，请检查网络连接',
//       data: null
//     });
//   }
// );

// // 认证相关API
// export const authApi = {
//   // 学生登录
//   studentLogin: (data) => api.post('/auth/student/login', data),
  
//   // 学生注册
//   studentRegister: (data) => api.post('/auth/student/register', data),
  
//   // 退出登录
//   logout: () => api.post('/auth/logout'),
  
//   // 忘记密码（根据实际需求可能需要添加）
//   forgotPassword: (data) => api.post('/auth/student/forgot-password', data)
// };

// // 学生相关API
// export const studentApi = {
//   // 获取学生仪表盘
//   getDashboard: () => api.get('/student/dashboard'),
  
//   // 获取社团列表
//   getClubs: (params) => api.get('/student/clubs', { params }),
  
//   // 获取社团详情
//   getClubDetail: (clubId) => api.get(`/student/clubs/${clubId}`),
  
//   // 申请加入社团
//   applyToClub: (clubId, data) => api.post(`/student/clubs/${clubId}/applications`, data),
  
//   // 获取我的社团
//   getMyClubs: () => api.get('/student/my-clubs'),
  
//   // 退出社团
//   leaveClub: (clubId) => api.delete(`/student/clubs/${clubId}/leave`),
  
//   // 获取活动列表
//   getActivities: () => api.get('/student/activities'),
  
//   // 获取活动详情
//   getActivityDetail: (activityId) => api.get(`/student/activities/${activityId}`),
  
//   // 活动报名
//   registerActivity: (activityId) => api.post(`/student/${activityId}/register`),
  
//   // 取消活动报名
//   cancelActivityRegistration: (activityId, data) => api.delete(`/student/${activityId}/cancel`, { data }),
  
//   // 获取我的活动
//   getMyActivities: () => api.get('/student/my-activities'),
  
//   // 获取个人信息
//   getProfile: () => api.get('/student/profile'),
  
//   // 更新个人信息
//   updateProfile: (data) => api.post('/student/profile', data),
  
//   // 修改密码
//   changePassword: (data) => api.put('/student/change-password', data),
  
//   // 获取常见问题
//   getFaqs: (params) => api.get('/student/faqs', { params }),
  
//   // 获取帮助文档
//   getDocuments: (params) => api.get('/student/documents', { params }),
  
//   // 获取帮助文档详情
//   getDocumentDetail: (documentId) => api.get(`/student/documents/${documentId}`)
// };

// export default api;



import axios from 'axios';


// 创建axios实例
const studentApi = axios.create({
  baseURL: 'http://10.34.58.66:9099/api/v1',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 请求拦截器，添加JWT token
studentApi.interceptors.request.use(
  config => {
    const token = localStorage.getItem('studentToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// 响应拦截器
studentApi.interceptors.response.use(
  response => {
    // 处理API的响应格式
    if (response.data && response.data.code === 200) {
      return response.data.data;
    } else {
      return Promise.reject(new Error(response.data?.message || '请求失败'));
    }
  },
  error => {
    if (error.response) {
      switch (error.response.status) {
        case 401:
          // token过期或无效，跳转到登录页
          localStorage.removeItem('studentToken');
          window.location.href = '/student/login';
          break;
        case 403:
          return Promise.reject(new Error('权限不足'));
        case 404:
          return Promise.reject(new Error('请求的资源不存在'));
        case 500:
          return Promise.reject(new Error('服务器内部错误'));
        default:
          return Promise.reject(new Error(error.response.data?.message || '请求失败'));
      }
    }
    return Promise.reject(error);
  }
);

// 认证相关API
export const authApi = {
  // 学生登录
  login: (data) => studentApi.post('/auth/student/login', data),
  
  // 学生注册
  register: (data) => studentApi.post('/auth/student/register', data),
  
  // 退出登录
  logout: () => studentApi.post('/auth/logout'),
  
  // 忘记密码 - 发送验证码
  sendResetCode: (data) => studentApi.post('/auth/student/sendResetCode', data)
};

// 学生仪表盘API
export const dashboardApi = {
  // 获取学生仪表盘数据
  getDashboard: () => studentApi.get('/student/dashboard')
};

// 社团相关API
export const clubApi = {
  // 获取社团列表
  getClubs: (params) => studentApi.get('/student/clubs', { params }),
  
  // 获取社团详情
  getClubDetail: (clubId) => studentApi.get(`/student/clubs/${clubId}`),
  
  // 申请加入社团
  applyClub: (clubId, data) => studentApi.post(`/student/clubs/${clubId}/applications`, data),
  
  // 获取我的社团
  getMyClubs: () => studentApi.get('/student/my-clubs'),
  
  // 退出社团
  leaveClub: (clubId) => studentApi.delete(`/student/clubs/${clubId}/leave`)
};

// 活动相关API
export const activityApi = {
  // 获取活动列表
  getActivities: (params) => studentApi.get('/student/activities', { params }),
  
  // 获取活动详情
  getActivityDetail: (activityId) => studentApi.get(`/student/activities/${activityId}`),
  
  // 活动报名
  registerActivity: (activityId, data) => studentApi.post(`/student/${activityId}/register`, data),
  
  // 取消活动报名
  cancelActivity: (activityId, data) => studentApi.delete(`/student/${activityId}/cancel`, { data }),
  
  // 获取我的活动
  getMyActivities: () => studentApi.get('/student/my-activities')
};

// 个人信息API
export const profileApi = {
  // 获取个人信息
  getProfile: () => studentApi.get('/student/profile'),
  
  // 更新个人信息
  updateProfile: (data) => studentApi.post('/student/profile', data),
  
  // 修改密码
  changePassword: (data) => studentApi.put('/student/change-password', data)
};

// 帮助与支持API
export const supportApi = {
  // 获取常见问题
  getFaqs: (params) => studentApi.get('/student/faqs', { params }),
  
  // 获取帮助文档列表
  getDocuments: (params) => studentApi.get('/student/documents', { params }),
  
  // 获取帮助文档详情
  getDocumentDetail: (documentId) => studentApi.get(`/student/documents/${documentId}`)
};

export default studentApi;