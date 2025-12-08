import {defineStore} from 'pinia';
import {ref, computed} from 'vue';
import { get, post, put, del } from '@/utils/request';


export const useDashboardStore = defineStore('dashboard',()=>{ 
  
  // 获取获取社团管理概览
  const getOverview = async() => { 
    const response = await get('/club-admin/dashboard')
    return response.data;
  };
  

 return {  getOverview };
});