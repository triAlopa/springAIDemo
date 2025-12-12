<script setup>
defineProps({
    history: {
        type: Array,
        required: true
    },
    currentChatId: {
        type: [Number, String],
        required: true
    }
});

defineEmits(['select-chat', 'create-chat', 'delete-chat']);
</script>

<template>
    <div class="w-64 lg:w-72 h-full liquid-sidebar flex flex-col border-r border-white/20 dark:border-gray-700/50 transition-all duration-300">
        <div class="h-16 flex items-center px-6">
            <div class="w-8 h-8 rounded bg-gradient-to-tr from-blue-400 to-purple-500 flex items-center justify-center text-white font-bold mr-3">B</div>
            <span class="font-bold text-xl tracking-tight">BOSS</span>
        </div>

        <div class="px-4 mb-4">
            <button @click="$emit('create-chat')" class="w-full py-3 px-4 bg-white/50 dark:bg-gray-700/50 hover:bg-white/80 dark:hover:bg-gray-600/80 backdrop-blur border border-gray-200 dark:border-gray-600 rounded-xl flex items-center gap-3 transition-all group shadow-sm hover:shadow-md">
                <el-icon class="text-lg text-[#00b1eb]"><Plus /></el-icon>
                <span class="text-sm font-medium">开启新对话</span>
            </button>
        </div>

        <div class="flex-1 overflow-y-auto no-scrollbar px-3 pb-4 space-y-1">
            <div class="text-xs font-medium text-gray-400 px-3 mb-2 mt-2 uppercase tracking-wider">Recent</div>
            
            <transition-group name="list">
                <div 
                    v-for="chat in history" 
                    :key="chat.id"
                    @click="$emit('select-chat', chat.id)"
                    class="group relative flex items-center p-3 rounded-lg cursor-pointer transition-all duration-200 hover:bg-white/40 dark:hover:bg-gray-700/40 hover:translate-x-1"
                    :class="{'bg-white/60 dark:bg-gray-700/60 shadow-sm border border-white/40 dark:border-gray-600': currentChatId === chat.id}"
                >
                    <el-icon class="text-lg mr-3 text-gray-500" :class="{'text-[#00b1eb]': currentChatId === chat.id}"><ChatDotRound /></el-icon>
                    <div class="flex-1 overflow-hidden">
                        <div class="truncate text-sm font-medium">{{ chat.sessionTitle }}</div>
                        <div class="truncate text-xs text-gray-400">{{ chat.lastTime }}</div>
                    </div>
                    
                    <button @click.stop="$emit('delete-chat', chat.id)" class="absolute right-2 opacity-0 group-hover:opacity-100 p-1.5 hover:bg-red-100 hover:text-red-500 rounded transition-all">
                        <el-icon><Delete /></el-icon>
                    </button>
                </div>
            </transition-group>
        </div>
        
        <div class="p-4 text-xs text-center text-gray-400 opacity-50">
            BOSS AI v1.0.2
        </div>
    </div>
</template>