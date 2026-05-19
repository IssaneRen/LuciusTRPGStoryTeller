<script setup lang="ts">
import { NButton, NCard, NText, NSpace } from 'naive-ui'
</script>

<template>
  <div class="flex flex-col items-center justify-center px-8 py-20">
    <!-- Hero Section -->
    <div class="text-center max-w-2xl mb-16">
      <h1 class="text-5xl font-bold text-white mb-6 leading-tight">
        让 AI 为你<span class="text-purple-400">讲述故事</span>
      </h1>
      <p class="text-lg text-gray-400 mb-10 leading-relaxed">
        AI 驱动的 TRPG 故事讲述平台。每一个选择都将改变故事的走向，
        每一次冒险都是独一无二的旅程。
      </p>
      <NSpace justify="center">
        <NButton type="primary" size="large" round tag="a" href="/about">
          了解更多
        </NButton>
      </NSpace>
    </div>

    <!-- Feature Cards -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-8 max-w-5xl w-full">
      <NCard class="bg-gray-800/50 border-gray-700 hover:border-purple-500/50 transition-all duration-300">
        <template #header>
          <div class="flex items-center gap-2">
            <span class="text-2xl">🎭</span>
            <span class="text-white font-medium">AI 叙事引擎</span>
          </div>
        </template>
        <NText class="text-gray-400">
          智能生成故事线，根据玩家选择动态调整剧情发展，每次冒险都不同。
        </NText>
      </NCard>

      <NCard class="bg-gray-800/50 border-gray-700 hover:border-purple-500/50 transition-all duration-300">
        <template #header>
          <div class="flex items-center gap-2">
            <span class="text-2xl">⚔️</span>
            <span class="text-white font-medium">角色扮演</span>
          </div>
        </template>
        <NText class="text-gray-400">
          创建独特角色，在沉浸式的世界观中展开冒险，体验自由的角色成长。
        </NText>
      </NCard>

      <NCard class="bg-gray-800/50 border-gray-700 hover:border-purple-500/50 transition-all duration-300">
        <template #header>
          <div class="flex items-center gap-2">
            <span class="text-2xl">🤝</span>
            <span class="text-white font-medium">多人协作</span>
          </div>
        </template>
        <NText class="text-gray-400">
          支持多人同时参与，共同编织属于你们的史诗故事。
        </NText>
      </NCard>
    </div>
  </div>
</template>
