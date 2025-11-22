<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';



const emit = defineEmits(['login-success']);

const isRegister = ref(false);
const typedText = ref('');
const fullText = "欢迎来到 BOSS AI。\n这里是思维的延伸，创意的起点。\n准备好开始探索了吗？";

// 打字机效果 (逻辑不变)
let typeInterval = null;
const startTyping = () => {
    let i = 0;
    clearInterval(typeInterval);

    typeInterval = setInterval(() => {
        if (i <= fullText.length) {
            typedText.value = fullText.slice(0, i);
            i++;
            if (i > fullText.length) {
                clearInterval(typeInterval);
            }
        }
    }, 100);
};

onMounted(() => {
    startTyping();
});

const form = reactive({
    email: '',
    password: '',
    confirmPassword: '',
    captcha: '',
    emailCode: ''
});

const captchaUrl = ref('https://dummyimage.com/100x40/e0e0e0/555555&text=A3d9');

const toggleMode = () => {
    isRegister.value = !isRegister.value;
    form.email = '';
    form.password = '';
};

const refreshCaptcha = () => {
    const random = Math.floor(Math.random() * 1000);
    captchaUrl.value = `https://dummyimage.com/100x40/e0e0e0/555555&text=${random}`;
};

const submit = () => {
    if (!form.email || !form.password) {
        ElMessage.warning('请填写完整信息');
        return;
    }
    setTimeout(() => {
        emit('login-success');
        ElMessage.success(isRegister.value ? '注册成功并登录' : '登录成功');
    }, 800);
};
</script>

<template>
    <div class="w-full h-full flex bg-white dark:bg-gray-900 relative z-20">
        <div
            class="w-[85%] h-full relative flex flex-col justify-center items-center p-20 overflow-hidden bg-gradient-to-br from-blue-50 to-indigo-50 dark:from-gray-900 dark:to-gray-800">
            <div
                class="absolute inset-0 bg-[url('https://images.unsplash.com/photo-1620641788421-7a1c342ea42e?q=80&w=2574&auto=format&fit=crop')] bg-cover opacity-10 dark:opacity-5">
            </div>

            <h1
                class="text-6xl font-bold mb-8 bg-clip-text text-transparent bg-gradient-to-r from-blue-500 to-purple-600 tracking-tighter">
                BOSS AI</h1>
            <div class="h-32 text-2xl text-gray-600 dark:text-gray-300 font-mono text-center max-w-3xl leading-relaxed">
                {{ typedText }}<span class="animate-pulse text-blue-500">|</span>
            </div>
        </div>

        <div
            class="w-[15%] min-w-[300px] h-full bg-white/80 dark:bg-gray-800/90 backdrop-blur-md border-l border-gray-200 dark:border-gray-700 flex flex-col p-6 shadow-2xl z-30">
            <div class="flex-1 flex flex-col justify-center">
                <h2 class="text-2xl font-bold mb-6 text-center">{{ isRegister ? '加入我们' : '欢迎回来' }}</h2>

                <el-form :model="form" class="space-y-4">
                    <el-input v-model="form.email" placeholder="邮箱" size="large">
                        <template #prefix><el-icon>
                               <Message />
                            </el-icon></template>
                    </el-input>

                    <el-input v-model="form.password" type="password" placeholder="密码" size="large" show-password>
                        <template #prefix><el-icon>
                                <Lock />
                            </el-icon></template>
                    </el-input>

                    <template v-if="isRegister">
                        <el-input v-model="form.confirmPassword" type="password" placeholder="确认密码" size="large">
                            <template #prefix><el-icon>
                                    <Lock />
                                </el-icon></template>
                        </el-input>

                        <div class="flex gap-2">
                            <el-input v-model="form.captcha" placeholder="验证码" class="flex-1">
                            </el-input>
                            <div @click="refreshCaptcha"
                                class="w-20 h-9 bg-gray-200 cursor-pointer flex items-center justify-center text-xs tracking-widest font-bold italic select-none rounded overflow-hidden">
                                <img :src="captchaUrl" class="w-full h-full object-cover opacity-80" />
                            </div>
                        </div>

                        <el-input v-model="form.emailCode" placeholder="邮箱验证码">
                            <template #append>
                                <el-button size="small">发送</el-button>
                            </template>
                        </el-input>
                    </template>

                    <el-button type="primary"
                        class="w-full !h-10 !text-lg !rounded-xl !bg-[#00b1eb] hover:!bg-[#009acb] !border-none shadow-lg shadow-blue-200 dark:shadow-none transition-transform active:scale-95"
                        @click="submit">
                        {{ isRegister ? '立即注册' : '登录' }}
                    </el-button>
                </el-form>

                <div class="mt-6 text-center text-sm text-gray-500">
                    {{ isRegister ? '已有账号?' : '还没有账号?' }}
                    <span @click="toggleMode" class="text-[#00b1eb] cursor-pointer hover:underline font-medium ml-1">
                        {{ isRegister ? '去登录' : '去注册' }}
                    </span>
                </div>
            </div>
        </div>
    </div>
</template>