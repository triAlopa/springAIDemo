<script setup>
import { ref, onMounted } from 'vue';

import LoginView from '@/components/LoginView.vue';

import { useRouter } from 'vue-router';


const isDark = ref(false);



const router = useRouter();
// 方法
const handleLoginSuccess = () => {
    router.push('/chat');
};




const updateThemeClass = () => {
    if (isDark.value) {
        document.documentElement.classList.add('dark');
    } else {
        document.documentElement.classList.remove('dark');
    }
};

onMounted(() => {
    // 检查是否有暗黑模式偏好，并初始化 isDark
    if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
        isDark.value = true;
        updateThemeClass();
    }
});
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
            <login-view @login-success="handleLoginSuccess"></login-view>

        </transition>
    </div>
</template>
<style>
/* App.vue 中可以放置组件特有的 CSS，但此处已移至 style.css */
/* 动画 keyframes 可以在 tailwind.config.js 中配置 */
</style>