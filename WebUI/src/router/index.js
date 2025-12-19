import { createRouter, createWebHistory } from 'vue-router'
// 导入路由组件（路由组件建议放 pages/views 文件夹，普通组件放 components）
import App from '../App.vue';
import ChatMain from '../page/user/ChatMain.vue';
import LoginMain from '../page/user/LoginMain.vue';
import ConsoleView from '../page/admin/Console.vue'
import TableView from '../page/admin/UserPage.vue'
import admin from '../page/admin/Admin.vue'

import UserManage from '../page/admin/user/Manage.vue'
import UserReport  from '../page/admin/user/Report.vue'
import UserForm  from '../page/admin/user/Form.vue'


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
        },
        {
            path:'/admin',
            component:admin,
            children: [
                {
                    path: 'user',
                    name: 'user',
                    children:[
                        {
                            path: 'manage',
                            name: 'UserManage',
                            component: UserManage
                        },
                        {
                            path: 'report',
                            name: 'UserReport',
                            component: UserReport
                        },
                        {
                            path: 'add',
                            component: UserForm // 同一个表单组件
                        },
                        {
                            path: 'edit/:id', // 动态 ID 路径
                            component: UserForm
                        }
                    ],
                },


                {
                    path: '',
                    redirect: 'admin/console'
                },
                {
                    path: 'console',
                    name: 'Console',
                    component: ConsoleView
                },
                {
                    path: 'table',
                    name: 'Table',
                    component: TableView
                }
            ]
        }
    ]
})

export default router