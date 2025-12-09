import {defineStore} from 'pinia';
import {ref, computed} from 'vue';
import { get, post, put, del } from '@/utils/request';

// 登录后获取所属社团的id club_id
// 登录表单类型
type loginForm = { 
  username: string;
  password: string;
}
export const useUserStore = defineStore('user',()=>{ 
const login = async(loginForm:loginForm) => { 
  const response = await post('/auth/club-admin/login',loginForm)
  console.log(response)
  if(response.data.code === 200) {
      localStorage.setItem('admin_name', response.data.data.user.name);
      localStorage.setItem('admin_token', response.data.data.token);
      localStorage.setItem('admin_club_id', response.data.data.user.club_id);
      localStorage.setItem('admin_id', response.data.data.user.admin_id);
      localStorage.setItem('admin_club_role', response.data.data.user.role);
      localStorage.setItem('admin_isLogin', 'true');
      return true;
  }else {
    return false;

  }
}

const logout = () => { 
  localStorage.removeItem('admin_name');
  localStorage.removeItem('admin_token');
  localStorage.removeItem('admin_club_id');
  localStorage.removeItem('admin_id');
  localStorage.removeItem('admin_club_role');
  localStorage.removeItem('admin_isLogin');
  localStorage.removeItem('club_name');
}

return {  login,logout }

});