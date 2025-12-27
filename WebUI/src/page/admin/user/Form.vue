<script setup>
import {ref, onMounted, computed} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {ArrowLeft, Check, Refresh} from '@element-plus/icons-vue'
import {ElMessage} from 'element-plus'
import {updateOrSaveApi, querySingleUser} from '@/api/user.js'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)

// 路径参数判断
const userId = computed(() => route.params.id)
const isEdit = computed(() => userId.value)

// 表单数据定义
const userForm = ref({
  nickName: '',
  gender: 1,
  email: '',
  password: '',
  birthday: '',
  image: '',
  points: 0,
  enable: 1
})

// 表单校验规则
const rules = {
  nickName: [{required: true, message: '请输入用户名', trigger: 'blur'}],
  email: [{required: true, type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur'}],
  password: [{required: !isEdit.value, message: '请输入密码', trigger: 'blur'}]
}

// 模拟获取用户信息（实际应调用 API）
const fetchUserInfo = async () => {
  if (isEdit.value) {
    const res = await querySingleUser(userId.value)
    if (res.code === 200) {
      console.log(res.data)
      userForm.value = res.data;
    }

    console.log('正在获取用户 ID 为', userId.value, '的数据')
  }
}

// 提交表
const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      await updateOrSaveApi(userForm.value)
          .then(result => {
            if (result.code === 200) {
              ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
              goBack()
            } else {
              ElMessage.warning(result.msg+'已存在')
            }
          }).catch(Error => {
            console.log(Error)
          })
    }
  })
}

const goBack = () => {
  router.push('/admin/user/manage') // 返回列表页
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
onMounted(() => {
  fetchUserInfo()
})
</script>

<template>
  <div class="form-page-container" style="margin-top: -17px">
    <transition name="fade-transform" mode="out-in" appear>
      <el-card class="form-card">
        <template #header>
          <div class="form-header">
            <el-button :icon="ArrowLeft" link @click="goBack">返回列表</el-button>
            <span class="form-title">{{ isEdit ? '编辑用户信息' : '新增用户账号' }}</span>
            <div style="width: 80px"></div>
          </div>
        </template>

        <el-form
            ref="formRef"
            :model="userForm"
            :rules="rules"
            label-width="100px"
            class="custom-form"
            size="large"
        >
          <div class="form-grid">
            <el-form-item label="用户名" prop="nickName">
              <el-input v-model="userForm.nickName" placeholder="请设置登录名"/>
            </el-form-item>

            <el-form-item label="用户头像">
              <el-upload
                  class="avatar-uploader"
                  action="/api/upload/image"
                  :show-file-list="false"
                  :on-success="handleAvatarSuccess"
                  :before-upload="beforeAvatarUpload"
                  :headers="uploadHeaders"
              >
                <img v-if="userForm.image" :src="userForm.image" class="avatar"/>
                <el-icon size="large" v-else class="avatar-uploader-icon ">
                  <Plus/>
                </el-icon>
              </el-upload>
            </el-form-item>

            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="userForm.gender">
                <el-radio :label="1">男</el-radio>
                <el-radio :label="2">女</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-form-item label="邮箱地址" prop="email">
              <el-input v-model="userForm.email" placeholder="example@mail.com"/>
            </el-form-item>

            <el-form-item label="登录密码" prop="password">
              <el-input
                  v-model="userForm.password"
                  type="password"
                  show-password
                  :placeholder="isEdit ? '不修改请留空' : '请设置 6 位以上密码'"
              />
            </el-form-item>

            <el-form-item label="出生日期">
              <el-date-picker
                  v-model="userForm.birthday"
                  type="date"
                  placeholder="选择日期"
                  style="width: 100%"
                  value-format="YYYY-MM-DD"
              />
            </el-form-item>

            <el-form-item label="初始积分">
              <el-input-number v-model="userForm.points" :min="0" style="width: 100%"/>
            </el-form-item>


          </div>

          <div class="form-footer">
            <el-button :icon="Refresh" @click="formRef.resetFields()">重置</el-button>
            <el-button type="primary" :icon="Check" @click="submitForm">确认提交</el-button>
          </div>
        </el-form>
      </el-card>
    </transition>
  </div>
</template>

<style scoped>
.form-page-container {
  min-height: calc(100vh - 40px);
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding-top: 40px;
  background-color: #f5f7fa;
}

.form-card {
  width: 100%;
  max-width: 800px;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.05) !important;
}

.form-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.form-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.custom-form {
  padding: 20px 40px;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 10px;
}

.form-footer {
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid #f0f2f5;
  display: flex;
  justify-content: center;
  gap: 20px;
}

/* 切换动画 */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.4s ease;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateY(30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateY(-30px);
}

.avatar-uploader :deep(.el-upload) {
  border: 1px dashed var(--el-border-color);
  border-radius: 50%; /* 圆形头像 */
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
  width: 120px;
  height: 120px;
}

.avatar-uploader :deep(.el-upload:hover) {
  border-color: var(--el-color-primary);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  text-align: center;
}

.avatar {
  width: 120px;
  height: 120px;
  display: block;
  object-fit: cover; /* 保证图片不变形 */
}
</style>