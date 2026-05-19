<script setup lang="ts">
import { ref } from 'vue'
import { NModal, NCard, NInput, NButton, NSpace, useMessage } from 'naive-ui'
import { useUserStore } from '@/stores/user'

const props = defineProps<{ show: boolean }>()
const emit = defineEmits<{ 'update:show': [value: boolean] }>()

const userStore = useUserStore()
const message = useMessage()
const usernameInput = ref('')
const loading = ref(false)

const handleLogin = async () => {
  const name = usernameInput.value.trim()
  if (!name) return

  if (name.length < 2 || name.length > 32) {
    message.warning('用户名长度应在 2-32 字符之间')
    return
  }

  loading.value = true
  try {
    await userStore.login(name)
    message.success(`欢迎, ${name}!`)
    emit('update:show', false)
    usernameInput.value = ''
  } catch (e: any) {
    message.error(e.message || '登录失败，请重试')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <NModal
    :show="props.show"
    preset="card"
    class="w-full max-w-sm"
    title="欢迎来到 Lucius"
    :bordered="false"
    :segmented="{ content: true }"
    @update:show="emit('update:show', $event)"
  >
    <NSpace vertical :size="20">
      <NInput
        v-model:value="usernameInput"
        placeholder="输入你的名字"
        size="large"
        round
        :disabled="loading"
        @keydown.enter="handleLogin"
      />
      <NButton
        type="primary"
        size="large"
        round
        block
        :disabled="!usernameInput.trim()"
        :loading="loading"
        @click="handleLogin"
      >
        进入世界
      </NButton>
    </NSpace>
    <template #footer>
      <p class="text-center text-gray-500 text-xs">
        仅需输入名字即可开始，无需密码
      </p>
    </template>
  </NModal>
</template>
