<template>
  <el-container style="height:100vh">
    <el-aside width="220px" style="padding:16px;box-sizing:border-box;border-right:1px solid #e6e6e6">
      <h3 style="margin:0 0 20px 15px">Admin导航</h3>
      <el-menu :default-active="activeMenu" @select="handleSelect">
        <el-menu-item index="console">控制台（Console）</el-menu-item>
        <el-menu-item index="table">通用表格（Table）</el-menu-item>
        <el-sub-menu style="margin:0 0 0  -8px" index="admin">
          <template #title>
            <el-icon type="small" >
              <location/>
            </el-icon>
            用户
          </template>
            <el-menu-item index="user/manage">管理</el-menu-item>
            <el-menu-item index="user/report">统计报表</el-menu-item>
        </el-sub-menu>
      </el-menu>

    </el-aside>

    <el-container>
      <el-main style="padding:16px;overflow:auto">
        <div style="margin : 0 0 20px 10px">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/admin/table' }">homepage</el-breadcrumb-item>
            <el-breadcrumb-item>
              <a href="/">promotion management</a>
            </el-breadcrumb-item>
            <el-breadcrumb-item>promotion list</el-breadcrumb-item>
            <el-breadcrumb-item>promotion detail</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <router-view/>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import {computed} from 'vue'
import {useRouter, useRoute} from 'vue-router'

const router = useRouter()
const route = useRoute()

const activeMenu = computed(() => {
  const path = route.path || ''
  if (path.endsWith('/table')) return 'table'
  if (path.endsWith('/chat')) return 'chat'
  return 'console'
})

function handleSelect(index) {
  if (index === 'chat') {
    router.push('/admin/chat')
  } else {
    router.push(`/admin/${index}`)
  }
}
</script>

<style scoped>
h3 {
  font-size: 16px;
  margin: 0
}
</style>
