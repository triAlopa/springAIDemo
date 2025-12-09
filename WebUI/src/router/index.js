import { createRouter, createWebHistory } from 'vue-router'
// 导入路由组件（路由组件建议放 pages/views 文件夹，普通组件放 components）
import App from '../App.vue';
import ChatMain from '../page/ChatMain.vue';
import LoginMain from '../page/LoginMain.vue';



// 创建路由器实例
const router = createRouter({
    // 路由工作模式（下文详解）
    history: createWebHistory(),
    // 路由规则：path 与组件的映射
    routes: [
        { 
            path: '/Login', 
            component: LoginMain
         },
        { 
            path: '/', 
            redirect: '/Login' 
        },
        { 
            path: '/chat', 
            component: ChatMain 
        }
    ]
})

export default router