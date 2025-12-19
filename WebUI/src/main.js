// src/main.js
import { createApp } from 'vue';
import App from './App.vue';
import ElementPlus from 'element-plus';
import router from './router/index.js';
import * as ElementPlusIconsVue from '@element-plus/icons-vue'; // 导入所有图标
import 'element-plus/dist/index.css';
import 'element-plus/theme-chalk/dark/css-vars.css';
import zhCn from 'element-plus/dist/locale/zh-cn.mjs';

import './style.css'; 

const app = createApp(App);

// 关键步骤：全局注册所有 Element Plus Icons
// 确保所有组件都能直接使用 <IconName /> 或 <el-icon><IconName /></el-icon>
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component);
}

app.use(ElementPlus,{ locale: zhCn });
app.use(router);
app.mount('#app');