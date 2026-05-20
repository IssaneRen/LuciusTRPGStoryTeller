<script setup lang="ts">
import { ref, watch } from 'vue'
import { NCard, NInput, NButton, NSpace, NTag, useMessage } from 'naive-ui'
import type { GraphNode } from '@/api/graph'

interface Props {
  node: GraphNode | null
}

interface Emits {
  (e: 'update', node: GraphNode): void
  (e: 'delete'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()
const message = useMessage()

const form = ref({
  label: '',
  description: '',
  tags: [] as string[],
})
const newTag = ref('')

watch(
  () => props.node,
  (node) => {
    if (node) {
      form.value = {
        label: node.label || '',
        description: node.description || '',
        tags: node.tags || [],
      }
    }
  },
  { immediate: true },
)

function handleUpdate() {
  if (!props.node) return
  if (!form.value.label.trim()) {
    message.warning('节点名称不能为空')
    return
  }

  emit('update', {
    ...props.node,
    label: form.value.label,
    description: form.value.description,
    tags: form.value.tags,
  })
  message.success('已更新')
}

function handleAddTag() {
  const tag = newTag.value.trim()
  if (!tag) return
  if (form.value.tags.includes(tag)) {
    message.warning('标签已存在')
    return
  }
  form.value.tags.push(tag)
  newTag.value = ''
  handleUpdate()
}

function handleRemoveTag(tag: string) {
  form.value.tags = form.value.tags.filter((t) => t !== tag)
  handleUpdate()
}

function handleDelete() {
  emit('delete')
}
</script>

<template>
  <NCard v-if="node" title="节点属性" class="h-full">
    <NSpace vertical>
      <div>
        <div class="mb-2 font-semibold">名称</div>
        <NInput
          v-model:value="form.label"
          placeholder="节点名称"
          @blur="handleUpdate"
        />
      </div>

      <div>
        <div class="mb-2 font-semibold">描述</div>
        <NInput
          v-model:value="form.description"
          type="textarea"
          placeholder="节点描述"
          :rows="4"
          @blur="handleUpdate"
        />
      </div>

      <div>
        <div class="mb-2 font-semibold">标签</div>
        <div class="flex flex-wrap gap-2 mb-2">
          <NTag
            v-for="tag in form.tags"
            :key="tag"
            closable
            @close="handleRemoveTag(tag)"
          >
            {{ tag }}
          </NTag>
        </div>
        <div class="flex gap-2">
          <NInput
            v-model:value="newTag"
            placeholder="添加标签"
            @keyup.enter="handleAddTag"
          />
          <NButton @click="handleAddTag">添加</NButton>
        </div>
      </div>

      <div class="pt-4 border-t">
        <NButton type="error" block @click="handleDelete">删除节点</NButton>
      </div>
    </NSpace>
  </NCard>
  <div v-else class="flex items-center justify-center h-full text-gray-400">
    选择节点以编辑属性
  </div>
</template>
