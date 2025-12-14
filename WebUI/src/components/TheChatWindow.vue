<script setup>
import { ref, watch, nextTick,onMounted,onUnmounted } from 'vue';
import { ElMessage } from 'element-plus';
import { marked } from 'marked';
import { Service, Picture, Promotion } from '@element-plus/icons-vue'; // Service 用于代替 ph-robot

const props = defineProps({
    messages: {
        type: Array,
        required: true
    },
    currentChatTitle: {
        type: String,
        required: true
    }
});

const emit = defineEmits(['send-message']);

const inputContent = ref('');
const textareaRef = ref(null);
const messageContainer = ref(null);

const autoResize = () => {
    const el = textareaRef.value;
    if (el) {
        el.style.height = 'auto';
        el.style.height = el.scrollHeight + 'px';
    }
};

const scrollToBottom = () => {
    nextTick(() => {
        if (messageContainer.value) {
            messageContainer.value.scrollTop = messageContainer.value.scrollHeight;
        }
    });
};

// 监听消息变化滚动到底部
watch(() => props.messages, () => {
    scrollToBottom();
}, { deep: true });


const send = () => {
    if (!inputContent.value.trim()) return;
    emit('send-message', inputContent.value);
    inputContent.value = '';
    //autoResize(); // 重置高度
    nextTick(() => {
        const el = textareaRef.value;
        if (el) {
            el.style.height = 'auto'; // 强制重置为最小高度
        }
    });
};

const parseMarkdown = (content) => {
     
    content=content.replace(/(\d+)\./g, '$1\\.');
    // 使用导入的 marked 库进行解析
    if (typeof marked.parse === 'function') {
        return marked.parse(content);
    }
    return content;
};

const triggerFileUpload = () => {
    ElMessage.info('图片上传功能演示：点击发送会自动模拟图片回复');
};


</script>

<template>

    <div class="flex-1 h-full bg-white/30 dark:bg-gray-800/30 flex flex-col relative backdrop-blur-sm">

        <header
            class="h-16 px-6 border-b border-gray-100 dark:border-gray-700/50 flex items-center justify-between bg-white/40 dark:bg-gray-900/40 backdrop-blur-md z-10">
            <div class="flex items-center gap-2">
                <span class="font-semibold text-lg">{{ currentChatTitle }}</span>
                <span
                    class="px-2 py-0.5 rounded text-[10px] bg-blue-100 text-blue-600 dark:bg-blue-900/30 dark:text-blue-400 font-bold">GPT-4</span>
            </div>
            <slot name="header-right"></slot>
        </header>

        <div class="flex-1 overflow-y-auto p-6 space-y-6 scroll-smooth" ref="messageContainer">
            <transition-group name="fade-slide">
                <div v-for="(msg, index) in messages" :key="index" class="flex w-full"
                    :class="msg.type === 'USER' ? 'justify-end' : 'justify-start'">
                    <div v-if="msg.type === 'ASSISTANT'"
                        class="w-8 h-8 rounded-full bg-gradient-to-br from-blue-500 to-cyan-400 flex-shrink-0 mr-3 flex items-center justify-center text-white shadow-lg">
                        <el-icon class="text-lg">
                            <Service />
                        </el-icon>
                    </div>

                    <div class="max-w-[70%] bubble-shadow p-4 rounded-2xl relative text-sm leading-relaxed break-words"
                        :class="msg.type === 'USER'
                            ? 'bg-[#00b1eb] text-white rounded-tr-none'
                            : 'bg-white dark:bg-gray-700 text-gray-700 dark:text-gray-200 rounded-tl-none border border-gray-100 dark:border-gray-600'">

                        <!-- <div v-if="msg.type === 'text'" class="whitespace-pre-wrap">{{ msg.content }}</div> -->
                        <!-- <div v-html="parseMarkdown(msg.textContent)" class="markdown-body"></div> -->
                        <div v-if="msg.contentType === 'text'" v-html="parseMarkdown(msg.textContent)" class="markdown-body"></div>
                        <el-image v-else-if="msg.contentType === 'image'" :src="msg.textContent" :preview-src-list="[msg.textContent]"
                            class="rounded-lg max-h-60 w-auto" fit="cover">
                        </el-image>

                        <div class="text-[10px] mt-1 opacity-60 text-right"
                            :class="msg.type === 'USER' ? 'text-slate-600' : 'text-gray-400'">
                            {{ msg.createdTime }}
                        </div>
                    </div>

                    <div v-if="msg.type === 'USER'"
                        class="w-8 h-8 rounded-full bg-gray-200 overflow-hidden flex-shrink-0 ml-3 shadow-md border-2 border-white dark:border-gray-600">
                        <img src="https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=100&h=100"
                            alt="User">
                    </div>
                </div>
            </transition-group>
        </div>

        <div class="p-6 pt-2">
            <div class="relative rounded-3xl p-3 transition-all duration-300" :class="[
                // 液态玻璃效果
                'bg-white/70 dark:bg-gray-800/80 backdrop-blur-xl',
                // 默认阴影 (柔和的立体感)
                'shadow-xl shadow-gray-200/50 dark:shadow-black/50',
                // 焦点视觉效果：点击时阴影加重
                'focus-within:shadow-2xl focus-within:shadow-blue-300/60 dark:focus-within:shadow-blue-700/60'
            ]">

                <div class="flex items-center gap-2">

                    <button class="p-2 text-gray-500 hover:text-primary rounded-lg transition-colors flex-shrink-0"
                        title="上传图片" @click="triggerFileUpload">

                        <el-icon class="text-xl">
                            <Picture />
                        </el-icon>
                    </button>

                    <textarea v-model="inputContent" @keydown.enter.prevent="send" placeholder="输入消息给 BOSS AI..."
                        class="flex-1 bg-transparent border-none outline-none resize-none max-h-32 text-base dark:text-white scrollbar-hide"
                        rows="1" @input="autoResize" ref="textareaRef"></textarea>

                    <button @click="send" :disabled="!inputContent.trim()"
                        class="w-10 h-10 rounded-xl bg-primary text-white hover:bg-blue-600 active:scale-95 transition-all disabled:opacity-30 disabled:cursor-not-allowed flex items-center justify-center flex-shrink-0">

                        <el-icon class="text-xl">
                            <Promotion />
                        </el-icon>
                    </button>
                </div>
            </div>

            <div class="text-center mt-2 text-xs text-gray-400">AI generated content may be inaccurate.</div>
        </div>
    </div>
</template>