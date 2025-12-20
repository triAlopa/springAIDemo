<script setup>
import {ref, onMounted, computed} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {ArrowLeft, Check, Plus, Delete} from '@element-plus/icons-vue'
import {ElMessage} from 'element-plus'

import {querySingleCompanyApi} from '@/api/company.js'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const companyId = computed(() => route.params.id)
const isEdit = computed(() => !!companyId.value)

// 表单数据定义
const companyForm = ref({
  name: '',
  type: 1,
  company_id: '',
  lowSalary: 10,
  highSalary: 20,
  address: '',
  jobTag: '',
  jobDesc: '',
  employerBenefit: '',
  enable: 1,
  // 动态 HR 列表 (对应 tb_ai_model)
  models: []
})

// 添加 HR 行
const addHr = () => {
  companyForm.value.models.push({
    name: '',
    description: '',
    temperature: 1.0,
    open_message: '',
    image: '',
    remark: ''
  })
}

// 删除 HR 行
const removeHr = (index) => {
  companyForm.value.models.splice(index, 1)
}

const fetchCompanyInfo = async () => {
  if (isEdit.value) {
    const res = await querySingleCompanyApi(companyId.value)
    if (res.code === 200) {
      companyForm.value = res.data
      // 确保 hrList 存在
      if (!companyForm.value.models) companyForm.value.models = []
    }
  }
}

const submitForm = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      const res = await saveOrUpdateCompanyApi(companyForm.value)
      if (res.code === 200) {
        ElMessage.success('操作成功')
        router.push('/admin/company/manage')
      }
    }
  })
}

const handleAvatarSuccess = (response) => {

  userForm.value.image = response.data
  ElMessage.success('头像上传成功')
}



// 上传前校验
const beforeAvatarUpload = (rawFile) => {



  const allowedTypes = ['image/jpeg', 'image/png', 'image/jpg']
  if (!allowedTypes.includes(rawFile.type)) {
    ElMessage.error('头像只能是 JPG 或 PNG 格式!')
    return false
  } else if (rawFile.size / 1024 / 1024 > 2) {
    ElMessage.error('头像大小不能超过 2MB!')
    return false
  }
  return true
}

const uploadHeaders=ref({
  authorization:JSON.parse(localStorage.getItem('userToken'))
})


onMounted(fetchCompanyInfo)
</script>

<template>
  <div class="form-page-container">
    <el-card class="form-card">
      <template #header>
        <div class="form-header">
          <el-button :icon="ArrowLeft" link @click="router.back()">返回</el-button>
          <span class="form-title">{{ isEdit ? '编辑公司' : '新增公司' }}</span>
          <div style="width: 60px"></div>
        </div>
      </template>

      <el-form ref="formRef" :model="companyForm" label-width="100px" size="large">
        <h3 class="section-title">基础信息</h3>
        <div class="form-grid">
          <el-form-item label="公司名称" prop="name" required>
            <el-input v-model="companyForm.name"/>
          </el-form-item>
          <el-form-item label="类型">
            <el-radio-group v-model="companyForm.type">
              <el-radio :label="1">初创</el-radio>
              <el-radio :label="2">上市</el-radio>
              <el-radio :label="3">500强</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="薪资范围">
            <div style="display: flex; align-items: center; gap: 10px">
              <el-input-number v-model="companyForm.lowSalary" :min="0"/>
              <span>至</span>
              <el-input-number v-model="companyForm.highSalary" :min="0"/>
              <span>k</span>
            </div>
          </el-form-item>
          <el-form-item label="公司地址">
            <el-input v-model="companyForm.address" placeholder="经纬度或详细地址"/>
          </el-form-item>
          <el-form-item label="职位标签">
            <el-input v-model="companyForm.jobTag" placeholder="多个标签用空格隔开"/>
          </el-form-item>
          <el-form-item label="福利待遇">
            <el-input v-model="companyForm.employerBenefit" placeholder="多个福利用空格隔开"/>
          </el-form-item>
          <el-form-item label="职位描述">
            <el-input v-model="companyForm.jobDesc" type="textarea" rows="3"/>
          </el-form-item>
        </div>

        <el-divider/>

        <div class="hr-header">
          <h3 class="section-title">HR 数据配置</h3>
          <el-button type="success" :icon="Plus" size="small" @click="addHr">添加 HR</el-button>
        </div>

        <div v-for="(hr, index) in companyForm.models" :key="index" class="hr-item-card">
          <div class="hr-item-header">
            <span>HR #{{ index + 1 }}</span>
            <el-button type="danger" :icon="Delete" link @click="removeHr(index)">删除</el-button>
          </div>
          <div class="hr-grid">
            <el-form-item label="HR姓名">
              <el-input v-model="hr.name" placeholder="HR姓名"/>
            </el-form-item>
            <el-form-item label="创意度">
              <el-slider v-model="hr.temperature" :min="0" :max="2.0" :step="0.1" show-input/>
            </el-form-item>
            <el-form-item label="hr头像">
              <el-upload
                  class="avatar-uploader"
                  action="/api/user/upload"
                  :show-file-list="false"
                  :on-success="handleAvatarSuccess"
                  :before-upload="beforeAvatarUpload"
                  :headers="uploadHeaders"
              >
                <img v-if="hr.image" :src="hr.image" class="avatar"/>
                <el-icon size="large" v-else class="avatar-uploader-icon ">
                  <Plus/>
                </el-icon>
              </el-upload>
            </el-form-item>
            <el-form-item label="开场白" class="full-width">
              <el-input v-model="hr.openMessage" type="textarea" rows="2" placeholder="AI开场白"/>
            </el-form-item>
            <el-form-item label="备注" class="full-width">
              <el-input v-model="hr.remark" type="textarea" rows="2" placeholder="描述用于AI提示词"/>
            </el-form-item>
          </div>
        </div>

        <div class="form-footer">
          <el-button size="large" @click="router.back()">取消</el-button>
          <el-button type="primary" size="large" :icon="Check" @click="submitForm">保存公司信息</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.form-page-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.form-card {
  max-width: 1000px;
  margin: 0 auto;
  border-radius: 12px;
}

.section-title {
  margin: 20px 0;
  color: #409eff;
  border-left: 4px solid #409eff;
  padding-left: 10px;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.hr-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.hr-item-card {
  background: #f8f9fb;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 15px;
}

.hr-item-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
  font-weight: bold;
  color: #606266;
}

.hr-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.full-width {
  grid-column: span 2;
}

.form-footer {
  margin-top: 30px;
  display: flex;
  justify-content: center;
  gap: 20px;
}
</style>