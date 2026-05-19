<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { NMessageProvider, NButton, NAvatar } from 'naive-ui'
import { useUserStore } from '@/stores/user'
import LoginModal from '@/components/LoginModal.vue'

const userStore = useUserStore()
const showLoginModal = ref(false)

onMounted(async () => {
  if (userStore.token) {
    await userStore.fetchMe()
  }
})
</script>

<template>
  <NMessageProvider>
    <div class="min-h-screen flex flex-col bg-gray-900">
      <!-- Navigation Bar -->
      <header class="sticky top-0 z-50 bg-gray-800/80 backdrop-blur-sm border-b border-gray-700 px-6 py-3 flex items-center justify-between">
        <a href="/" class="text-xl font-bold text-white hover:text-purple-400 transition-colors">
          Lucius TRPG
        </a>
        <div class="flex items-center gap-3">
          <template v-if="userStore.username">
            <NAvatar round size="small" class="bg-purple-600">
              {{ userStore.username[0]?.toUpperCase() }}
            </NAvatar>
            <span class="text-gray-200 text-sm">{{ userStore.username }}</span>
            <NButton text size="small" class="text-gray-400 hover:text-white" @click="userStore.logout()">
              退出
            </NButton>
          </template>
          <template v-else>
            <NButton type="primary" size="small" round @click="showLoginModal = true">
              登录
            </NButton>
          </template>
        </div>
      </header>

      <!-- Main Content -->
      <main class="flex-1">
        <router-view />
      </main>

      <!-- Login Modal -->
      <LoginModal v-model:show="showLoginModal" />
    </div>
  </NMessageProvider>
</template>
