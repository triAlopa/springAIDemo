<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus'; // 导入 ElementPlus 组件
import TheSidebar from '../components/TheSidebar.vue';
import TheChatWindow from '../components/TheChatWindow.vue';
import UserCapsule from '../components/UserCapsule.vue';
import { sendApi, queryMessages ,storeMessageApi} from '@/api/msgdemo.js';
import { userInfoApi, userChangePassApi } from '@/api/user.js';
import { userQuerySessionApi, userDeleteSessionApi, userCreateSessionApi } from '@/api/session.js';
import { useRouter } from 'vue-router';
import { nanoid } from 'nanoid'

const router = useRouter();
const MOCK_MESSAGES = {
    1: [
        { role: 'user', type: 'text', content: 'Vue3 的 setup 语法糖有什么优势？', time: '10:00' },
        { role: 'ai', type: 'text', content: 'Vue 3 的 `<script setup>` 语法糖主要有以下优势：\n1. **更少的样板代码**：无需 `return` 暴露变量。\n2. **更好的 TypeScript 支持**：原生支持纯 TS 声明。\n3. **更好的运行时性能**：模板会被编译成更高效的渲染函数。\n\n需要我给你写个例子吗？', time: '10:01' },
        { role: 'ai', type: 'text', content: '量子纠缠是量子力学中一种奇特的现象，两个或多个粒子在相互作用后形成一种“纠缠态”，此时它们的量子状态会紧密关联，无论相隔多远。当一个粒子的状态被测量时，另一个粒子的状态会**瞬间**确定（例如自旋方向），仿佛存在某种超距联系。\n\n### 核心特点：\n1. **瞬时性**：纠缠粒子的状态关联是瞬时的，不受距离限制（爱因斯坦称之为“鬼魅般的超距作用”）。\n2. **随机性**：单个粒子的状态在测量前是随机的，但纠缠对的状态始终互斥（如一个自旋向上，另一个必然向下）。\n3. **不可分割**：纠缠态是整体性的，无法单独描述单个粒子状态。\n\n### 通俗比喻：\n想象一对“量子骰子”：\n- 单独看，每个骰子的点数都是随机的。\n- 但当它们纠缠时，只要测量其中一个骰子为“3”，另一个无论多远都会**瞬间**变成“4”（总和固定为7）。\n\n### 重要说明：\n- ❌ **不是信息传递**：量子纠缠无法传递经典信息（无法控制测量结果）。\n- ✅ **应用场景**：量子通信（量子密钥分发）、量子计算的基础之一。\n\n量子纠缠揭示了自然界的非局域性，至今仍是物理学和哲学的研究热点。 🌌', time: '10:01' }
    ],
    2: [
        { role: 'user', type: 'text', content: '生成一张图片：未来的上海。', time: '14:20' },
        { role: 'ai', type: 'text', content: '正在为你生成...', time: '14:20' },
        { role: 'ai', type: 'image', content: 'https://images.unsplash.com/photo-1480796927426-f609979314bd?q=80&w=2000&auto=format&fit=crop', time: '14:21' },
        { role: 'ai', type: 'text', content: '😄 Java 确实很卷，但卷得很有道理！作为常年占据 TIOBE 榜首的语言，Java 的生态成熟、岗位需求大，但也意味着竞争激烈。不过换个角度看：\n\n**卷在哪里？**  \n- 初级岗位扎堆，技术栈要求越来越高（Spring Cloud/Alibaba、分布式、高并发...）  \n- 技术更新快（JDK 21虚拟线程、GraalVM、Spring AI 等新特性需持续跟进）  \n\n**破局关键：**  \n✅ **差异化能力**：深耕云原生（K8s+DevOps）、大数据（Flink）、或结合AI（Spring AI应用）  \n✅ **场景落地**：能解决实际痛点（如用Project Reactor优化高并发，而非只会背八股文）  \n\n**举个栗子🌰**：  \n用Java玩转AI——Spring AI整合LangChain4j，5行代码调用大模型：  \n```java\nAiClient client = new OpenAiChatClient(apiKey);  \nString response = client.generate(\"用Java实现快速排序\");  \nSystem.out.println(response);  \n', time: '14:21' }
    ],
    3: [
        { role: 'ai', type: 'text', content: '哈哈，您这风格切换得真快！不过没问题，我可是「热情话多」模式全开版AI 😄\n\n您都喊「Hello World」了，这不得来个经典开场？👇\n\n```java\npublic class HelloWorld {\n    public static void main(String[] args) {\n        System.out.println(\"🌈 你好，魔法世界！(Hello World from a passionate AI!)\"); \n    }\n}\n```\n\n**输出结果**：  \n`🌈 你好，魔法世界！(Hello World from a passionate AI!)`\n\n需要我变个 Spring AI 的魔法？聊技术？解 Bug？甚至写首诗？随时待命！✨ 您尽管吩咐~', time: '20:20' },
        { role: 'ai', type: 'text', content: '哈哈，承蒙夸奖！不过地狱笑话需谨慎，来一个程序员特供版（轻量级）：  \n\n**“为什么程序员下地狱后反而效率更高？  \n——因为地狱里的需求永不‘404’，客户终于学会一次性说清需求了。”**  \n\n（温馨提示：幽默有风险，玩笑需边界，本笑话绝不针对任何真实客户或程序员群体 😉）', time: '20:20' },
        { role: 'ai', type: 'text', content: '好的！以下是三种语言的 \"Hello, World!\" 代码：\n\n**1. C++**\n```cpp\n#include <iostream>\n\nint main() {\n    std::cout << \"Hello, World!\" << std::endl;\n    return 0;\n}\n```\n* **编译运行：** 需要编译器（如 g++），命令通常是 `g++ hello.cpp -o hello` 然后 `./hello`\n\n**2. Python**\n```python\nprint(\"Hello, World!\")\n```\n* **运行：** 直接执行，命令 `python hello.py` (或 `python3 hello.py`)\n\n**3. C#**\n```csharp\nusing System;\n\nclass Program {\n    static void Main() {\n        Console.WriteLine(\"Hello, World!\");\n    }\n}\n```\n* **编译运行：** 需要 .NET SDK，命令 `dotnet run` (如果作为控制台应用项目) 或使用 Visual Studio 等 IDE。\n\n希望这些简洁的示例对你有帮助！"', time: '20:20' },
        { role: 'ai', type: 'text', content: '', time: '20:20' }
    ]
};

// 聊天数据
//历史会话记录
const chatSessionHistory = ref([]);
/**
 * 
 */
const currentSessionId = ref();
const messagesStore = reactive({ ...MOCK_MESSAGES });


const isDark = ref(false);

// 用户状态
const currentUser = reactive({
    nickName: '',
    image: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=100&h=100',
    gender: 0,
    points: 0,
    email: '',
    registerTime: '',
    useDays: 0,
    isCheckedIn: false
});

const days = computed(() => {
    let userDate = Date.parse(currentUser.registerTime)
    let now = Date.now();
    return Math.round((now - userDate) / (1000 * 60 * 60 * 24));
})

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
                currentUser.image = 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=100&h=100'
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
    // passRuleRef.resetFields()
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

const messageStore = reactive([]);

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


/**
 * 无需.value
 */
const currentMessages = ref([])
watch(currentSessionId, async (newSessionId) => {
    //优化用户切换的感受、、
    currentMessages.value = [];
    const messages = queryStoreMessagesHandle(newSessionId)
    if (messages) {
        setTimeout(() => {
            currentMessages.value = messages;
        }, 250)
        console.log('本地已存在，无需查询')
        return;
    };

    console.log(newSessionId);
    const result = await queryMessages(newSessionId)
        .catch(error => {
            console.log(error);
        });

    if (result.code == 200) {
        console.log(result)
        let data = result.data
        if (data && data.length > 0) {
            storeMessageHandle(newSessionId, data);
        }
        currentMessages.value = result.data;
    } else {
        console.log(result.code);
        ElMessage.info(result.msg)
    }
    console.log(result)
}, { deep: true })

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
/*
*选择了某一个会话的函数
*/
const handleSelectChat = (sessionId) => {
    currentSessionId.value = sessionId;
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
const handleCreateChat = async() => {

    const id = generateSessionId();

    const newSession = {
        sessionId: id,
        sessionTitle: '新对话',
        lastTime:handleLocalTime.value,
    }
    console.log(newSession)

    await userCreateSessionApi(newSession)
        .then(result => {
            if (result.code == 200) {
                chatSessionHistory.value.unshift(newSession);
                currentSessionId.value = sessionId;
            }
        }).catch(error=>{
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
                    delete messagesStore[sessionId];
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

/* const handleLocalMessageTime = () => {
    return timeString=new Date().format('yy-MM-dd HH:mm:ss')
} */

const handleLocalTime = computed(() => {
    let now = new Date();
    let year = now.getFullYear().toString().substring(2, 4);
    let month = (now.getMonth() + 1).toString().padStart(2, '0');
    let day = now.getDate().toString().padStart(2, '0');
    let hours = now.getHours().toString().padStart(2, '0');
    let minutes = now.getMinutes().toString().padStart(2, '0');
    let seconds = now.getSeconds().toString().padStart(2, '0');
    let formattedTime = `${year}年${month}月${day} ${hours}:${minutes}:${seconds}`;
    return formattedTime;
})




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


    const timeString = handleLocalTime.value;
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


    // 更新标题 (如果是第一条)
    const currentChat = chatSessionHistory.value.find(c => c.sessionId === currentSessionId.value);
    if (currentChat && currentChat.sessionTitle === '新对话') {
        currentChat.sessionTitle = text.substring(0, 10) + (text.length > 10 ? '...' : '');
    }

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
                storeMessageApi(aiMessage,currentSessionId.value);
                break;
            }

            buffer += decoder.decode(value, { stream: true });

            // --- 修正 SSE 数据包解析：使用单换行符分割，以处理服务器的不规范发送 ---
            const lines = buffer.split('\n\n');
            buffer = lines.pop() || ''; // 保留最后一个不完整的行

            for (const line of lines) {
                console.log('11111')

                // 1. 严格处理每一行，清理首尾空白
                const trimmedLine = line.trim();

                //2. 忽略空行或非数据行
                if (!trimmedLine || !trimmedLine.startsWith('data:')) {
                    continue;
                }
                // if (trimmedLine === 'data:' || trimmedLine === 'data: ') {
                //     continue;
                // }

                // 3. 提取 data 部分：移除 "data:" 并清理首尾空格
                let dataString = trimmedLine.substring(5).trim();

                // if (!dataString) {
                //     continue; // 忽略内容为空的数据行 (例如 data: )
                // }
                if (dataString.includes('data:')) {
                    console.log('remove........................')
                    console.log(dataString)
                    // dataString = dataString.replaceAll('\ndata:', '\n');
                    dataString = dataString.replaceAll('data:', '');
                    // dataString = dataString.replaceAll('data:data:', '');
                    console.log('remove' + dataString)
                    /*  const index = dataString.indexOf('data:');
                     dataString = dataString.substring(0, index) + dataString.substring(index); */
                }
                // 直接追加提取后的内容
                aiMessage.textContent += dataString;
                console.log('add')
            }
        }
    } catch (error) {
        ElMessage.error(`[连接或流式错误: ${error.message}]`);
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
                    @send-message="handleSendMessage">
                    <!-- 把右上角的胶囊塞进去 -->
                    <template #header-right>
                        <user-capsule :user="currentUser" :is-dark="isDark" @toggle-theme="toggleTheme"
                            @logout="handleLogout" @check-in="handleCheckIn"
                            @changePass="handleChangUserPassworld"></user-capsule>
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