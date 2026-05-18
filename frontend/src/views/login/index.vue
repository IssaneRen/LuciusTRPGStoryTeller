<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { NCard, NForm, NFormItem, NInput, NButton, NSpace, useMessage } from 'naive-ui'

const router = useRouter()
const route = useRoute()
const user = useUserStore()
const message = useMessage()

const form = ref({ username: '', password: '' })
const loading = ref(false)
const isRegister = ref(false)

async function handleSubmit() {
  if (form.value.username.length < 3 || form.value.password.length < 6) {
    message.warning('用户名至少3位，密码至少6位')
    return
  }
  loading.value = true
  try {
    if (isRegister.value) {
      await import('@/utils/request').then(({ api }) =>
        api.post('/api/auth/register', form.value),
      )
      message.success('注册成功，请登录')
      isRegister.value = false
    } else {
      await user.login(form.value.username, form.value.password)
      const redirect = (route.query.redirect as string) || '/dashboard'
      router.push(redirect)
    }
  } catch {
    message.error(isRegister.value ? '注册失败' : '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="h-screen flex items-center justify-center bg-gray-50">
    <NCard class="w-96" :title="isRegister ? '注册' : '登录'">
      <NForm @submit.prevent="handleSubmit">
        <NFormItem label="用户名">
          <NInput v-model:value="form.username" placeholder="请输入用户名" />
        </NFormItem>
        <NFormItem label="密码">
          <NInput v-model:value="form.password" type="password" placeholder="请输入密码" />
        </NFormItem>
        <NSpace vertical>
          <NButton type="primary" block :loading="loading" attr-type="submit">
            {{ isRegister ? '注册' : '登录' }}
          </NButton>
          <NButton text @click="isRegister = !isRegister">
            {{ isRegister ? '已有账号？去登录' : '没有账号？去注册' }}
          </NButton>
        </NSpace>
      </NForm>
    </NCard>
  </div>
</template>
