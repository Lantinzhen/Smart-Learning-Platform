import { defineStore } from "pinia";
import { ref, computed } from "vue";
import { get, post, put, del } from "@/utils/request";

// 登录表单类型
type loginForm = {
  username: string;
  password: string;
};
export const useAdminStore = defineStore("admin", () => {
  const login = async (loginForm: loginForm) => {
    const response = await post("/auth/school-admin/login", loginForm);
    console.log(response);
    if (response.data.code === 200) {
      localStorage.setItem("school_admin_name", response.data.data.user.name);
      // token只存在这个地方，避免混乱
      localStorage.setItem("admin_token", response.data.data.token);
      localStorage.setItem("school_admin_id", response.data.data.user.admin_id);
      localStorage.setItem("school_admin_role", response.data.data.user.admin_role);
      localStorage.setItem("admin_isLogin", "true");
      return true;
    } else {
      return false;
    }
  };

  const logout = () => {
    localStorage.removeItem("school_admin_name");
    localStorage.removeItem("admin_token");
    localStorage.removeItem("school_admin_id");
    localStorage.removeItem("school_admin_role");
    localStorage.removeItem("scheool_admin_isLogin");
  };

  return { login, logout };
});
