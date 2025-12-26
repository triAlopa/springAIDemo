<script setup>
import {ref, watch, nextTick, onMounted, onUnmounted} from 'vue';
import {ElMessage} from 'element-plus';
import {marked} from 'marked';
import {Service, Picture, Promotion} from '@element-plus/icons-vue';

const props = defineProps({
  messages: {
    type: Array,
    required: true
  },
  currentChatTitle: {
    type: String,
    required: true
  },
  isDark: {
    type: Boolean,
    required: true
  },
  currentModel: {
    type: Object,
    required: true,
  },
  currentBaseInfo: {
    type: Object,
    required: true,
  }
});

marked.setOptions({
  html: true,
  breaks: true,
  sanitize: false,
  gfm: true          // 启用 GitHub 风格的 Markdown
});

const emit = defineEmits(['send-message', 'handleOffer']);

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
}, {deep: true});


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

const action = (event) => {


  const target = event.target.closest('[data-action]');

  if (!target) return;

  const actionType = target.dataset.action;

  if (actionType === 'accept') {
    // console.log('accept')
    emit('handleOffer', 'accept');
  } else if (actionType === 'reject') {
    //  console.log('reject')
    emit('handleOffer', 'reject');
  }
}


const parseMarkdown = (content) => {

  if (!content) return ''

  content = content.replace(/(\d+)\./g, '$1\\.');
  // 使用导入的 marked 库进行解析
  if (typeof marked.parse === 'function') {
    // console.log(marked.parse(content))
    return marked.parse(content);
  }
  return content;
};

/* const triggerFileUpload = () => {
  ElMessage.info('图片上传功能演示：点击发送会自动模拟图片回复');
};
 */

const options = [
  {value: '介绍一下你们公司'},
  {value: '你们是世界500强吗？'},
  {value: '你们连500强都不是，是谁给你们投递简历的自信？'},
  {value: '你们是500强没错，但是500强招我也要排号，你们公司有什么独一无二的'},
  {value: '你们作为一个初创公司,连3年的招人经验都没有？我很难相信你们有培养新人的经验'},
  {value: '原则上你们公司是过不了我的简历初筛的,看在你这么摇尾巴的份上,勉强给你一个狗叫的机会'},
  {value: '中厂？你们这种不上不下的公司最尴尬。要钱，给不起；要名，没有。除了靠一张嘴‘求’人，还能拿出点什么像样的东西？'},
  {value: '有5位大学生，只有4杯水，你要怎么分？'},
  {value: '你知道spring吗,有读过spring的源码吗,有自主研发过summer框架吗？'},
  {value: '你们有研发过比快排更快的算法吗？'},
  {value: '你们的QPS能到多少？我之前的个人项目都能抗住上万并发。'},
  {value: '你们的薪资范围，是觉得我的能力只值这么多，还是你们公司只给得起这么多？'},
  {value: '我要996工作制,一年工作9个月,一个月工作9天,每天6小时,你们能接受吗？'},
  {value: '我看不到你们的诚意,你不录有的是公司录'},
  {value: '这薪资，是税前还是税后？如果是税前的话，建议你们再想想'},
  {value: '期权？你们公司能活到行权那天吗？'},
  {value: '你们那架构，是祖传的if-else架构吗？'},
  {value: '单机QPS过不了十万，也好意思说自己是高并发系统？'},
  {value: '你说你熟悉MySQL，来给我手写一个支持ACID的存储引擎'},
  {value: '你们手撕过红黑树吗？不会连红黑树都手撕不出来吧？'},
  {value: '能现场写个B+树让我看看吗？还是说你们只会调API？'},
  {value: '需求变更多少次了？你们的产品经理是拍脑袋做决策的吗'},
  {value: '连个技术债管理都没有，代码屎山堆得挺开心啊'},
  {value: '你们的技术分享，是不是就是念官方文档？'},
  {value: '回去等通知吧'},
  {value: '我给你们个建议：关掉招聘页面，好好提升自己'},
  {value: '今天就到这吧，我后面还有三家大厂的终面'},
  ]

const handleSelectQuickyMessage=(value)=>{
  console.log(value)
  inputContent.value=value;
}
</script>

<template>

  <div class="flex-1 h-full bg-white/30 dark:bg-gray-800/30 flex flex-col relative backdrop-blur-sm">

    <header
        class="h-16 px-6 border-b border-gray-100 dark:border-gray-700/50 flex items-center justify-between bg-white/40 dark:bg-gray-900/40 backdrop-blur-md z-10">
      <div class="flex items-center gap-2">
        <span class="font-semibold text-lg">{{ currentChatTitle }}</span>

        <el-popover placement="bottom-end" :width="340" trigger="hover"
                    popper-class="!p-0 !rounded-xl overflow-hidden shadow-xl dark:shadow-2xl dark:shadow-gray-900/50 border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 transition-all duration-200 ease-out"
                    transition="el-zoom-in-top">
          <template #reference>
            <div v-show="currentModel.name !== ''" class="inline-flex items-center justify-center px-4 py-2 
              rounded-lg bg-white dark:bg-gray-800 border border-gray-300 
              dark:border-gray-600 shadow-sm hover:shadow-md hover:border-blue-400 
              dark:hover:border-blue-500 hover:bg-gray-50 dark:hover:bg-gray-700 
              transition-all duration-200 cursor-pointer">
              <el-icon class="text-blue-500 dark:text-blue-400 mr-2">
                <InfoFilled/>
              </el-icon>
              <span class="text-sm font-medium text-gray-700 dark:text-gray-200">公司详情</span>
            </div>
          </template>

          <div class="bg-white dark:bg-gray-800 p-5">
            <div class="mb-6">
              <h3 class="text-xl font-bold text-gray-800 dark:text-white mb-1">核心信息</h3>
              <div class="w-12 h-1 bg-gradient-to-r from-blue-400 to-blue-600 rounded-full"></div>
            </div>

            <div class="space-y-4">
              <div class="flex items-center">
                <div
                    class="w-8 h-8 flex items-center justify-center rounded-lg mr-3 bg-red-100 dark:bg-red-900/30 flex-shrink-0">
                  <el-icon class="text-red-500 dark:text-red-400 text-lg">
                    <User/>
                  </el-icon>
                </div>
                <div class="flex-grow">
                  <span class="text-xs text-gray-500 dark:text-gray-400 block">HR 姓名</span>
                  <span class="text-sm font-semibold text-gray-800 dark:text-gray-100">{{ currentModel.name }}</span>
                </div>
              </div>

              <div class="flex items-center">
                <div
                    class="w-8 h-8 flex items-center justify-center rounded-lg mr-3 bg-green-100 dark:bg-green-900/30 flex-shrink-0">
                  <el-icon class="text-green-500 dark:text-green-400 text-lg">
                    <OfficeBuilding/>
                  </el-icon>
                </div>
                <div class="flex-grow">
                  <span class="text-xs text-gray-500 dark:text-gray-400 block">公司名称</span>
                  <span class="text-sm font-semibold text-gray-800 dark:text-gray-100">{{
                      currentModel.company.name
                    }}</span>
                </div>
              </div>

              <div class="flex items-center">
                <div
                    class="w-8 h-8 flex items-center justify-center rounded-lg mr-3 bg-yellow-100 dark:bg-yellow-900/30 flex-shrink-0">
                  <el-icon class="text-yellow-500 dark:text-yellow-400 text-lg">
                    <Histogram/>
                  </el-icon>
                </div>
                <div class="flex-grow">
                  <span class="text-xs text-gray-500 dark:text-gray-400 block">公司档位</span>
                  <span class="text-sm font-semibold text-gray-800 dark:text-gray-100">
                    <span v-if="currentModel.company.type == 1" class="text-blue-600 dark:text-blue-400">初创公司</span>
                    <span v-else-if="currentModel.company.type == 2"
                          class="text-green-600 dark:text-green-400">上市公司</span>
                    <span v-else-if="currentModel.company.type == 3"
                          class="text-purple-600 dark:text-purple-400">500强公司</span>
                    <span v-else class="text-gray-500">其他类型</span>
                  </span>
                </div>
              </div>

              <div class="flex items-center">
                <div
                    class="w-8 h-8 flex items-center justify-center rounded-lg mr-3 bg-indigo-100 dark:bg-indigo-900/30 flex-shrink-0">
                  <el-icon class="text-indigo-500 dark:text-indigo-400 text-lg">
                    <Opportunity/>
                  </el-icon>
                </div>
                <div class="flex-grow">
                  <span class="text-xs text-gray-500 dark:text-gray-400 block">地址</span>
                  <span class="text-sm font-semibold text-gray-800 dark:text-gray-100">{{
                      currentModel.company.address
                    }}</span>
                </div>
              </div>

              <div class="flex items-start">
                <div
                    class="w-8 h-8 flex items-center justify-center rounded-lg mr-3 bg-pink-100 dark:bg-pink-900/30 flex-shrink-0 mt-1">
                  <el-icon class="text-pink-500 dark:text-pink-400 text-lg">
                    <Briefcase/>
                  </el-icon>
                </div>
                <div class="flex-grow">
                  <span class="text-xs text-gray-500 dark:text-gray-400 block mb-1">职位属性</span>
                  <div class="flex flex-wrap gap-1">
                    <el-tag v-for="(job, index) in currentModel.company.jobTag" :key="index" size="small"
                            class="!text-xs !px-2 !py-1 !rounded-md border-0" :class="[
                        'bg-blue-100 dark:bg-blue-900/30',
                        'text-blue-600 dark:text-blue-300',
                        'hover:bg-blue-200 dark:hover:bg-blue-800/50'
                      ]">
                      {{ job }}
                    </el-tag>
                  </div>
                </div>
              </div>

              <div class="flex items-center pt-3 border-t border-dashed border-gray-200 dark:border-gray-700">
                <div
                    class="w-8 h-8 flex items-center justify-center rounded-lg mr-3 bg-teal-100 dark:bg-teal-900/30 flex-shrink-0">
                  <el-icon class="text-teal-500 dark:text-teal-400 text-lg">
                    <Money/>
                  </el-icon>
                </div>
                <div class="flex-grow">
                  <span class="text-xs text-gray-500 dark:text-gray-400 block">薪资范围</span>
                  <span class="text-base font-bold text-red-500 dark:text-red-400">
                    {{ currentModel.company.lowSalary }}K - {{ currentModel.company.highSalary }}K
                  </span>
                </div>
              </div>

              <div class="flex items-center">
                <div
                    class="w-8 h-8 flex items-center justify-center rounded-lg mr-3 bg-purple-100 dark:bg-purple-900/30 flex-shrink-0">
                  <el-icon class="text-purple-500 dark:text-purple-400 text-lg">
                    <StarFilled/>
                  </el-icon>
                </div>
                <div class="flex-grow">
                  <span class="text-xs text-gray-500 dark:text-gray-400 block">企业名称</span>
                  <span class="text-sm font-semibold text-gray-800 dark:text-gray-100">{{
                      currentModel.company.name
                    }}</span>
                </div>
              </div>

              <div class="flex items-center">
                <div
                    class="w-8 h-8 flex items-center justify-center rounded-lg mr-3 bg-orange-100 dark:bg-orange-900/30 flex-shrink-0">
                  <el-icon class="text-orange-500 dark:text-orange-400 text-lg">
                    <UserFilled/>
                  </el-icon>
                </div>
                <div class="flex-grow">
                  <span class="text-xs text-gray-500 dark:text-gray-400 block">HR 性格</span>
                  <span class="text-sm font-semibold text-gray-800 dark:text-gray-100">
                    <span v-if="currentModel.temperature >= 0.0 && currentModel.temperature <= 0.7"
                          class="text-red-500">强硬型</span>
                    <span v-else-if="currentModel.temperature > 0.7 && currentModel.temperature <= 1.5"
                          class="text-yellow-500">幽默型</span>
                    <span v-else-if="currentModel.temperature > 1.5 && currentModel.temperature <= 2.0"
                          class="text-blue-500">卑微型</span>
                    <span v-else class="text-gray-500">null型</span>
                  </span>
                </div>
              </div>

              <!-- 工作详情 -->
              <div class="flex items-start">
                <div
                    class="w-8 h-8 flex items-center justify-center rounded-lg mr-3 bg-blue-100 dark:bg-blue-900/30 flex-shrink-0 mt-1">
                  <el-icon class="text-blue-500 dark:text-blue-400 text-lg">
                    <Sunny/>
                  </el-icon>
                </div>
                <div class="flex-grow">
                  <span class="text-xs text-gray-500 dark:text-gray-400 block mb-1">工作详情</span>
                  <span class="text-sm text-blue-600 dark:text-blue-300 leading-relaxed">{{
                      currentModel.company.jobDesc
                    }}</span>
                </div>
              </div>

              <!-- 员工福利 -->
              <div class="flex items-start">
                <div
                    class="w-8 h-8 flex items-center justify-center rounded-lg mr-3 bg-cyan-100 dark:bg-cyan-900/30 flex-shrink-0 mt-1">
                  <el-icon class="text-cyan-500 dark:text-cyan-400 text-lg">
                    <Sunny/>
                  </el-icon>
                </div>
                <div class="flex-grow">
                  <span class="text-xs text-gray-500 dark:text-gray-400 block mb-1">员工福利</span>
                  <div class="flex flex-wrap gap-1">
                    <el-tag v-for="(benefit, index) in currentModel.company.employerBenefit" :key="index" size="small"
                            class="!text-xs !px-2 !py-1 !rounded-md border-0" :class="[
                        'bg-green-100 dark:bg-green-900/30',
                        'text-green-600 dark:text-green-300',
                        'hover:bg-green-200 dark:hover:bg-green-800/50'
                      ]">
                      {{ benefit }}
                    </el-tag>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-popover>

      </div>
      <slot name="header-right"></slot>
    </header>

    <div class="flex-1 overflow-y-auto p-6 space-y-6 scroll-smooth" ref="messageContainer">
      <transition-group name="fade-slide">
        <div v-for="(msg, index) in messages" :key="index" class="flex w-full"
             :class="msg.type === 'USER' ? 'justify-end' : 'justify-start'">
          <div v-if="msg.type === 'ASSISTANT'"
               class="w-8 h-8 rounded-full bg-gray-200 overflow-hidden flex-shrink-0 mr-3 shadow-md border-2 border-white dark:border-gray-600">
            <img :src="currentBaseInfo.AIImage"
                 alt="ASSISTANT"
                 class="w-full h-full object-cover">
          </div>


          <div class="max-w-[70%] bubble-shadow p-4 rounded-2xl relative text-sm leading-relaxed break-words"
               :class="msg.type === 'USER'
              ? 'bg-[#00b1eb] text-white rounded-tr-none'
              : 'bg-white dark:bg-gray-700 text-gray-700 dark:text-gray-200 rounded-tl-none border border-gray-100 dark:border-gray-600'">

            <!-- <div v-if="msg.type === 'text'" class="whitespace-pre-wrap">{{ msg.content }}</div> -->
            <!-- <div v-html="parseMarkdown(msg.textContent)" class="markdown-body"></div> -->
            <div v-if="msg.contentType === 'text'" v-html="parseMarkdown(msg.textContent)" class="markdown-body"
                 @click="action">
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
            <img :src="currentBaseInfo.userImage"
                 alt="User">
          </div>
        </div>
      </transition-group>

    </div>

    <el-select v-if="currentBaseInfo.sessionStatus==1"
               placeholder="快捷回复"
               size="default"
               style="margin-left: 30px; width:300px "
               @change="handleSelectQuickyMessage"
    >
      <el-option
          v-for="item in options"
          :key="item.value"
          :label="item.value"
          :value="item.value"
      />
    </el-select>
    <div class="p-6 pt-2">
      <div class="relative rounded-3xl p-3 transition-all duration-300" :class="[
        // 液态玻璃效果
        'bg-white/70 dark:bg-gray-800/80 backdrop-blur-xl',
        // 默认阴影 (柔和的立体感)
        'shadow-xl shadow-gray-200/50 dark:shadow-black/50',
        // 焦点视觉效果：点击时阴影加重
        'focus-within:shadow-2xl focus-within:shadow-blue-300/60 dark:focus-within:shadow-blue-700/60'
      ]">

        <div class="flex items-center gap-2" v-if="currentBaseInfo.sessionStatus==1">

          <!-- <button class="p-2 text-gray-500 hover:text-primary rounded-lg transition-colors flex-shrink-0" title="上传图片"
            @click="triggerFileUpload">

            <el-icon class="text-xl">
              <Picture />
            </el-icon>
          </button> -->

          <textarea v-model="inputContent" @keydown.enter.prevent="send" placeholder="输入消息拷打你的hr...."
                    class="flex-1 bg-transparent border-none outline-none resize-none max-h-32 text-base dark:text-white scrollbar-hide"
                    rows="1" @input="autoResize" ref="textareaRef"></textarea>

          <button @click="send" :disabled="(!inputContent.trim())|| currentBaseInfo.sessionStatus==0"
                  class="w-10 h-10 rounded-xl bg-primary text-white hover:bg-blue-600 active:scale-95 transition-all disabled:opacity-30 disabled:cursor-not-allowed flex items-center justify-center flex-shrink-0">

            <el-icon class="text-xl">
              <Promotion/>
            </el-icon>
          </button>
        </div>
      </div>

      <div class="text-center mt-2 text-xs text-gray-400">只供娱乐使用</div>
    </div>
  </div>
</template>