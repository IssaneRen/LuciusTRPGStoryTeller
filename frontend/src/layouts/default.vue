<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { NLayout, NLayoutSider, NLayoutContent, NMenu, NButton } from 'naive-ui'
import type { MenuOption } from 'naive-ui'
import { h } from 'vue'

const router = useRouter()
const user = useUserStore()

const menuOptions: MenuOption[] = [
  { label: '仪表盘', key: '/dashboard' },
  { label: '图管理', key: '/graph' },
]

function handleMenuClick(key: string) {
  router.push(key)
}

function handleLogout() {
  user.logout()
  router.push('/login')
}
</script>

<template>
  <NLayout has-sider class="h-screen">
    <NLayoutSider bordered :width="200" class="p-4">
      <h1 class="text-lg font-bold mb-4">Lucius TRPG</h1>
      <NMenu :options="menuOptions" @update:value="handleMenuClick" />
      <div class="absolute bottom-4 left-4">
        <NButton size="small" @click="handleLogout">退出登录</NButton>
      </div>
    </NLayoutSider>
    <NLayoutContent>
      <router-view />
    </NLayoutContent>
  </NLayout>
</template>
