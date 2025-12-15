<script setup>
import { ref, watch, nextTick, onMounted, onUnmounted } from 'vue';
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
  },
  currentModel: {
    type: Object,
    required: true,
    /**
     * "data": {
    "name": "小蓝",
    "image": "https://example.com/images/blue_avatar.jpg",
    "temperature": 0.7,
    "company": {
      "name": "深蓝科技",
      "type": 1,
      "lowSalary": 15,
      "highSalary": 35,
      "address": "北京市东城区正义路",
      "jobTag": [
        "Java",
        "大数据",
        "云计算"
      ],
      "jobDesc": "负责后端系统架构设计与开发，熟悉分布式系统",
      "employerBenefit": [
        "五险一金",
        "年度旅游",
        "弹性工作",
        "股票期权"
      ]
    }
  }
     */
    default: () => ({
      name: "小蓝",
      image: "https://example.com/images/blue_avatar.jpg",
      temperature: 0.7,
      company: {
        name: "深蓝",
        type: 1,
        lowSalary: 15,
        highSalar: 35,
        address: "北京市东城区正义路",
        jobTag: [
          "Java",
          "大数据",
          "云计算"
        ],
        jobDesc: "负责后端系统架构设计与开发，熟悉分布式系统",
        employerBenefit: [
          "五险一金",
          "年度旅游",
          "弹性工作",
          "股票期权"
        ]
      }
    })
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

  content = content.replace(/(\d+)\./g, '$1\\.');
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

        <div class="inline-block">
          <el-popover placement="bottom-end" :width="320" trigger="hover"
            popper-class="!p-0 !rounded-xl overflow-hidden shadow-2xl transition-all duration-300">
            <template #reference>
              <button
                class="flex items-center space-x-2 py-2 px-4 bg-white rounded-full border border-gray-200 shadow-md text-sm font-semibold text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-opacity-50">
                <el-icon class="text-blue-500">
                  <InfoFilled />
                </el-icon>
                <span>公司详情</span>
              </button>
            </template>

            <div class="bg-white p-5">
              <h3 class="text-xl font-extrabold mb-4 text-gray-800 border-b pb-2">
                核心信息
              </h3>

              <div class="space-y-4">
                <div class="flex items-center">
                  <div class="w-7 h-7 flex items-center justify-center rounded-lg mr-3 bg-red-100 flex-shrink-0">
                    <el-icon class="text-red-500 text-lg">
                      <User />
                    </el-icon>
                  </div>
                  <div class="flex-grow">
                    <span class="text-xs text-gray-500 block">HR 姓名:</span>
                    <span class="text-sm font-bold text-gray-800">{{ currentModel.name }}</span>
                  </div>
                </div>

                <div class="flex items-center">
                  <div class="w-7 h-7 flex items-center justify-center rounded-lg mr-3 bg-green-100 flex-shrink-0">
                    <el-icon class="text-green-500 text-lg">
                      <OfficeBuilding />
                    </el-icon>
                  </div>
                  <div class="flex-grow">
                    <span class="text-xs text-gray-500 block">公司名称:</span>
                    <span class="text-sm font-bold text-gray-800">{{ currentModel.company.name }}</span>
                  </div>
                </div>

                <div class="flex items-center">
                  <div class="w-7 h-7 flex items-center justify-center rounded-lg mr-3 bg-yellow-100 flex-shrink-0">
                    <el-icon class="text-yellow-500 text-lg">
                      <Histogram />
                    </el-icon>
                  </div>
                  <div class="flex-grow">
                    <span class="text-xs text-gray-500 block">公司档位:</span>
                    <span class="text-sm font-bold text-gray-800">
                      <div v-if="currentModel.company.type == 1">初创公司</div>
                      <div v-else-if="currentModel.company.type == 2">上市公司</div>
                      <div v-else-if="currentModel.company.type == 3">500强公司</div>
                      <div v-else>路边一条</div>
                    </span>
                  </div>
                </div>

                <div class="flex items-center">
                  <div class="w-7 h-7 flex items-center justify-center rounded-lg mr-3 bg-indigo-100 flex-shrink-0">
                    <el-icon class="text-indigo-500 text-lg">
                      <Opportunity />
                    </el-icon>
                  </div>
                  <div class="flex-grow">
                    <span class="text-xs text-gray-500 block">地址:</span>
                    <span class="text-sm font-bold text-gray-800">{{ currentModel.company.address }}</span>
                  </div>
                </div>

                <div class="flex items-center">
                  <div class="w-7 h-7 flex items-center justify-center rounded-lg mr-3 bg-pink-100 flex-shrink-0">
                    <el-icon class="text-pink-500 text-lg">
                      <Briefcase />
                    </el-icon>
                  </div>
                  <div class="flex-grow">
                    <span class="text-xs text-gray-500 block">职位属性:</span>
                    <span class="text-sm font-bold text-gray-800">
                      <el-tag v-for="(job, index) in currentModel.company.jobTag" :key="index">
                        {{ job }}
                      </el-tag>
                    </span>
                  </div>
                </div>

                <div class="flex items-center pt-2 border-t border-dashed border-gray-200">
                  <div class="w-7 h-7 flex items-center justify-center rounded-lg mr-3 bg-teal-100 flex-shrink-0">
                    <el-icon class="text-teal-500 text-lg">
                      <Money />
                    </el-icon>
                  </div>
                  <div class="flex-grow">
                    <span class="text-xs text-gray-500 block">薪资:</span>
                    <span class="text-base font-extrabold text-red-600">
                      {{ currentModel.company.lowSalary }}K至{{ currentModel.company.highSalary }}K</span>
                  </div>
                </div>

                <div class="flex items-center">
                  <div class="w-7 h-7 flex items-center justify-center rounded-lg mr-3 bg-purple-100 flex-shrink-0">
                    <el-icon class="text-purple-500 text-lg">
                      <StarFilled />
                    </el-icon>
                  </div>
                  <div class="flex-grow">
                    <span class="text-xs text-gray-500 block">企业名称:</span>
                    <span class="text-sm font-bold text-gray-800">{{ currentModel.company.name }}</span>
                  </div>
                </div>

                <div class="flex items-center">
                  <div class="w-7 h-7 flex items-center justify-center rounded-lg mr-3 bg-orange-100 flex-shrink-0">
                    <el-icon class="text-orange-500 text-lg">
                      <UserFilled />
                    </el-icon>
                  </div>
                  <div class="flex-grow">
                    <span class="text-xs text-gray-500 block">HR 性格:</span>
                    <span class="text-sm font-bold text-gray-800">
                      <div v-if="currentModel.temperature>=0.0&& currentModel.temperature<=1.0">强硬型</div>
                      <div v-else-if="currentModel.temperature>=1.0&& currentModel.temperature<=1.5">幽默型</div>
                      <div v-else-if="currentModel.temperature>=1.5&& currentModel.temperature<=2.0">卑微型</div>
                      <div v-else>null型</div>
                    </span>
                  </div>
                </div>

                <div class="flex items-center">
                  <div class="w-7 h-7 flex items-center justify-center rounded-lg mr-3 bg-blue-100 flex-shrink-0">
                    <el-icon class="text-blue-500 text-lg">
                      <Sunny />
                    </el-icon>
                  </div>
                  <div class="flex-grow">
                    <span class="text-xs text-gray-500 block">工作详情:</span>
                    <span class="text-sm font-bold text-blue-600">{{ currentModel.company.jobDesc }}</span>
                  </div>
                </div>
                <div class="flex items-center">
                  <div class="w-7 h-7 flex items-center justify-center rounded-lg mr-3 bg-blue-100 flex-shrink-0">
                    <el-icon class="text-blue-500 text-lg">
                      <Sunny />
                    </el-icon>
                  </div>
                  <div class="flex-grow">
                    <span class="text-xs text-gray-500 block">员工福利:</span>
                    <span class="text-sm font-bold text-blue-600">
                       <el-tag v-for="(job, index) in currentModel.company.employerBenefit" :key="index">
                        {{ job }}
                      </el-tag>
                    </span>
                  </div>
                </div>

              </div>
            </div>
          </el-popover>
        </div>

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
            <div v-if="msg.contentType === 'text'" v-html="parseMarkdown(msg.textContent)" class="markdown-body">
            </div>
            <el-image v-else-if="msg.contentType === 'image'" :src="msg.textContent"
              :preview-src-list="[msg.textContent]" class="rounded-lg max-h-60 w-auto" fit="cover">
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

          <button class="p-2 text-gray-500 hover:text-primary rounded-lg transition-colors flex-shrink-0" title="上传图片"
            @click="triggerFileUpload">

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