<template>
  <el-card>
    <div style="display:flex;align-items:center;justify-content:space-between">
      <h2>用户报表</h2>
    </div>
    <div ref="chartRef" style="width:100%;height:360px;margin-top:12px"></div>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'

const chartRef = ref(null)

onMounted(()=>{
  if (chartRef.value){
    const chart = echarts.init(chartRef.value)
    chart.setOption({
      title:{text:'示例流量趋势'},
      tooltip:{},
      xAxis:{type:'category',data:['周一','周二','周三','周四','周五','周六','周日']},
      yAxis:{type:'value'},
      series:[{type:'line',data:[120,200,150,80,70,110,130]}]
    })
  }
})
</script>

<style scoped>
</style>
