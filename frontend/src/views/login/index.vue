<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { NCard, NForm, NFormItem, NInput, NButton, useMessage } from 'naive-ui'

const router = useRouter()
const route = useRoute()
const user = useUserStore()
const message = useMessage()

const form = ref({ username: '', password: '' })
const loading = ref(false)

async function handleSubmit() {
  if (!form.value.username) {
    message.warning('用户名不能为空')
    return
  }
  if (!form.value.password) {
    message.warning('密码不能为空')
    return
  }
  loading.value = true
  try {
    await user.login(form.value.username, form.value.password)
    const redirect = (route.query.redirect as string) || '/dashboard'
    router.push(redirect)
  } catch {
    message.error('登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="h-screen flex items-center justify-center bg-gray-50">
    <NCard class="w-96" title="登录">
      <NForm @submit.prevent="handleSubmit">
        <NFormItem label="用户名">
          <NInput v-model:value="form.username" placeholder="请输入用户名" />
        </NFormItem>
        <NFormItem label="密码">
          <NInput v-model:value="form.password" type="password" placeholder="请输入密码" />
        </NFormItem>
        <NButton type="primary" block :loading="loading" attr-type="submit">
          登录
        </NButton>
      </NForm>
    </NCard>
  </div>
</template>
