<template>
  <el-container style="height:100vh">
    <el-aside width="220px" style="padding:16px;box-sizing:border-box;border-right:1px solid #e6e6e6">
      <h3 style="margin:0 0 20px 15px">Admin导航</h3>
      <el-menu   @select="handleSelect">
        <el-sub-menu style="margin:0 0 0  -8px" index="user">
          <template #title>
            <el-icon type="small" >
              <location/>
            </el-icon>
            用户
          </template>
            <el-menu-item index="user/manage">管理</el-menu-item>
            <el-menu-item index="user/report">统计报表</el-menu-item>
        </el-sub-menu>
        <el-sub-menu style="margin:0 0 0  -8px" index="company">
          <template #title>
            <el-icon type="small" >
              <location/>
            </el-icon>
            公司
          </template>
          <el-menu-item index="company/manage">管理</el-menu-item>
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
import {useRouter, useRoute} from 'vue-router'

const router = useRouter()


function handleSelect(index) {
    router.push(`/admin/${index}`)
}
</script>

<style scoped>
h3 {
  font-size: 16px;
  margin: 0
}
</style>
