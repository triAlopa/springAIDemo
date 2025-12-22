<script setup>
import {ref, onMounted} from 'vue'
import {queryLogApi} from '@/api/log.js'


const tableData = ref([])
const total = ref(0)
const queryForm = ref({
  pageSize: 10, pageNum: 1
})


const queryLog = async () => {
  const result = await queryLogApi(queryForm.value)
  if (result.code === 200) {
    tableData.value = result.data.rows
    total.value = result.data.total
  }
  console.log(result.data)
}

const handleSizeChange = (val) => {
  queryForm.value.pageSize = val;
  queryLog();
}
const handleCurrentChange = (val) => {
  queryForm.value.pageNum = val;
  queryLog();
}

const tableRowClassName = ({ row }) => {
  if (row.costTime >= 100) {
    return 'highlight-danger'
  }
  return ''
}

onMounted(queryLog)
</script>

<template>
  <div class="main-wrapper">
    <el-card class="box-card">
      <div class="table-container">
        <el-table
            stripe
            :row-class-name="tableRowClassName"
            :data="tableData"
            border
            height="700px"
            style="width: 100%"
        >
          <el-table-column property="operateId" label="操作用户id" width="85"/>
          <el-table-column property="operateTime" label="操作时间" width="210"/>
          <el-table-column property="className" label="操作全类名" width="210"/>
          <el-table-column property="methodName" label="方法名称" width="210"/>
          <el-table-column property="methodParams" label="方法名参数" width="210">
            <template #default="scope">
              {{ scope.row.methodName === '[]' ? '' : scope.row.methodName }}
            </template>
          </el-table-column>
          <el-table-column
              property="returnValue"
              label="返回值"
              width="250"
              show-overflow-tooltip
              />

          <el-table-column property="costTime" label="耗费时长" >
            <template #default="scope">
              <el-tag v-if="scope.row.costTime>0 && scope.row.costTime<30" type="info">{{ scope.row.costTime }}</el-tag>
              <el-tag v-else-if="scope.row.costTime>=30 && scope.row.costTime<50" type="warning">{{
                  scope.row.costTime
                }}
              </el-tag>
              <el-tag v-else-if="scope.row.costTime>=50 && scope.row.costTime<100" type="danger">{{
                  scope.row.costTime
                }}
              </el-tag>
              <el-tag v-else type="danger">{{ scope.row.costTime + '我操 这很严重了!!! 重点关注！' }}</el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="pagination-footer">
        <el-pagination
            v-model:current-page="queryForm.pageNum"
            v-model:page-size="queryForm.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
        />
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

/* 强制覆盖 Body，使其变成弹性容器 */
:deep(.el-card__body) {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 15px !important;
  overflow: hidden;
}

/* 简化版样式 */
:deep(.el-table .highlight-danger) {
  background-color: #fff2f0 !important;
  font-weight: bold;
}

:deep(.el-table .highlight-danger:hover > td) {
  background-color: #ffccc7 !important;
}

</style>