import {defineStore} from 'pinia'
import {ref, computed} from 'vue';
import { get, post, put, del } from '@/utils/request';

export const useMemberStore = defineStore('member',()=>{ 
  // 获取社团成员列表
  const getMemberList = async(page?:number,pageSize?:number)=>{
    const url = `/club-admin/members?page=${page || 1}&pageSize=${pageSize || 10}`
    const response = await get(url);
    return response.data;
  }

  // 修改成员角色
  const updateMemberRole = async(id:number,role:string)=>{
    const url = `/club-admin/members/${id}/role`
    const response = await put(url,{role});
    return response.data;
  }

  // 删除成员
  const deleteMember = async(id:number)=>{
    const url = `/club-admin/members/${id}`
    const response = await del(url);
    return response.data;
  }

  return { getMemberList,
    updateMemberRole,
    deleteMember,}
});