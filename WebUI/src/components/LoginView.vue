<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { registerApi } from '@/api/user.js';
import { rule } from 'postcss';

// Define events to emit to the parent component
const emit = defineEmits(['login-success'])

const isRegister = ref(false)
const typedText = ref('')
const fullText = "欢迎来到 BOSS AI。\n这里是思维的延伸，创意的起点。\n准备好开始探索了吗？"

// Typing effect logic
let typeInterval = null

const startTyping = () => {


    typedText.value = ''
    let i = 0
    let isDeleting = false
    clearInterval(typeInterval)

    typeInterval = setInterval(() => {
        if (!isDeleting && i <= fullText.length) {
            typedText.value = fullText.slice(0, i)
            i++
            if (i > fullText.length) {
                isDeleting = true
                setTimeout(() => { /* Pause */ }, 2000)
            }
        } else if (isDeleting && i >= 0) {
            clearInterval(typeInterval)
        }
    }, 100)


}

onMounted(() => {
    startTyping()
})

const form = reactive({
    email: '',
    password: '',
    confirmPassword: '',
    captcha: '',
    emailCode: ''
})

const registerForm = reactive({
    nickName: '',
    email: '',
    password: '',
    confirmPassword: '',
    gender: '',
    emailCode: '',
    birthday: ''
})


const captchaUrl = ref('https://dummyimage.com/100x40/e0e0e0/555555&text=A3d9')

const toggleMode = () => {
    isRegister.value = !isRegister.value
    registerForm.email = ''
    registerForm.password = ''
}

const refreshCaptcha = () => {
    const random = Math.floor(Math.random() * 1000)
    captchaUrl.value = `https://dummyimage.com/100x40/e0e0e0/555555&text=${random}`
}

const validatorEmail = (rule, value, callback) => {
    var regex = /^\w+([-+.']\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/g;
    if (!regex.exec(value)) {
        console.log('111')
        callback(new Error('邮箱格式错误'))
    }
    callback();
}

const validatorPassword = (rule, value, callback) => {
    if (!value) {
        callback(new Error('重复输入密码'))
    }

    if (value != registerForm.password) {
        callback(new Error('密码不一致！'))
    }
    callback();
}
const roleFromRef = ref()
//注册表单校验规则
const rules = ref({
    nickName: [
        { required: true, message: '请输入你的昵称', trigger: 'blur' },
        { min: 2, max: 32, message: '请在2~32位之间', trigger: 'blur' }
    ],
    email: [
        { required: true, message: '请输入你的邮箱', trigger: 'blur' },
        { validator: validatorEmail, message: '邮箱格式错误', trigger: 'blur' }
    ],
    emailCode: [
        { required: true, message: '请输入邮箱验证码', trigger: 'change' }
    ],
    gender: [
        { required: true, message: '请输入你的性别', trigger: 'change' }
    ],
    password: [
        { required: true, message: '请输入你的密码', trigger: 'blur' }
    ],
    confirmPassword: [
        { required: true, message: '请重复输入你的密码', trigger: 'blur' },
        { validator: validatorPassword, message: '密码不一致！', trigger: 'change' }
    ],
    birthday: [
        { required: true, message: '请输入生日', trigger: 'blur' }
    ]
})

const submit = async (roleFromRef) => {

    if (!roleFromRef) return;

    await roleFromRef.validate(async (valid, value) => {
        if (valid) {
            await registerApi(registerForm)
                .then(result => {
                    if (result.code != 200) {
                        //前后端统一使用 409作为重复注册状态码
                        if(result.code==409){
                            ElMessage.error("重复注册的账号："+result.msg)
                            return;
                        }
                        console.log(result.msg)
                        ElMessage.warning('注册失败，请检查数据！');
                        return;
                    } else {
                        setTimeout(() => {
                            emit('login-success')
                            ElMessage.success(isRegister.value ? '注册成功并登录' : '登录成功')
                        }, 800)
                    }
                }).catch(error => {
                    ElMessage.warning(error);
                })
        } else {
            ElMessage.error('非法注册!');
            return;
        }
    })


    /* if (result.code == 200) {
        alert(' 11')
        console.log(result.msg)
        ElMessage.warning(result.msg);
    } else {
        setTimeout(() => {
            emit('login-success')
            ElMessage.success(isRegister.value ? '注册成功并登录' : '登录成功')
        }, 800)
    } */
}
</script>

<template>
    <!-- Fix: Change background to darkBg in dark mode -->
    <div class="w-full h-full flex bg-white dark:bg-darkBg relative z-20">
        <!-- Left Side: Introduction and Typing Effect -->
        <div
            class="w-[85%] h-full relative flex flex-col justify-center items-center p-20 overflow-hidden bg-gradient-to-br from-blue-50 to-indigo-50 dark:from-[#181818] dark:to-[#222222]">
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

        <!-- Right Side: Login/Registration Form -->
        <!-- Fix: Login box background and border color using darkSurface -->
        <div
            class="w-[15%] min-w-[300px] h-full bg-white/80 dark:bg-darkSurface/90 backdrop-blur-md border-l border-gray-200 dark:border-gray-700 flex flex-col p-6 shadow-2xl z-30">
            <div class="flex-1 flex flex-col justify-center">
                <h2 class="text-2xl font-bold mb-6 text-center">{{ isRegister ? '加入我们' : '欢迎回来' }}</h2>

                <el-form :model="registerForm" class="space-y-4" :rules="rules" ref="roleFromRef">
                    <!-- 注册表单 -->
                    <el-form-item prop="nickName" style="margin: 15px 0px;">
                        <el-input v-model="registerForm.nickName" placeholder="用户名称" size="large">
                            <template #prefix><el-icon>
                                    <User />
                                </el-icon></template>
                        </el-input>
                    </el-form-item>
                    <el-form-item prop="email" style="margin: 15px 0px;">
                        <el-input v-model="registerForm.email" placeholder="邮箱" size="large">
                            <template #prefix><el-icon>
                                    <Message />
                                </el-icon></template>
                        </el-input>
                    </el-form-item>
                    <el-form-item prop="password" style="margin: 15px 0px;">
                        <el-input v-model="registerForm.password" type="password" placeholder="密码" size="large"
                            show-password>
                            <template #prefix><el-icon>
                                    <Lock />
                                </el-icon></template>
                        </el-input>
                    </el-form-item>
                    <div v-if="isRegister">
                        <el-form-item prop="confirmPassword" style="margin: 15px 0px;">
                            <el-input v-model="registerForm.confirmPassword" type="password" placeholder="确认密码"
                                show-password size="large">
                                <template #prefix><el-icon>
                                        <Lock />
                                    </el-icon></template>
                            </el-input>
                        </el-form-item>
                        <el-form-item prop="emailCode" style="margin: 15px 0px;">
                            <el-input v-model="registerForm.emailCode" placeholder="邮箱验证码">
                                <template #append>
                                    <el-button size="small">发送</el-button>
                                </template>
                            </el-input>
                        </el-form-item>
                        <el-form-item prop="gender" style="margin: 15px 0px;">
                            <el-radio-group v-model="registerForm.gender">
                                <el-radio value="1" size="small">男</el-radio>
                                <el-radio value="0" size="small">女</el-radio>
                            </el-radio-group>
                        </el-form-item>
                        <el-form-item prop="birthday" style="margin-top: 15px; margin-bottom: -8px;">
                            <el-date-picker v-model="registerForm.birthday" type="date" placeholder="生日"
                                size="defualut" />
                        </el-form-item>
                    </div>

                    <el-form-item prop="name" style="margin: 7px 0px;">
                        <div class="flex gap-2" v-if="!isRegister">

                            <el-input v-model="form.captcha" placeholder="验证码" class="flex-1">
                            </el-input>
                            <div @click="refreshCaptcha"
                                class="w-20 h-9 bg-gray-200 cursor-pointer flex items-center justify-center text-xs tracking-widest font-bold italic select-none rounded overflow-hidden">
                                <img :src="captchaUrl" class="w-full h-full object-cover opacity-80" />
                            </div>

                        </div>
                    </el-form-item>


                    <el-button type="primary"
                        class="w-full !h-10 !text-lg !rounded-xl !bg-[#00b1eb] hover:!bg-[#009acb] !border-none shadow-lg shadow-blue-200 dark:shadow-none transition-transform active:scale-95"
                        @click="submit(roleFromRef)">
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