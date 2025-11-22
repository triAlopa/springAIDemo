import { ref } from 'vue';

// 模拟用户数据
export const currentUser = ref({
  id: 1001,
  username: "春乏夏困秋打盹zz",
  avatar: "https://images.unsplash.com/photo-1517841905240-472988babdf9?q=80&w=2459&auto=format&fit=crop", // 真实 Unsplash 图片
  level: 6,
  coins: 726.5,
  bCoins: 0,
  stats: { following: 104, followers: 7, posts: 68 },
  email: "boss@example.com",
  isCheckedIn: false // 是否已签到
});

// 模拟历史会话列表
export const historyList = ref([
  { id: 1, title: "如何优化 Vue3 性能？", date: "今天" },
  { id: 2, title: "生成一张赛博朋克风格的图片", date: "昨天" },
  { id: 3, title: "解释量子纠缠", date: "上周" },
]);

// 模拟聊天记录
export const chatMessages = ref([
  { 
    id: 1, 
    role: 'user', 
    content: '你好，BOSS。请给我一张风景图。', 
    type: 'text' 
  },
  { 
    id: 2, 
    role: 'ai', 
    content: '你好！这是为你找到的风景图。', 
    type: 'image',
    url: 'https://images.unsplash.com/photo-1472214103451-9374bd1c798e?q=80&w=2070&auto=format&fit=crop'
  },
  { 
    id: 3, 
    role: 'ai', 
    content: '还需要我帮你做什么吗？', 
    type: 'text' 
  }
]);