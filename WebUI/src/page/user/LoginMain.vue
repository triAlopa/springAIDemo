<script setup>
import {ref, reactive} from 'vue';

import LoginView from '@/components/LoginView.vue';

import { useRouter } from 'vue-router';
import {ElMessage} from "element-plus";
import {loginApi, loginCodeApi, registerApi, requestCodeAPi} from "@/api/user.js";

// 方法
const handleLoginSuccess = () => {
    router.push('/chat');
};

//创建路由控制者
const router = useRouter();



const loginForm = reactive({
  email: '',
  password: '',
  loginCode: ''
})


const captchaUrl = ref('https://dummyimage.com/100x40/e0e0e0/555555&text=点击我获取')


const refreshCaptcha = (email) => {
  loginCodeApi(email)
      .then(result => {
        if (result.code == 200) {
          let code = result.data
          captchaUrl.value = `https://dummyimage.com/100x40/e0e0e0/555555&text=${code}`
        } else {
          ElMessage.error(result.msg)
        }
      }).catch(error => {
    ElMessage.error(error)
  })
}


async function registerUser(form) {
  await registerApi(form)
      .then(result => {
        if (result.code != 200) {
          //前后端统一使用 409作为重复注册状态码
          if (result.code == 409) {
            ElMessage.error("重复注册的账号：" + result.msg)
            return;
          } else if (result.code == 400) {
            //注册表单参数填入错误
            ElMessage.error(result.msg)
            return;
          }
          console.log(result.msg)
          ElMessage.warning('注册失败，请检查数据！');
        } else {
          setTimeout(() => {
            let info = JSON.stringify(result.data);
            localStorage.setItem("userToken", info)
            router.replace('/chat')
            ElMessage.success('注册成功并登录')
          }, 800)
        }
      }).catch(error => {
        ElMessage.warning(error);
      })
}


async function loginUser(form) {
  loginForm.email = form.email;
  loginForm.password = form.password;
  loginForm.loginCode = form.loginCode;
  await loginApi(loginForm)
      .then(result => {
        if (result.code != 200) {
          console.log(result.msg)
          ElMessage.warning('登录失败，请检查数据！' + result.msg);
        } else {
          setTimeout(() => {
            let info = JSON.stringify(result.data);
            localStorage.setItem("userToken", info)
            router.replace('/chat')
            ElMessage.success('登录成功')
          }, 800)
        }
      }).catch(error => {
        ElMessage.warning(error);
      })
}



</script>
<template>
    <div class="h-screen w-full flex flex-col justify-center items-center overflow-hidden relative">
        <!-- 背景装饰球 -->
        <div
            class="absolute top-[-10%] left-[-10%] w-[500px] h-[500px] bg-blue-300/30 rounded-full blur-[100px] pointer-events-none animate-float">
        </div>
        <div class="absolute bottom-[-10%] right-[-10%] w-[400px] h-[400px] bg-purple-300/30 rounded-full blur-[100px] pointer-events-none animate-float"
            style="animation-delay: 1s;"></div>

        <transition name="el-fade-in-linear" mode="out-in">

            <!-- 1. 登录页 -->
            <login-view @login-success="handleLoginSuccess"
                        @refreshLoginCode="refreshCaptcha"
                        @registerHandler="registerUser"
                        @loginUser="loginUser"
         :captchaUrl="captchaUrl"
            ></login-view>

        </transition>
    </div>
</template>
<style>
</style>