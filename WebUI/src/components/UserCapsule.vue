<script setup>
import { computed } from 'vue';
// ...
import { Sunny, Moon, Message } from '@element-plus/icons-vue';
const props = defineProps({
    user: {
        type: Object,
        required: true
    },
    isDark: {
        type: Boolean,
        required: true
    }
});

const emit = defineEmits(['toggle-theme', 'logout', 'check-in','changePass']);

const handleToggleTheme = (event) => {
    // 获取点击位置坐标，用于实现扩散动画
    const x = event.clientX;
    const y = event.clientY;
    emit('toggle-theme', { x, y });
};
</script>

<template>
    <div class="flex items-center gap-4">
        <button @click="handleToggleTheme" class="relative w-14 h-7 rounded-full bg-gray-200 dark:bg-gray-700 transition-colors overflow-hidden border border-gray-300 dark:border-gray-600 shadow-inner flex items-center px-1">
            <div class="absolute inset-0 bg-[#00b1eb]/10 dark:bg-purple-500/10"></div>
            <div class="w-5 h-5 rounded-full bg-white shadow-md transform transition-transform duration-500 flex items-center justify-center z-10"
                 :class="isDark ? 'translate-x-7' : 'translate-x-0'">
               <el-icon class="text-xs" :class="isDark ? 'text-indigo-500' : 'text-orange-400'"><component :is="isDark ? Moon : Sunny" /></el-icon>
            </div>
        </button>

        <el-popover
            placement="bottom-end"
            :width="320"
            trigger="hover"
            popper-class="!p-0 !rounded-2xl !border-0 !shadow-2xl"
            :show-arrow="false"
            transition="el-zoom-in-top"
        >
            <template #reference>
                <div class="flex items-center gap-3 pl-1 pr-4 py-1 bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-full shadow-sm cursor-pointer hover:shadow-md hover:border-blue-300 transition-all group">
                    <div class="w-8 h-8 rounded-full overflow-hidden border border-gray-100">
                        <img :src="user.image" class="w-full h-full object-cover group-hover:scale-110 transition-transform">
                    </div>
                    <div class="flex flex-col" >
                        <span class="text-xs font-bold text-gray-700 dark:text-gray-200">{{ user.nickName }}</span>
                        <span class="text-[10px] text-gray-400">
                            <el-tag v-if="user.gender==1" type="primary" size="small" round>男</el-tag>
                            <el-tag v-else-if="user.gender==0" type="danger" size="small" round>女</el-tag>
                        </span>
                    </div>
                    <el-icon class="text-xs text-gray-400"><CaretBottom /></el-icon>
                </div>
            </template>
            
            <div class="bg-white dark:bg-gray-800 rounded-2xl overflow-hidden">
                <div class="h-20 bg-gradient-to-r from-pink-300 to-blue-300 relative">
                    <div class="absolute -bottom-8 left-6 w-16 h-16 rounded-full border-2 border-white dark:border-gray-800 overflow-hidden shadow-lg z-10">
                        <img :src="user.image" class="w-full h-full object-cover">
                    </div>
                </div>
                
                <div class="pt-10 px-6 pb-6">
                    <div class="flex justify-between items-start">
                        <div>
                            <h3 class="font-bold text-lg dark:text-white">{{ user.nickName }}</h3>
                            <div class="text-xs text-gray-400 mt-1">积分: {{ user.points }} </div>
                        </div>
                        <button 
                            @click="$emit('check-in')"
                            :disabled="user.isCheckedIn"
                            class="px-4 py-1.5 rounded-full text-xs font-bold text-white transition-all shadow-md"
                            :class="user.isCheckedIn ? 'bg-gray-300 cursor-not-allowed' : 'bg-[#00b1eb] hover:bg-[#009acb] hover:shadow-blue-200/50 active:scale-95'"
                        >
                            {{ user.isCheckedIn ? '已签到' : '签到' }}
                        </button>
                    </div>

                    <div class="flex justify-between mt-6 px-2">
                        <div class="text-center cursor-pointer hover:text-[#00b1eb] transition-colors">
                            <div class="text-xs text-gray-400">性别</div>
                            <div class="font-bold text-lg dark:text-gray-200">{{user.gender===1?'男':'女'}}</div>
                        </div>
                    </div>

                    <div class="mt-6 space-y-1">
                        <div class="flex items-center justify-between p-2 rounded hover:bg-gray-50 dark:hover:bg-gray-700 cursor-pointer transition-colors group">
                            <span class="text-sm text-gray-600 dark:text-gray-300 flex items-center gap-2">
                               <el-icon class="group-hover:text-[#00b1eb]">
                                <Message /></el-icon>{{ user.email }}
                            </span>
                        </div>
                        <div class="flex items-center justify-between p-2 rounded hover:bg-gray-50 dark:hover:bg-gray-700 cursor-pointer transition-colors group">
                            <span class="text-sm text-gray-600 dark:text-gray-300 flex items-center gap-2">
                               <el-icon class="group-hover:text-[#00b1eb]">
                                <Calendar /></el-icon>您已经注册了{{ user.useDays}}天
                            </span>
                        </div>
                         <div class="flex items-center justify-between p-2 rounded hover:bg-gray-50 dark:hover:bg-gray-700 cursor-pointer transition-colors group">
                            <span class="text-sm text-gray-600 dark:text-gray-300 flex items-center gap-2">
                               <el-icon class="group-hover:text-[#00b1eb]"><Setting /></el-icon>修改密码
                            </span>
                            <el-icon class="text-xs text-gray-400"><Right @click="$emit('changePass')" /></el-icon>
                        </div>
                    </div>
                    
                    <div class="mt-4 pt-4 border-t border-gray-100 dark:border-gray-700">
                        <button @click="$emit('logout')" class="w-full text-left text-sm text-red-500 hover:bg-red-50 dark:hover:bg-red-900/20 p-2 rounded transition-colors">
                            <el-icon class="mr-2"><SwitchButton /></el-icon> 退出登录
                        </button>
                    </div>
                </div>
            </div>
        </el-popover>
    </div>
</template>