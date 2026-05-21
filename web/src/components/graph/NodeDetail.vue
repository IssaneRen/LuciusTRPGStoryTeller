<script setup lang="ts">
interface NodeData {
  label: string
  tags?: string[]
  description?: string
}

interface Props {
  data: NodeData
  position: { x: number; y: number }
}

defineProps<Props>()
const emit = defineEmits<{ close: [] }>()
</script>

<template>
  <div class="detail-panel" @click.stop>
    <div class="detail-header">
      <h3>{{ data.label }}</h3>
      <button class="close-btn" @click="emit('close')">×</button>
    </div>

    <div v-if="data.tags && data.tags.length > 0" class="detail-tags">
      <span v-for="tag in data.tags" :key="tag" class="tag">{{ tag }}</span>
    </div>

    <p v-if="data.description" class="detail-desc">{{ data.description }}</p>
  </div>
</template>

<style scoped>
.detail-panel {
  position: fixed;
  top: 80px;
  right: 24px;
  width: 320px;
  background: #1a1a2e;
  border: 1px solid rgba(139, 92, 246, 0.3);
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.6);
  z-index: 100;
}
.detail-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 12px;
}
.detail-header h3 {
  color: #e2e8f0;
  font-size: 16px;
  font-weight: 600;
  line-height: 1.4;
  margin: 0;
  flex: 1;
}
.close-btn {
  background: none;
  border: none;
  color: #94a3b8;
  font-size: 20px;
  cursor: pointer;
  padding: 0 4px;
  line-height: 1;
}
.close-btn:hover { color: #fff; }
.detail-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 12px;
}
.tag {
  background: rgba(139, 92, 246, 0.15);
  color: #c4b5fd;
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 4px;
}
.detail-desc {
  color: #94a3b8;
  font-size: 13px;
  line-height: 1.6;
  margin: 0;
}
</style>
