<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus'; // 导入 ElementPlus 组件
import LoginView from './components/LoginView.vue';
import TheSidebar from './components/TheSidebar.vue';
import TheChatWindow from './components/TheChatWindow.vue';
import UserCapsule from './components/UserCapsule.vue';
import { chat, test } from '@/api/msgdemo.js';


// --- 模拟数据 (Mock Data) ---
const MOCK_HISTORY = [
    { id: 1, title: "Vue3 Setup 语法糖问题", date: "刚刚" },
    { id: 2, title: "生成一幅赛博朋克图片", date: "昨天" },
    { id: 3, title: "解释量子纠缠", date: "2天前" },
    { id: 4, title: "Python 爬虫脚本优化", date: "1周前" },
];

const MOCK_MESSAGES = {
    1: [
        { role: 'user', type: 'text', content: 'Vue3 的 setup 语法糖有什么优势？', time: '10:00' },
        { role: 'ai', type: 'text', content: 'Vue 3 的 `<script setup>` 语法糖主要有以下优势：\n1. **更少的样板代码**：无需 `return` 暴露变量。\n2. **更好的 TypeScript 支持**：原生支持纯 TS 声明。\n3. **更好的运行时性能**：模板会被编译成更高效的渲染函数。\n\n需要我给你写个例子吗？', time: '10:01' }
    ],
    2: [
        { role: 'user', type: 'text', content: '生成一张图片：未来的上海。', time: '14:20' },
        { role: 'ai', type: 'text', content: '正在为你生成...', time: '14:20' },
        { role: 'ai', type: 'image', content: 'https://images.unsplash.com/photo-1480796927426-f609979314bd?q=80&w=2000&auto=format&fit=crop', time: '14:21' }
    ]
};
// --- End Mock Data ---


const isLoggedIn = ref(false);
const isDark = ref(false);

// 用户状态
const currentUser = reactive({
    name: '春乏夏困',
    avatar: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=100&h=100',
    level: 6,
    coins: 726.5,
    points: 104,
    isCheckedIn: false
});

// 聊天数据
const chatHistory = ref([...MOCK_HISTORY]);
const currentChatId = ref(1);
const messagesStore = reactive({ ...MOCK_MESSAGES });

// 计算属性
const currentMessages = computed(() => {
    return messagesStore[currentChatId.value] || [];
});

const currentChatTitle = computed(() => {
    const chat = chatHistory.value.find(c => c.id === currentChatId.value);
    return chat ? chat.title : '新对话';
});

// 方法
const handleLoginSuccess = () => {
    isLoggedIn.value = true;
};

const handleLogout = () => {
    ElMessageBox.confirm('确定要退出登录吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(() => {
        isLoggedIn.value = false;
    });
};

const updateThemeClass = () => {
    if (isDark.value) {
        document.documentElement.classList.add('dark');
    } else {
        document.documentElement.classList.remove('dark');
    }
};

const toggleTheme = ({ x, y }) => {
    // 使用 View Transitions API
    const isSupported = document.startViewTransition;

    if (!isSupported) {
        isDark.value = !isDark.value;
        updateThemeClass();
        return;
    }

    const endRadius = Math.hypot(
        Math.max(x, innerWidth - x),
        Math.max(y, innerHeight - y)
    );

    const transition = document.startViewTransition(() => {
        isDark.value = !isDark.value;
        updateThemeClass();
    });

    transition.ready.then(() => {
        const clipPath = [
            `circle(0px at ${x}px ${y}px)`,
            `circle(${endRadius}px at ${x}px ${y}px)`,
        ];
        document.documentElement.animate(
            {
                clipPath: isDark.value ? [...clipPath] : [...clipPath].reverse(),
            },
            {
                duration: 400,
                easing: 'ease-in',
                pseudoElement: isDark.value
                    ? '::view-transition-new(root)'
                    : '::view-transition-old(root)',
            }
        );
    });
};

const handleCheckIn = () => {
    if (currentUser.isCheckedIn) return;
    currentUser.isCheckedIn = true;
    currentUser.coins += 10;
    ElMessage.success('签到成功！硬币 +10');
};

const handleSelectChat = (id) => {
    currentChatId.value = id;
};

const handleCreateChat = () => {
    const newId = Date.now();
    chatHistory.value.unshift({
        id: newId,
        title: '新对话',
        date: '刚刚'
    });
    messagesStore[newId] = [];
    currentChatId.value = newId;
};

const handleDeleteChat = (id) => {
    ElMessageBox.confirm(
        '确定删除该对话记录吗?',
        '警告',
        {
            confirmButtonText: '删除',
            cancelButtonText: '取消',
            type: 'warning',
        }
    ).then(() => {
        chatHistory.value = chatHistory.value.filter(c => c.id !== id);
        delete messagesStore[id];
        if (currentChatId.value === id && chatHistory.value.length > 0) {
            currentChatId.value = chatHistory.value[0].id;
        }
        ElMessage.success('删除成功');
    });
};

const handleSendMessage = async (text) => {
    // 1. 添加用户消息
    if (!messagesStore[currentChatId.value]) {
        messagesStore[currentChatId.value] = [];
    }

    const now = new Date();
    const timeString = `${now.getHours()}:${now.getMinutes().toString().padStart(2, '0')}`;

    messagesStore[currentChatId.value].push({
        role: 'user',
        type: 'text',
        content: text,
        time: timeString
    });

    // 更新标题 (如果是第一条)
    const currentChat = chatHistory.value.find(c => c.id === currentChatId.value);
    if (currentChat && currentChat.title === '新对话') {
        currentChat.title = text.substring(0, 10) + (text.length > 10 ? '...' : '');
    }

    // 2. 模拟 AI 回复
    // 注意：在 SFC 中，你需要手动引入 Phorphr-icons 的类名，
    // 或者在 main.js 中全局注册/改用 ElementPlus icon/自定义 SVG
    // setTimeout(() => {
    //     messagesStore[currentChatId.value].push({
    //         role: 'ai',
    //         type: 'text',
    //         content: 'BOSS AI 收到你的消息: "' + text + '"\n这是一个模拟的回复。后端接入时替换此逻辑。',
    //         time: timeString
    //     });
    // }, 1000);


    // console.log(JSON.stringify({ "prompt": text, "chatId": currentChatId.value }));


    const res = test(text, currentChatId.value).then(res => {
        setTimeout(() => {
            messagesStore[currentChatId.value].push({
                role: 'ai',
                type: 'text',
                content: res,
                time: timeString
            });
        });

    })


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
            <login-view v-if="!isLoggedIn" @login-success="handleLoginSuccess"></login-view>

            <!-- 2. 聊天主界面 (占比85%) -->
            <div v-else
                class="w-[95%] lg:w-[85%] h-[90vh] flex rounded-3xl overflow-hidden shadow-2xl border border-white/20 dark:border-gray-700 relative z-10 transition-all duration-300">

                <!-- 左侧侧边栏 -->
                <the-sidebar :history="chatHistory" :current-chat-id="currentChatId" @select-chat="handleSelectChat"
                    @create-chat="handleCreateChat" @delete-chat="handleDeleteChat"></the-sidebar>

                <!-- 右侧聊天窗口 -->
                <the-chat-window :messages="currentMessages" :current-chat-title="currentChatTitle"
                    @send-message="handleSendMessage">
                    <!-- 把右上角的胶囊塞进去 -->
                    <template #header-right>
                        <user-capsule :user="currentUser" :is-dark="isDark" @toggle-theme="toggleTheme"
                            @logout="handleLogout" @check-in="handleCheckIn"></user-capsule>
                    </template>
                </the-chat-window>

            </div>
        </transition>
    </div>
</template>
<style>
/* App.vue 中可以放置组件特有的 CSS，但此处已移至 style.css */
/* 动画 keyframes 可以在 tailwind.config.js 中配置 */
</style>