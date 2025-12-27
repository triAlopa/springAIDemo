<script setup>
import {ref, onMounted, onUnmounted, watch} from 'vue'
import * as echarts from 'echarts'
import {Refresh} from '@element-plus/icons-vue'
import {queryUserReport, getEmailApi, getUserExcel} from '@/api/user.js'
import dayjs from "dayjs";
import {ElMessage} from "element-plus";

// --- 1. 响应式变量定义 ---
const isWeek = ref(true) // 布尔型：true 代表一周，false 代表一个月
const lineBarRef = ref(null)
const pieRef = ref(null)

// 存储后端原始数据
const userStats = ref([])
const emailStats = ref([])

let lineBarChart = null
let pieChart = null

//API 数据拉取逻辑 ---

// 查询用户增减报表 (左图)
const getUserReport = async () => {
  const startDate = isWeek.value
      ? dayjs().subtract(7, 'day').format('YYYY-MM-DD')
      : dayjs().subtract(30, 'day').format('YYYY-MM-DD');

  const result = await queryUserReport(startDate)
  if (result.code === 200) {
    userStats.value = result.data

    const xAxisData = userStats.value.map(item => item.date)
    const regData = userStats.value.map(item => item.regCount)
    const delData = userStats.value.map(item => item.delCount)

    renderLineBarChart({xAxis: xAxisData, register: regData, delete: delData})
  }
}

//
const getEmailReport = async () => {
  const result = await getEmailApi()
  if (result.code === 200) {
    emailStats.value = result.data
    renderPieChart(emailStats.value)
  }
}

// 统一刷新方法
const refreshAllData = () => {
  getUserReport()
  getEmailReport()
}

// --- 3. ECharts 渲染逻辑 ---

const renderLineBarChart = (data) => {
  if (!lineBarChart) lineBarChart = echarts.init(lineBarRef.value)
  lineBarChart.setOption({
    tooltip: {trigger: 'axis', axisPointer: {type: 'shadow'}},
    legend: {data: ['用户注册', '用户删除'], bottom: 10},
    grid: {left: '3%', right: '4%', bottom: '15%', containLabel: true},
    xAxis: {type: 'category', data: data.xAxis},
    yAxis: {type: 'value'},
    series: [
      {
        name: '用户注册',
        type: 'bar',
        barWidth: '30%',
        data: data.register,
        itemStyle: {color: '#409EFF', borderRadius: [4, 4, 0, 0]}
      },
      {
        name: '用户删除',
        type: 'line',
        smooth: true,
        data: data.delete,
        itemStyle: {color: '#F56C6C'},
        lineStyle: {width: 3}
      }
    ]
  }, true)
}

const renderPieChart = (data) => {
  if (!pieChart) pieChart = echarts.init(pieRef.value)
  pieChart.setOption({
    tooltip: {trigger: 'item', formatter: '{a} <br/>{b} : {c} ({d}%)'},
    legend: {bottom: 0, type: 'scroll'},
    series: [
      {
        name: '邮箱类型',
        type: 'pie',
        radius: [30, 120],
        center: ['50%', '45%'],
        roseType: 'area',
        itemStyle: {borderRadius: 8},
        data: data
      }
    ]
  }, true)
}

// 生命周期与监听 ---

// 监听布尔值变化，自动更新左侧图表
watch(isWeek, () => {
  getUserReport()
})

const handleResize = () => {
  lineBarChart?.resize()
  pieChart?.resize()
}


const downloadExcel = async () => {
  try {
    const res = await getUserExcel();

    // 1. 创建 Blob 对象 (res 此时应该是二进制流)
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });

    // 2. 创建下载链接
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;

    // 3. 指定下载文件名
    link.setAttribute('download', `用户统计报表_${new Date().getTime()}.xlsx`);

    // 4. 触发点击并移除
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url); // 释放内存

    ElMessage.success("导出成功");
  } catch (error) {
    console.error("下载失败", error);
    ElMessage.error("导出失败，请检查后端日志");
  }
};


onMounted(() => {
  refreshAllData() // 初始化查询
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  lineBarChart?.dispose()
  pieChart?.dispose()
})
</script>

<template>
  <div class="main-wrapper">
    <el-card class="box-card">
      <div class="card-header">
        <div class="title-left">
          <h2 style="margin: 0;">用户统计报表</h2>
        </div>
        <div class="header-btns">
          <el-radio-group v-model="isWeek" size="default" style="margin-right: 15px">
            <el-radio-button :label="true">近一周</el-radio-button>
            <el-radio-button :label="false">近一个月</el-radio-button>
          </el-radio-group>
          <el-button type="primary" :icon="Refresh" @click="downloadExcel">拉取数据</el-button>
        </div>
      </div>

      <div class="charts-container">
        <div class="chart-box left-border">
          <div class="chart-title">用户增减趋势</div>
          <div ref="lineBarRef" class="inner-chart"></div>
        </div>

        <div class="chart-box">
          <div class="chart-title">邮箱来源分布</div>
          <div ref="pieRef" class="inner-chart"></div>
        </div>
      </div>

      <div class="pagination-footer">
        <span style="color: #909399; font-size: 14px;">
          数据状态：{{ isWeek ? '周维度' : '月维度' }} | 更新时间：{{ dayjs().format('YYYY-MM-DD HH:mm:ss') }}
        </span>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.main-wrapper {
  height: calc(100vh - 20px);
  padding: 10px;
  box-sizing: border-box;
  overflow: hidden;
}

.box-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

:deep(.el-card__body) {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 15px !important;
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-shrink: 0;
  margin-bottom: 20px;
}

.charts-container {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.chart-box {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 10px;
}

.left-border {
  border-right: 1px solid #f0f2f5;
}

.chart-title {
  font-size: 14px;
  font-weight: bold;
  color: #606266;
  text-align: center;
  margin-bottom: 10px;
}

.inner-chart {
  flex: 1;
  width: 100%;
}

.pagination-footer {
  display: flex;
  justify-content: flex-start;
  padding: 10px 0;
  flex-shrink: 0;
  border-top: 1px solid #eee;
}
</style>