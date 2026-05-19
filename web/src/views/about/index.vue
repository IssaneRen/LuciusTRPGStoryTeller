<script setup lang="ts">
import { NCard, NH2, NText, NButton } from 'naive-ui'
</script>

<template>
  <div class="min-h-screen bg-gray-900 text-white p-8 flex flex-col items-center">
    <NH2 class="text-3xl font-bold mb-6">
      <NText type="primary">关于 Lucius TRPG</NText>
    </NH2>

    <NCard class="max-w-2xl bg-gray-800 mb-6">
      <h3 class="text-xl font-semibold mb-3">项目愿景</h3>
      <p class="text-gray-300 leading-relaxed">
        Lucius TRPG 是一个面向桌面角色扮演游戏爱好者的 AI 辅助平台。
        我们致力于利用人工智能技术，为玩家和 GM 提供更丰富的故事体验，
        降低 TRPG 的入门门槛，让更多人能享受到叙事游戏的乐趣。
      </p>
    </NCard>

    <NCard class="max-w-2xl bg-gray-800 mb-6">
      <h3 class="text-xl font-semibold mb-3">技术栈</h3>
      <ul class="text-gray-300 space-y-2">
        <li>前端: Vue 3 + TypeScript + Naive UI + UnoCSS</li>
        <li>后端: Go + Gin + SQLite</li>
        <li>AI: 多模型支持 (Claude / GPT / DeepSeek)</li>
      </ul>
    </NCard>

    <NButton type="primary" tag="a" href="/">返回首页</NButton>
  </div>
</template>
