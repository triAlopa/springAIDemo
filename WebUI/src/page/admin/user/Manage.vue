<script setup>
import {ref, onMounted, watch} from 'vue'
import {queryAllUserApi, delSingUser, batchDelUser} from '@/api/user.js'
import {useRouter} from 'vue-router'
import {ArrowUp, ArrowDown, Plus, Delete} from '@element-plus/icons-vue'

const isSearchShow = ref(true)
const tableData = ref([])
const total = ref(0)
const queryForm = ref({
  nickName: '', gender: '', registerTime: [],
  start: '', end: '', pageSize: 10, pageNum: 1
})

const router = useRouter()

// 跳转到编辑/新增页面
const goToEdit = (id) => {
  if (id) {
    router.push(`edit/${id}`)
  } else {
    router.push('add')
  }
}
watch(() => queryForm.value.registerTime, (newVal) => {
  if (newVal && newVal.length === 2) {
    queryForm.value.start = newVal[0].trim();
    queryForm.value.end = newVal[1].trim();
  } else {
    queryForm.value.start = '';
    queryForm.value.end = '';
  }
  console.log(queryForm.value.start)
  console.log(queryForm.value.end)
})

const queryAllUser = async () => {
  const result = await queryAllUserApi(queryForm.value)
  if (result.code === 200) {
    tableData.value = result.data.rows
    total.value = result.data.total
  }
  console.log(result.data)
}

const handleSizeChange = (val) => {
  queryForm.value.pageSize = val;
  queryAllUser();
}
const handleCurrentChange = (val) => {
  queryForm.value.pageNum = val;
  queryAllUser();
}
const clear = () => {
  queryForm.value = {nickName: '', gender: '', registerTime: [], start: '', end: '', pageSize: 10, pageNum: 1};
  queryAllUser()
}

const delIds = ref([])

const handleSelectionChange = (val) => {
  delIds.value = [];

  val.forEach(item => delIds.value.push(item.id))


}
import {InfoFilled} from '@element-plus/icons-vue'
import {ElMessage} from "element-plus";

const confirmEvent = async (id) => {
  console.log('confirm!')
  await delSingUser(id)
      .then(result => {
        if (result.code == 200) {
          ElMessage.success("删除成功")
          queryAllUser();
        } else {
          ElMessage.warning(result.msg)
        }
      })
}
const cancelEvent = () => {
  console.log('cancel!')
}

const batchDelUsers = async () => {

  const params = new URLSearchParams();
  delIds.value.forEach(id => params.append('ids', id));


  await batchDelUser(params)
      .then(result => {
        if (result.code === 200) {
          ElMessage.success("删除成功")
          queryAllUser();
        } else {
          ElMessage.warning(result.msg)
        }
      }).catch(Error=>{
        console.log(Error)
      })
}

onMounted(queryAllUser)
</script>

<template>
  <div class="main-wrapper">
    <el-card class="box-card">
      <div class="card-header">
        <div class="title-left">
          <h2 style="margin: 0;">用户管理</h2>
          <el-button type="primary" link @click="isSearchShow = !isSearchShow" class="fold-btn">
            {{ isSearchShow ? '收起搜索' : '展开搜索' }}
            <el-icon class="el-icon--right">
              <ArrowUp v-if="isSearchShow"/>
              <ArrowDown v-else/>
            </el-icon>
          </el-button>
        </div>
        <div class="header-btns">
          <el-button type="primary" :icon="Plus" @click="goToEdit()">新增</el-button>
        </div>
      </div>

      <el-collapse-transition>
        <div v-show="isSearchShow" class="search-box">
          <el-form :inline="true" :model="queryForm" size="default">
            <el-form-item label="姓名">
              <el-input v-model="queryForm.nickName"
                        placeholder="输入姓名" clearable style="width: 200px"/>
            </el-form-item>
            <el-form-item label="性别">
              <el-select v-model="queryForm.gender" placeholder="选择" style="width: 100px">
                <el-option label="男" value="1"/>
                <el-option label="女" value="2"/>
              </el-select>
            </el-form-item>
            <el-form-item label="注册时间">
              <el-date-picker
                  v-model="queryForm.registerTime"
                  type="daterange"
                  range-separator="至"
                  value-format="YYYY-MM-DD"
                  style="width: 240px"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="queryAllUser">查询</el-button>
              <el-button @click="clear">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-collapse-transition>

      <div class="tool-bar">
        <el-button type="danger" @click="batchDelUsers" :icon="Delete" plain>批量删除</el-button>
      </div>

      <div class="table-container">
        <el-table
            :data="tableData"
            border
            height="550px"
            style="width: 100%"
            @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55"/>
          <el-table-column property="nickName" label="用户名" width="120"/>
          <el-table-column label="性别" width="80">
            <template #default="scope">
              <el-tag v-if="scope.row.gender===1" type="info">{{'男'}}</el-tag>
              <el-tag v-else-if="scope.row.gender===2" type="danger">{{'女'}}</el-tag>
              <el-tag v-else-if="scope.row.gender===0">{{'未知'}}</el-tag>
              <el-tag v-else-if="scope.row.gender===9">{{'未指定'}}</el-tag>
              <el-tag v-else>{{'null型'}}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="头像" width="70">
            <template #default="scope">
              <el-avatar :size="30" :src="scope.row.image"/>
            </template>
          </el-table-column>
          <el-table-column property="email" label="邮箱" width="360px" show-overflow-tooltip/>
          <el-table-column property="points" label="积分" width="100"/>
          <el-table-column property="registerTime" label="注册时间" width="170"/>
          <el-table-column label="操作" fixed="right">
            <template #default="scope">
              <el-button link type="primary" @click="goToEdit(scope.row.id)">修改</el-button>
              <el-popconfirm
                  confirm-button-text="Yes"
                  cancel-button-text="No"
                  :icon="InfoFilled"
                  icon-color="#626AEF"
                  title="你确定要删除吗?"
                  @confirm="confirmEvent(scope.row.id)"
                  @cancel="cancelEvent"
              >
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

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-shrink: 0;
  margin-bottom: 10px;
}

.title-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.search-box {
  background-color: #f9f9f9;
  padding: 15px 15px 0 15px;
  border-radius: 4px;
  margin-bottom: 10px;
  flex-shrink: 0;
}

.tool-bar {
  margin-bottom: 10px;
  flex-shrink: 0;
}


.table-container {
  flex: 1;
  overflow: hidden;
  margin-bottom: 10px;
}

.pagination-footer {
  display: flex;
  justify-content: flex-start;
  padding: 10px 0;
  flex-shrink: 0;
  border-top: 1px solid #eee;
}
</style>