import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { get, post, put, del } from '@/utils/request';


export const useApplicationStore = defineStore('application',()=>{ 
     // 获取社团申请列表
    const getApplyList = async() => { 
      const url = `/club-admin/applications`;
      const response = await get(url);
      return response.data;
    };

    // 对申请进行审批
    const approveApplication = async(id:number|undefined,status:string,content?:string) => { 
      const url = `/club-admin/applications/${id}/review`;
      const data = {status,content};
      const response = await put(url,data);
      return response.data;
    };

    // 获取申请详情
    const getApplyDetail = async(id:number) => { 
      const url = `/club-admin/applications/${id}`;
      const response = await get(url);
      return response.data.data;
    };

   



    return {  getApplyList,approveApplication,getApplyDetail };
 
});