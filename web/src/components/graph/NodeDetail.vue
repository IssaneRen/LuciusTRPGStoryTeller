<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { NButton, NText } from 'naive-ui'

interface NodeData {
  label: string
  tags?: string[]
  description?: string
  dependencies?: string[]
}

interface Props {
  data: NodeData
  position: { x: number; y: number }
}

const props = defineProps<Props>()
const emit = defineEmits<{
  close: []
}>()

const detailRef = ref<HTMLElement | null>(null)

const handleClickOutside = (e: MouseEvent) => {
  if (detailRef.value && !detailRef.value.contains(e.target as Node)) {
    emit('close')
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<template>
  <Transition name="detail">
    <div
      ref="detailRef"
      class="fixed z-50 bg-[#1e1e2e] rounded-12px p-6 shadow-[0_8px_32px_rgba(0,0,0,0.5)] max-w-400px min-w-320px border border-solid border-[rgba(139,92,246,0.3)]"
      :style="{
        left: `${position.x + 200}px`,
        top: `${position.y}px`
      }"
    >
      <!-- Header -->
      <div class="flex items-start justify-between mb-4">
        <h3 class="text-white text-lg font-semibold flex-1 pr-2">
          {{ data.label }}
        </h3>
        <NButton
          text
          size="small"
          class="text-gray-400 hover:text-white"
          @click="emit('close')"
        >
          ✕
        </NButton>
      </div>

      <!-- Tags -->
      <div v-if="data.tags && data.tags.length > 0" class="flex flex-wrap gap-2 mb-4">
        <span
          v-for="tag in data.tags"
          :key="tag"
          class="bg-[rgba(139,92,246,0.2)] text-[#c4b5fd] text-sm px-3 py-1 rounded-9999px"
        >
          {{ tag }}
        </span>
      </div>

      <!-- Description -->
      <div v-if="data.description" class="mb-4">
        <NText class="text-gray-300 text-sm leading-relaxed">
          {{ data.description }}
        </NText>
      </div>

      <!-- Dependencies -->
      <div v-if="data.dependencies && data.dependencies.length > 0">
        <h4 class="text-gray-400 text-sm font-medium mb-2">依赖关系</h4>
        <ul class="space-y-1">
          <li
            v-for="dep in data.dependencies"
            :key="dep"
            class="text-gray-300 text-sm pl-4 relative before:content-['→'] before:absolute before:left-0 before:text-purple-400"
          >
            {{ dep }}
          </li>
        </ul>
      </div>
    </div>
  </Transition>
</template>

<style scoped>
.detail-enter-active, .detail-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}
.detail-enter-from {
  opacity: 0;
  transform: translateX(-8px);
}
.detail-leave-to {
  opacity: 0;
  transform: translateX(-8px);
}
</style>
