<script setup lang="ts">
import { NButton, NSpace, NCard, NH1, NText } from 'naive-ui'
</script>

<template>
  <div class="min-h-screen bg-gray-900 text-white flex flex-col items-center justify-center p-8">
    <NH1 class="text-4xl font-bold mb-4">
      <NText type="primary">Lucius TRPG</NText>
    </NH1>
    <p class="text-lg text-gray-300 mb-8 max-w-xl text-center">
      AI 驱动的 TRPG 故事讲述平台。在这里，每一个选择都将改变故事的走向。
    </p>

    <NSpace>
      <NButton type="primary" size="large" tag="a" href="/about">
        了解更多
      </NButton>
    </NSpace>

    <div class="mt-16 grid grid-cols-1 md:grid-cols-3 gap-6 max-w-4xl w-full">
      <NCard title="AI 叙事引擎" class="bg-gray-800">
        <NText depth="3">智能生成故事线，根据玩家选择动态调整剧情发展。</NText>
      </NCard>
      <NCard title="角色扮演" class="bg-gray-800">
        <NText depth="3">创建独特角色，在沉浸式的世界观中展开冒险。</NText>
      </NCard>
      <NCard title="多人协作" class="bg-gray-800">
        <NText depth="3">支持多人同时参与，共同编织属于你们的故事。</NText>
      </NCard>
    </div>
  </div>
</template>
