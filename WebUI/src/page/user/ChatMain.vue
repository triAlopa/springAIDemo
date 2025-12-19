<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus'; // 导入 ElementPlus 组件
import TheSidebar from '@/components/TheSidebar.vue';
import TheChatWindow from '@/components/TheChatWindow.vue';
import UserCapsule from '@/components/UserCapsule.vue';
import { sendApi, queryMessages, storeMessageApi } from '@/api/msgdemo.js';
import { userInfoApi, userChangePassApi } from '@/api/user.js';
import { userQuerySessionApi, userDeleteSessionApi, userCreateSessionApi } from '@/api/session.js';
import { queryModelApi } from '@/api/model.js';
import { userOfferApi } from '@/api/offer.js';
import { useRouter } from 'vue-router';
import { nanoid } from 'nanoid'

const router = useRouter();
//是否为暗黑模式
const isDark = ref(false);
/**
 * 聊天数据
 */

//历史会话记录
/**
 * {
 *  id='',
 *  sessionId:'',
 *  sessionTitle:'',
 *  lastTime:'',
 *  modelId:''
 * }
 */
const chatSessionHistory = ref([]);
//当前会话id
const currentSessionId = ref();

//当前会话的所有聊天信息
const currentMessages = ref([])
//当前hr以及公司数据
const currentModel = ref({
    name: "",
    image: "",
    temperature: 0.0,
    company: {
        name: "",
        type: 0,
        lowSalary: 0,
        highSalar: 0,
        address: "",
        jobTag: [],
        jobDesc: "",
        employerBenefit: []
    }
});

//本地的所有聊天信息 sessionId+message
const messageStore = reactive([]);

// 当前用户状态
const currentUser = reactive({
    nickName: '',
    image: '',
    gender: 0,
    points: 0,
    email: '',
    registerTime: '',
    useDays: 0,
    isCheckedIn: false
});

//当前会话id
const currentBaseInfo = ref({
    sessionStatus: 1,
    userImage: '',
    AIImage: '',
});



/*
*查询个人信息
*/
const quetyUserInfo = async () => {
    return await userInfoApi()
        .then(result => {
            if (result.code == 401) {
                ElMessage.warning("请先登录")
                router.replace('/Login')
                return result;
            }
            if (result.code == 200) {
                let data = result.data;
                Object.assign(currentUser, data)
                currentUser.useDays = days.value
                //TODO 
                currentBaseInfo.value.userImage = currentUser.image
                console.log(result)
                console.log(currentUser)
                return result;
            } else {
                ElMessage.error(result.msg)
                currentUser.useDays = days.value
                return result;
            }
        }).catch(error => {
            console.log(error);
        })
}
/**
 * 前端计算用户使用天数
 */
const days = computed(() => {
    let userDate = Date.parse(currentUser.registerTime)
    let now = Date.now();
    return Math.round((now - userDate) / (1000 * 60 * 60 * 24));
})

/**
 * 修改用户密码相关
 */
const dialogVisible = ref(false)
const passworldForm = ref({ originPassword: '', changedPassword: '' })
const passworldRules = {
    originPassword: [
        { required: true, message: '请输入密码', trigger: 'change' }
    ],
    changedPassword: [
        { required: true, message: '请输入密码', trigger: 'change' },
        { min: 6, max: 50, message: '请输入不少于6位的修改密码', trigger: 'change' },
    ]
};
const passRuleRef = ref();
const handleChangUserPassworld = () => {
    console.log('用户需要修改密码')
    dialogVisible.value = true;
    resetPassForm();
}

const resetPassForm = () => {
    passworldForm.value.originPassword = '';
    passworldForm.value.changedPassword = '';
}

const handleCloseDialog = (done) => {
    ElMessageBox.confirm('确定关闭吗')
        .then(() => {
            passRuleRef.value.resetFields();
            done()
        })
        .catch(() => {
            console.log('隐忍Java，，，35 ,,996')
        })
}

//提交修改
const sumbit = async (form) => {
    if (!form) return;
    await form.validate(async (valid, fields) => {
        if (!valid) {
            console.log("连这两项都填不好，建议使用老人机")
            ElMessage.warning('按要求补完表单')
            return;
        } else {
            await userChangePassApi(passworldForm.value)
                .then(result => {
                    if (result.code == 200) {
                        ElMessage.success('修改成功')
                        dialogVisible.value = false;
                        resetPassForm();
                        //修改token
                        let token = result.data
                        localStorage.removeItem('userToken')
                        localStorage.setItem('userToken', JSON.stringify(token))
                    } else {
                        ElMessage.error('原密码错误')
                    }
                })
        }
    })

}



/**
 * 查询该用户的会话
 */
const getUserSession = async () => {
    await userQuerySessionApi()
        .then(result => {
            if (result.code == 200) {
                chatSessionHistory.value = result.data;
            } else {
                console.log(result)
                ElMessage.error(result.msg)
            }
        }).catch(error => {
            console.log(error);
        })
}


/**
 * 处理聊天数据存储在messageStore数组
 */
const queryStoreMessagesHandle = (selectSessionId) => {
    const index = messageStore.findIndex(item => item.sessionId == selectSessionId);
    console.log(selectSessionId, index);
    if (index >= 0) {
        return messageStore[index].messages;
    }
}

/**
 * 处理聊天会话存储在js数组
 */
const storeMessageHandle = (sessionId, messages) => {
    messageStore.unshift({ sessionId, messages })
    console.log(messageStore);
}

watch(currentSessionId, async (newSessionId) => {
    //优化用户切换的感受、、
    currentMessages.value = [];
    const messages = queryStoreMessagesHandle(newSessionId)
    //校验js数组有没有
    if (messages) {
        setTimeout(() => {
            currentMessages.value = messages;
        }, 250)
        console.log('本地已存在，无需查询')

        return;
    };

    console.log(newSessionId);
    queryCurrentMessages(newSessionId)

}, { deep: true })



const queryCurrentMessages = async (newSessionId) => {
    const result = await queryMessages(newSessionId)
        .catch(error => {
            console.log(error);
        });

    if (result.code == 200) {
        // console.log(result)
        let messages = result.data
        if (messages && messages.length > 0) {
            storeMessageHandle(newSessionId, messages);
        }
        currentMessages.value = result.data;
    } else {
        console.log(result.code);
        ElMessage.info(result.msg)
    }
    // console.log(result)
}

/*
* 计算会话的标题
*/
const currentChatTitle = computed(() => {
    const chat = chatSessionHistory.value.find(c => c.sessionId === currentSessionId.value);
    return chat ? chat.sessionTitle : '任意选择会话开始';
});

/*
*退出登录
*/
const handleLogout = () => {
    ElMessageBox.confirm('确定要退出登录吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(() => {
        localStorage.removeItem('userToken')
        router.replace('/Login')
        ElMessage.success('退出登陆成功')
    });
};

/*
*模式切换
*/
const updateThemeClass = () => {
    if (isDark.value) {
        document.documentElement.classList.add('dark');
    } else {
        document.documentElement.classList.remove('dark');
    }
};
/*
*模式切换动画
*/
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
/*
*签到
*/
const handleCheckIn = () => {
    if (currentUser.isCheckedIn) return;
    currentUser.isCheckedIn = true;
    currentUser.points += 10;
    ElMessage.success('签到成功！硬币 +10');
};


const queryCurrentSessionModel = async (modelId) => {
    await queryModelApi(modelId)
        .then(result => {
            if (result.code == 200) {
                currentModel.value = result.data;
                // console.log(result)
                currentBaseInfo.value.AIImage = currentModel.value.image
            } else {
                console.log(result.msg)
            }
        }).catch(error => {
            console.log(error)
        })
}

/*
*选择了某一个会话的函数
*/
const handleSelectChat = async (selectChat) => {
    currentSessionId.value = selectChat.sessionId;
    //聊天状态
    currentBaseInfo.value.sessionStatus = selectChat.enable
    console.log(selectChat)
    //
    let modelId = selectChat.modelId;
    //查询hr及公司
    await queryCurrentSessionModel(modelId);

    console.log(currentBaseInfo.value)
};

/**
 * 生成用户会话id
 */
const generateSessionId = () => {
    let sess_suffix = nanoid(6);
    let sess_perfix = 'sess_' + currentUser.email.substring(0, 4);
    return sess_perfix + '_' + sess_suffix;
}

/*
*添加会话
*/
const handleCreateChat = async () => {

    const id = generateSessionId();

    const newSession = {
        sessionId: id,
        sessionTitle: '新对话',
        modelId: '',
        lastTime: handleLocalTime(true),
        enable: 1,
    }
    console.log(newSession)

    await userCreateSessionApi(newSession)
        .then(result => {
            if (result.code == 200) {
                currentBaseInfo.value.sessionStatus=1
                let model = result.data;
                //当前创建的会话hr和公司
                currentModel.value = model;
                newSession.modelId = model.modelId;

                //切换为当前的会话
                currentSessionId.value = id;
                //标题
                currentChatTitle.value = model.name;
                newSession.sessionTitle = model.name;
                newSession.enable = 1

                //添加到本地显示
                chatSessionHistory.value.unshift(newSession);
                console.log(result.data)
            } else {
                console.log('不够你玩的吗，都申请完了😅😅')
                ElMessage.warning('服务器开了个小差😛~,请一段时间后重试')
            }
        }).catch(error => {
            console.log(error)
        })

};
/*
*删除信息
*/
const handleDeleteChat = (sessionId) => {
    ElMessageBox.confirm(
        '确定删除该对话记录吗?',
        '警告',
        {
            confirmButtonText: '删除',
            cancelButtonText: '取消',
            type: 'warning',
        }
    ).then(async () => {
        await userDeleteSessionApi(sessionId)
            .then(result => {
                if (result.code == 200) {
                    chatSessionHistory.value = chatSessionHistory.value.filter(c => c.sessionId !== sessionId);
                    if (currentSessionId.value === sessionId && chatSessionHistory.value.length > 0) {
                        currentSessionId.value = chatSessionHistory.value[0].sessionId;
                    }
                    ElMessage.success('删除成功!');
                }
            }).catch(error => {
                console.log(error)
            })

    });
};

const handleLocalTime = (isSession) => {
    let now = new Date();
    let year = now.getFullYear();
    let month = (now.getMonth() + 1).toString().padStart(2, '0');
    let day = now.getDate().toString().padStart(2, '0');
    if (isSession) {
        return `${year}-${month}-${day}`;
    }
    //yy-MM-dd HH:mm:ss
    year = now.getFullYear().toString().substring(2, 4);
    let hours = now.getHours().toString().padStart(2, '0');
    let minutes = now.getMinutes().toString().padStart(2, '0');
    let seconds = now.getSeconds().toString().padStart(2, '0');

    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;;
}

const changeImage=(image)=>{
    console.log(image)
    currentBaseInfo.value.userImage=image
    currentUser.image=image
}


/*
*发送信息
*/
const handleSendMessage = async (text) => {


    console.log(currentMessages.value)


    currentMessages.value = queryStoreMessagesHandle(currentSessionId.value);
    // 1. 添加用户消息
    if (!currentMessages.value) {
        currentMessages.value = [];
    }


    const timeString = handleLocalTime(false);
    /* messageStore[currentSessionId.value].push({
        type: 'USER',
        contentType: 'text',
        textContent: text,
        createdTime: timeString
    }); */


    const currentUserSendMsg = {
        type: 'USER',
        contentType: 'text',
        textContent: text,
        createdTime: timeString
    }

    currentMessages.value.push(currentUserSendMsg);
    await handleAIResponseMessage(text);

    setTimeout(() => {
        queryCurrentMessages(currentSessionId.value);
    }, 400);

};

const handleAIResponseMessage = async (text) => {
    const timeString = handleLocalTime(false);
    const currentASSISTANTSendMsg = {
        type: 'ASSISTANT',
        contentType: 'text',
        textContent: '',
        createdTime: timeString
    }
    currentMessages.value.push(currentASSISTANTSendMsg);

    //存储在前端数组里面
    storeMessageHandle(currentSessionId.value, currentMessages.value)

    console.log(messageStore[currentSessionId.value])

    // const aiMessage = messageStore[currentSessionId.value][messageStore[currentSessionId.value].length - 1];
    const aiMessage = currentMessages.value[currentMessages.value.length - 1];

    // 3. 使用原生 Fetch API 处理流
    try {
        let userForJson = localStorage.getItem("userToken")

        let userToken = JSON.parse(userForJson)
        const result = await sendApi(text, currentSessionId.value, userToken);

        console.log(result)
        if (!result.ok) {
            throw new Error(`HTTP error! status: ${result.status}`);
        }
        // 确保响应体是流 (ReadableStream)
        const reader = result.body.getReader();
        const decoder = new TextDecoder('utf-8');
        let buffer = '';
        while (true) {
            const { done, value } = await reader.read();

            if (done) {
                console.log('Stream finished.');
                console.log(aiMessage.textContent);
                //此番交互完毕,发出请求保存用户与ai这一次聊天记录
                storeMessageApi(aiMessage, currentSessionId.value);

                break;
            }

            buffer += decoder.decode(value, { stream: true });

            // --- 修正 SSE 数据包解析：使用单换行符分割，以处理服务器的不规范发送 ---
            const lines = buffer.split('\n\n');
            buffer = lines.pop() || ''; // 保留最后一个不完整的行

            for (const line of lines) {

                // 1. 严格处理每一行，清理首尾空白
                const trimmedLine = line;
                console.log(trimmedLine)

                //2. 忽略空行或非数据行
                if (!trimmedLine) {
                    continue;
                }
                // 3. 提取 data 部分：移除 "data:" 并清理首尾空格
                let dataString = trimmedLine.substring(5);

                if (!dataString) {
                    continue; // 忽略内容为空的数据行 (例如 data: )
                }
                dataString = dataString.replaceAll('data:', '');
                aiMessage.textContent += dataString;
                console.log('add')
            }

        }
    } catch (error) {
        ElMessage.error(`[连接或流式错误: ${error.message}]`);
    }
}


const handleOfferClick = async (type) => {
    console.log(type)

    if (!type) return;
    if (currentBaseInfo.value.sessionStatus == 0) {
        ElMessage.warning('此聊天已结束，美美收offer🥳')
        return;
    }

    if (type == 'accept') {
        await userOfferApi(1, currentSessionId.value)
            .then(result => {
                if (result.code == 200) {
                    currentBaseInfo.value.sessionStatus = 0;
                    console.log(currentBaseInfo.value)
                    

                    ElMessage.success('欢迎加入我们。请检查你的邮箱确保信息无误🥳')
                }else{
                    ElMessage.warning('网络有些波动，稍后重试')
                }
            })
        //const session = chatSessionHistory.value.filter(chat => chat.sessionId == currentSessionId.value);



        // if (!session) {
        //     console.log('not find error')
        //     return;
        // }




    }
};




const userInitInfo = async () => {
    try {
        const result = await quetyUserInfo();
        console.log(result)
        if (result && result.code == 200) {
            await getUserSession();
        } else {
            console.log('查询失败:', result);
        }
    } catch (error) {
        console.error('执行出错:', error);
    }
}

onMounted(async () => {
    await userInitInfo();
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

            <!-- 2. 聊天主界面 (占比85%) -->
            <div
                class="w-[95%] lg:w-[85%] h-[90vh] flex rounded-3xl overflow-hidden shadow-2xl border border-white/20 dark:border-gray-700 relative z-10 transition-all duration-300">

                <!-- 左侧侧边栏 -->
                <the-sidebar :history="chatSessionHistory" :current-chat-id="currentSessionId"
                    @select-chat="handleSelectChat" @create-chat="handleCreateChat"
                    @delete-chat="handleDeleteChat"></the-sidebar>

                <!-- 右侧聊天窗口 -->
                <the-chat-window :messages="currentMessages" :current-chat-title="currentChatTitle"
                    :currentBaseInfo="currentBaseInfo" @send-message="handleSendMessage" :currentModel="currentModel"
                    :is-dark="isDark" @handleOffer="handleOfferClick">
                    <!-- 右上角的胶囊  -->
                    <template #header-right>
                        <user-capsule :user="currentUser" :is-dark="isDark" @toggle-theme="toggleTheme"
                            @logout="handleLogout" @check-in="handleCheckIn"
                            @changePass="handleChangUserPassworld"
                            @changeImage="changeImage"></user-capsule>
                    </template>
                </the-chat-window>

            </div>
        </transition>
    </div>

    <el-dialog v-model="dialogVisible" title="修改密码" width="500" :before-close="handleCloseDialog">
        <el-form ref="passRuleRef" style="max-width: auto" :model="passworldForm" :rules="passworldRules"
            label-width="auto">
            <el-form-item label="修改前" prop="originPassword">
                <el-input v-model="passworldForm.originPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="修改后" prop="changedPassword">
                <el-input v-model="passworldForm.changedPassword" type="password" show-password />
            </el-form-item>
        </el-form>
        <div class="dialog-footer">
            <el-button @click="sumbit(passRuleRef)">提交</el-button>
            <el-button type="primary" @click="dialogVisible = false">
                离开
            </el-button>
        </div>
    </el-dialog>
</template>
<style></style>