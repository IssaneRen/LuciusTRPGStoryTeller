<script setup lang="ts">
import { ref } from 'vue'
import { useUserStore } from '@/stores/user'
import { useMessage } from 'naive-ui'

const props = defineProps<{
  show: boolean
}>()

const emit = defineEmits<{
  'update:show': [value: boolean]
}>()

const userStore = useUserStore()
const message = useMessage()

const usernameInput = ref('')
const loading = ref(false)

const handleLogin = async () => {
  if (!usernameInput.value.trim()) {
    return
  }

  if (usernameInput.value.length < 2 || usernameInput.value.length > 32) {
    message.warning('用户名长度应在 2-32 字符之间')
    return
  }

  loading.value = true
  try {
    await userStore.login(usernameInput.value.trim())
    message.success('欢迎来到 Lucius 世界!')
    emit('update:show', false)
    usernameInput.value = ''
  } catch (error: any) {
    message.error(error.message || '登录失败，请重试')
  } finally {
    loading.value = false
  }
}

const handleClose = () => {
  emit('update:show', false)
  usernameInput.value = ''
}
</script>

<template>
  <NModal :show="props.show" @update:show="handleClose">
    <NCard
      class="w-full max-w-md"
      title="欢迎来到 Lucius"
      :bordered="false"
      role="dialog"
      aria-modal="true"
    >
      <NSpace vertical :size="16">
        <NInput
          v-model:value="usernameInput"
          placeholder="请输入你的名字"
          size="large"
          :disabled="loading"
          @keydown.enter="handleLogin"
        />
        <NButton
          type="primary"
          size="large"
          block
          :disabled="!usernameInput.trim()"
          :loading="loading"
          @click="handleLogin"
        >
          进入世界
        </NButton>
        <div class="text-center text-gray-400 text-sm">
          仅需输入名字即可开始
        </div>
      </NSpace>
    </NCard>
  </NModal>
</template>
