<script setup>
import {ref, onMounted, computed} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {ArrowLeft, Check, Plus, Delete} from '@element-plus/icons-vue'
import {ElMessage} from 'element-plus'

import {querySingleCompanyApi, UpdateCompanyApi, SaveCompanyApi} from '@/api/company.js'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const companyId = computed(() => route.params.id)
const isEdit = computed(() => !!companyId.value)


const companyForm = ref({
  name: '',
  type: 1,
  company_id: '',
  lowSalary: 10,
  highSalary: 20,
  address: '',
  jobTag: [],
  jobDesc: '',
  employerBenefit: [],
  models: []
})


const addHr = () => {
  companyForm.value.models.push({
    name: '',
    description: '',
    temperature: 1.0,
    openMessage: '',
    image: '',
    remark: '',
    enable: 1
  })
}


const removeHr = (index) => {
  companyForm.value.models.splice(index, 1)
}

const fetchCompanyInfo = async () => {
  if (isEdit.value) {
    const res = await querySingleCompanyApi(companyId.value)
    if (res.code === 200) {
      companyForm.value = res.data
      if (!companyForm.value.models) companyForm.value.models = []
      if (!companyForm.value.jobTag) companyForm.value.jobTag = []
      if (!companyForm.value.employerBenefit) companyForm.value.employerBenefit = []
    } else {
      ElMessage.warning(res.msg)
    }
  }
}

const submitForm = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      if (isEdit.value) {
        const res = await UpdateCompanyApi(companyForm.value)
        if (res.code === 200) {
          ElMessage.success('操作成功')
          router.push('/admin/company/manage')
        }
      } else {
        const res = await SaveCompanyApi(companyForm.value)
        if (res.code === 200) {
          ElMessage.success('操作成功')
          router.push('/admin/company/manage')
        }
      }
    }
  })
}

const handleAvatarSuccess = (response, hr) => {
  if (response.code === 200) {
    hr.image = response.data
    ElMessage.success('头像上传成功')
  }
}

const rules = {
  name: [{required: true, message: '请输入用户名', trigger: 'blur'}],

  type: [{required: !isEdit.value, message: '选择规模', trigger: 'blur'}],

  address: [{required: !isEdit.value, message: '请输入地址', trigger: 'blur'}],

  salary: [
    { validator: (rule,value,callback)=>{
      const {lowSalary,highSalary} =companyForm.value
      if(!lowSalary || !highSalary) return;
      if(lowSalary==='' || highSalary ===''){
        return callback(new Error('请输入薪水'))
      }

      if(lowSalary>highSalary){
        return callback(new Error('最低薪水不能大于最高薪水'))
      }
      callback();
      },   trigger: 'blur' }
  ]
}

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

const uploadHeaders = ref({
  authorization: JSON.parse(localStorage.getItem('userToken'))
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

      <el-form ref="formRef" :model="companyForm" label-width="100px" size="large" :rules="rules">
        <h3 class="section-title">基础信息</h3>
        <div class="form-grid">
          <el-form-item label="公司名称" prop="name" required>
            <el-input v-model="companyForm.name"/>
          </el-form-item>
          <el-form-item label="类型" prop="type">
            <el-radio-group v-model="companyForm.type">
              <el-radio :label="1">初创</el-radio>
              <el-radio :label="2">上市</el-radio>
              <el-radio :label="3">500强</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="薪资范围" prop="salary">
            <div style="display: flex; align-items: center; gap: 10px">
              <el-input-number type="number"  v-model="companyForm.lowSalary" :min="0"/>
              <span>至</span>
              <el-input-number  type="number"   v-model="companyForm.highSalary" :min="0"/>
              <span>k</span>
            </div>
          </el-form-item>
          <el-form-item label="公司地址" prop="address">
            <el-input v-model="companyForm.address" placeholder="地址"/>
          </el-form-item>

          <el-form-item label="职位标签">
            <el-select
                v-model="companyForm.jobTag"
                multiple filterable allow-create default-first-option
                placeholder="请输入并回车添加"
                style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="福利待遇">
            <el-select
                v-model="companyForm.employerBenefit"
                multiple filterable allow-create default-first-option
                placeholder="请输入并回车添加"
                style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="职位描述" class="full-width">
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

            <el-form-item label="HR头像">
              <el-upload
                  class="avatar-uploader"
                  action="/api/user/upload"
                  :show-file-list="false"
                  :on-success="(res) => handleAvatarSuccess(res, hr)"
                  :before-upload="beforeAvatarUpload"
                  :headers="uploadHeaders"
              >
                <img v-if="hr.image" :src="hr.image" class="avatar"/>
                <el-icon v-else class="avatar-uploader-icon">
                  <Plus/>
                </el-icon>
              </el-upload>
            </el-form-item>

            <el-form-item label="状态">
              <el-switch
                  v-model="hr.enable"
                  :active-value="1" :inactive-value="0"
                  active-text="开启" inactive-text="关闭"
              />
            </el-form-item>
            <el-form-item label="开场白" class="full-width">
              <el-input v-model="hr.openMessage" type="textarea" rows="2" placeholder="AI开场白"/>
            </el-form-item>
            <el-form-item label="备注" class="full-width">
              <el-input v-model="hr.remark" type="textarea" rows="2" placeholder="备注信息"/>
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

/* 头像上传特定样式 */
.avatar-uploader :deep(.el-upload) {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 100px;
  height: 100px;
}

.avatar {
  width: 100px;
  height: 100px;
  object-fit: cover;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  line-height: 100px;
  text-align: center;
}
</style>