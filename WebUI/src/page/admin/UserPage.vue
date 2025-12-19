<template>
  <el-card>
    <div style="display:flex;align-items:center;justify-content:space-between">
      <h2>通用表格（Table）</h2>
    </div>
    <el-table :data="pagedData" style="width:100%;margin-top:12px">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="value" label="值" width="120" />
    </el-table>

    <div style="margin-top:12px;text-align:right">
      <el-pagination
          background
          :page-size="pageSize"
          :current-page.sync="page"
          :total="rawData.length"
          layout="prev, pager, next"
      />
    </div>
  </el-card>
</template>

<script setup>
import { ref, computed } from 'vue'

const rawData = ref(Array.from({length:53}).map((_,i)=>({id:i+1,name:`Item ${i+1}`,value:Math.floor(Math.random()*1000)})))
const page = ref(1)
const pageSize = ref(10)

const pagedData = computed(()=>{
  const start = (page.value-1)*pageSize.value
  return rawData.value.slice(start, start+pageSize.value)
})
</script>

<style scoped>
</style>
