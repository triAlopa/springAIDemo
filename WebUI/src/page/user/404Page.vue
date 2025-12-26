<template>
  <el-result title="404" sub-title="Sorry, request error">
    <template #icon>
      <el-image style="width: auto"
          src="https://img95.699pic.com/desgin_photo/40123/3105_list.jpg"
      />
    </template>
  </el-result>
</template>

<script setup>

import {useRouter}  from "vue-router";
import  {onMounted} from 'vue'
import {ElMessage} from "element-plus";
const  router=useRouter();

onMounted(()=>{
  ElMessage.warning('此页面不存在，5秒自动跳转首页');
  setTimeout(()=>{
    router.push('/login')
  },5000)
})


</script>