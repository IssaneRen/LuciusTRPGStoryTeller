<script setup lang="ts">
import { computed } from 'vue'

interface NodeData {
  label: string
  tags?: string[]
  description?: string
  dependencies?: string[]
}

interface Props {
  data: NodeData
  dimmed?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  dimmed: false
})

const emit = defineEmits<{
  click: []
}>()

const cardClasses = computed(() => [
  'node-card',
  'w-180px',
  'bg-[#1e1e2e]',
  'border',
  'border-solid',
  'rounded-12px',
  'p-3',
  'cursor-pointer',
  'transition-all',
  'duration-200',
  props.dimmed ? 'opacity-15 pointer-events-none' : 'border-[rgba(139,92,246,0.3)] hover:border-[rgba(139,92,246,0.7)]'
])
</script>

<template>
  <div :class="cardClasses" @click="emit('click')">
    <div class="text-white text-sm font-medium mb-2 line-clamp-2">
      {{ data.label }}
    </div>
    <div v-if="data.tags && data.tags.length > 0" class="flex flex-wrap gap-1">
      <span
        v-for="tag in data.tags"
        :key="tag"
        class="bg-[rgba(139,92,246,0.2)] text-[#c4b5fd] text-xs px-2 py-0.5 rounded-9999px"
      >
        {{ tag }}
      </span>
    </div>
  </div>
</template>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
