<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { NCard, NSpin, NButton, NSpace, NTag } from 'naive-ui'
import { useMessage } from 'naive-ui'
import axios from 'axios'
import GraphView from '@/components/graph/GraphView.vue'

const route = useRoute()
const message = useMessage()

const loading = ref(true)
const graphData = ref<{
  nodes: Array<{ id: string; data: any }>
  edges: Array<{ id: string; source: string; target: string }>
} | null>(null)
const selectedTags = ref<string[]>([])

const allTags = computed(() => {
  if (!graphData.value) return []
  const tagSet = new Set<string>()
  graphData.value.nodes.forEach(node => {
    if (node.data.tags) {
      node.data.tags.forEach((tag: string) => tagSet.add(tag))
    }
  })
  return Array.from(tagSet)
})

async function fetchGraphData() {
  loading.value = true
  try {
    const graphId = route.params.id as string
    const response = await axios.get(`/api/graphs/${graphId}`)
    graphData.value = response.data
  } catch (error: any) {
    message.error(error.response?.data?.error || '加载图数据失败')
    console.error('Failed to load graph:', error)
  } finally {
    loading.value = false
  }
}

function toggleTag(tag: string) {
  const index = selectedTags.value.indexOf(tag)
  if (index === -1) {
    selectedTags.value.push(tag)
  } else {
    selectedTags.value.splice(index, 1)
  }
}

function clearTags() {
  selectedTags.value = []
}

onMounted(() => {
  fetchGraphData()
})
</script>

<template>
  <div class="w-full h-screen flex flex-col bg-[#0f0f1a]">
    <!-- Filter Bar -->
    <div class="sticky top-0 z-10 bg-[#1e1e2e] border-b border-solid border-[rgba(139,92,246,0.3)] px-6 py-4">
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-3">
          <span class="text-white text-sm font-medium">标签筛选:</span>
          <NSpace v-if="allTags.length > 0">
            <NTag
              v-for="tag in allTags"
              :key="tag"
              :type="selectedTags.includes(tag) ? 'primary' : 'default'"
              round
              checkable
              :checked="selectedTags.includes(tag)"
              @update:checked="toggleTag(tag)"
              class="cursor-pointer"
            >
              {{ tag }}
            </NTag>
          </NSpace>
          <span v-else class="text-gray-400 text-sm">暂无标签</span>
        </div>
        <NButton
          v-if="selectedTags.length > 0"
          text
          size="small"
          class="text-purple-400 hover:text-purple-300"
          @click="clearTags"
        >
          清除筛选
        </NButton>
      </div>
    </div>

    <!-- Graph Content -->
    <div class="flex-1 relative">
      <NSpin v-if="loading" class="absolute inset-0 flex items-center justify-center">
        <template #description>
          <span class="text-gray-400">加载图数据中...</span>
        </template>
      </NSpin>

      <div v-else-if="!graphData" class="flex items-center justify-center h-full">
        <NCard class="bg-[#1e1e2e] border-[rgba(139,92,246,0.3)]">
          <div class="text-center py-8">
            <div class="text-gray-400 mb-4">未找到图数据</div>
            <NButton type="primary" tag="a" href="/">返回首页</NButton>
          </div>
        </NCard>
      </div>

      <GraphView
        v-else
        :nodes="graphData.nodes"
        :edges="graphData.edges"
        :selected-tags="selectedTags"
      />
    </div>
  </div>
</template>
