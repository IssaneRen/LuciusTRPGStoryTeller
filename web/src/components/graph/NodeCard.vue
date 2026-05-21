<script setup lang="ts">
import { Handle, Position } from '@vue-flow/core'

interface NodeData {
  label: string
  tags?: string[]
  description?: string
}

interface Props {
  data: NodeData
  dimmed?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  dimmed: false
})

const emit = defineEmits<{ click: [] }>()
</script>

<template>
  <div
    class="node-card"
    :class="{ dimmed }"
    @click="emit('click')"
  >
    <Handle type="target" :position="Position.Left" class="handle" />
    <div class="node-title">{{ data.label }}</div>
    <div v-if="data.tags && data.tags.length > 0" class="node-tags">
      <span v-for="tag in data.tags.slice(0, 3)" :key="tag" class="tag">{{ tag }}</span>
    </div>
    <Handle type="source" :position="Position.Right" class="handle" />
  </div>
</template>

<style scoped>
.node-card {
  width: 200px;
  padding: 12px 14px;
  background: #1a1a2e;
  border: 1px solid rgba(139, 92, 246, 0.35);
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}
.node-card:hover {
  border-color: rgba(139, 92, 246, 0.8);
  box-shadow: 0 0 12px rgba(139, 92, 246, 0.2);
}
.node-card.dimmed {
  opacity: 0.12;
  filter: blur(2px);
  pointer-events: none;
}
.node-title {
  color: #e2e8f0;
  font-size: 13px;
  font-weight: 500;
  line-height: 1.4;
  margin-bottom: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.node-tags {
  display: flex;
  gap: 4px;
  flex-wrap: wrap;
}
.tag {
  background: rgba(139, 92, 246, 0.15);
  color: #c4b5fd;
  font-size: 10px;
  padding: 1px 6px;
  border-radius: 4px;
}
.handle {
  width: 6px;
  height: 6px;
  background: #8b5cf6;
  border: none;
}
</style>
