<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import LoginModal from '@/components/LoginModal.vue'

const userStore = useUserStore()
const showLoginModal = ref(false)

const handleLogout = () => {
  userStore.logout()
}

onMounted(async () => {
  if (userStore.token) {
    try {
      await userStore.fetchMe()
    } catch {
      // Silent fail if token is invalid
    }
  }
})
</script>

<template>
  <NMessageProvider>
    <div class="min-h-screen flex flex-col">
      <!-- Navigation Bar -->
      <header class="bg-gray-800 text-white px-6 py-4 flex items-center justify-between">
        <h1 class="text-xl font-bold">Lucius TRPG</h1>
        <div class="flex items-center gap-4">
          <template v-if="userStore.username">
            <span class="text-gray-300">{{ userStore.username }}</span>
            <span class="text-gray-500">|</span>
            <NButton text @click="handleLogout">退出</NButton>
          </template>
          <template v-else>
            <NButton type="primary" @click="showLoginModal = true">登录</NButton>
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
