<script setup>
import {ref, onMounted} from 'vue'
import {useRouter} from 'vue-router'
import {ArrowUp, ArrowDown, Plus, Delete, InfoFilled} from '@element-plus/icons-vue'
import {ElMessage} from "element-plus"
import {queryAllCompanyApi, DelCompanyApi} from '@/api/company.js'

const isSearchShow = ref(true)
const tableData = ref([])
const total = ref(0)
const router = useRouter()

const queryForm = ref({
  name: '',
  type: '',
  pageSize: 5,
  pageNum: 1
})

// 获取数据
const queryAllCompany = async () => {
  const result = await queryAllCompanyApi(queryForm.value)
  if (result.code === 200) {
    tableData.value = result.data.rows
    total.value = result.data.total
    await handleTag()
  }
}
const handleTag= async ()=>{
 await tableData.value.forEach(item=> item.jobTag= item.jobTag.split(' ') )
 await  tableData.value.forEach(item=> item.employerBenefit= item.employerBenefit.split(' ') )
}


const goToEdit = (companyId) => {
  if (companyId) router.push(`edit/${companyId}`)
  else router.push('add')
}

const delIds = ref([])
const handleSelectionChange = (val) => {
  delIds.value = val.map(item => item.companyId)
}

const confirmDelete = async (companyId) => {
  const result = await DelCompanyApi(companyId)
  if (result.code === 200) {
    ElMessage.success("删除成功")
    queryAllCompany()
  }
}

onMounted(queryAllCompany)
</script>

<template>
  <div class="main-wrapper">
    <el-card class="box-card">
      <div class="card-header">
        <div class="title-left">
          <h2 style="margin: 0;">公司管理</h2>
          <el-button type="primary" link @click="isSearchShow = !isSearchShow">
            {{ isSearchShow ? '收起搜索' : '展开搜索' }}
            <el-icon>
              <ArrowUp v-if="isSearchShow"/>
              <ArrowDown v-else/>
            </el-icon>
          </el-button>
        </div>
        <el-button type="primary" :icon="Plus" @click="goToEdit()">新增公司</el-button>
      </div>

      <el-collapse-transition>
        <div v-show="isSearchShow" class="search-box">
          <el-form :inline="true" :model="queryForm">
            <el-form-item label="公司名称">
              <el-input v-model="queryForm.name" placeholder="输入名称" clearable/>
            </el-form-item>
            <el-form-item label="类型">
              <el-select v-model="queryForm.type" placeholder="请选择" style="width: 150px">
                <el-option label="初创公司" value="1"/>
                <el-option label="上市公司" value="2"/>
                <el-option label="500强公司" value="3"/>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="queryAllCompany">查询</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-collapse-transition>

      <div class="table-container">
        <el-table :data="tableData" border @selection-change="handleSelectionChange" height="550px">
          <el-table-column type="selection" width="55"/>
          <el-table-column prop="name" label="公司名称" width="150" fixed/>

          <el-table-column label="类型" width="120">
            <template #default="scope">
<!--              <el-tag :type="scope.row.gender === 0 ? 'danger' : ''">{{ scope.row.gender === 0 ? '女' : '男' }}</el-tag>-->
              <el-tag v-if="scope.row.type===1" type="info" >{{'初创公司'}}</el-tag>
              <el-tag v-else-if="scope.row.type===2" type="success" >{{'上市公司'}}</el-tag>
              <el-tag v-else-if="scope.row.type===3" type="primary" >{{'500强公司'}}</el-tag>
              <el-tag v-else  type="danger" >{{'null型公司'}}</el-tag>
            </template>
          </el-table-column>

          <el-table-column label="薪水范围" width="130">
            <template #default="scope">
              <span style="color: #f56c6c; font-weight: bold;">
                {{ scope.row.lowSalary }}k - {{ scope.row.highSalary }}k
              </span>
            </template>
          </el-table-column>

          <el-table-column prop="address" label="公司位置" width="300" show-overflow-tooltip/>

          <el-table-column label="职位标签" width="200">
            <template #default="scope">
              <el-tag v-for="tag in scope.row.jobTag" :key="tag" size="small" style="margin-right: 4px">
                {{ tag }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column prop="jobDesc" label="具体要求" width="200" show-overflow-tooltip/>

          <el-table-column label="员工福利" width="300">
            <template #default="scope">
              <el-tag v-for="tag in scope.row.employerBenefit " :key="tag" type="success" size="small"
                      effect="plain" style="margin-right: 4px">
                {{ tag }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column label="操作" width="150" fixed="right">
            <template #default="scope">
              <el-button link type="primary" @click="goToEdit(scope.row.companyId)">修改</el-button>
              <el-popconfirm title="确定删除该公司吗？" @confirm="confirmDelete(scope.row.companyId)">
                <template #reference>
                  <el-button link type="danger">删除</el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="pagination-footer">
        <el-pagination
            v-model:current-page="queryForm.pageNum"
            v-model:page-size="queryForm.pageSize"
            :total="total"
            :page-sizes="[5, 10, 15, 20]"
            layout="total, sizes, prev, pager, next, jumper"
            @current-change="queryAllCompany"
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
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.search-box {
  background-color: #f9f9f9;
  padding: 15px 15px 0 15px;
  border-radius: 4px;
  margin-bottom: 10px;
}

.table-container {
  flex: 1;
  overflow: hidden;
}

.pagination-footer {
  margin-top: 10px;
}
</style>