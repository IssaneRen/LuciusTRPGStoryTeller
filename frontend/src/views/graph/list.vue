<script setup lang="ts">
import { ref, onMounted, h } from 'vue'
import { useRouter } from 'vue-router'
import { NButton, NDataTable, NModal, NInput, NSelect, NSpace, useMessage, useDialog } from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import { graphApi, type Graph, type CreateGraphInput } from '@/api/graph'

const router = useRouter()
const message = useMessage()
const dialog = useDialog()

const graphs = ref<Graph[]>([])
const loading = ref(false)
const showCreateModal = ref(false)
const createForm = ref<CreateGraphInput>({
  name: '',
  type: 'clue',
})

const typeOptions = [
  { label: '线索图', value: 'clue' },
  { label: '模组图', value: 'module' },
]

const columns: DataTableColumns<Graph> = [
  { title: 'ID', key: 'id', width: 80 },
  { title: '名称', key: 'name' },
  {
    title: '类型',
    key: 'type',
    width: 120,
    render: (row) => (row.type === 'clue' ? '线索图' : '模组图'),
  },
  {
    title: '节点数',
    key: 'nodes',
    width: 100,
    render: (row) => row.nodes?.length || 0,
  },
  {
    title: '边数',
    key: 'edges',
    width: 100,
    render: (row) => row.edges?.length || 0,
  },
  {
    title: '操作',
    key: 'actions',
    width: 180,
    render: (row) => {
      return h('div', { style: 'display: flex; gap: 8px;' }, [
        h(NButton, { size: 'small', onClick: () => handleEdit(row.id) }, { default: () => '编辑' }),
        h(NButton, { size: 'small', type: 'error', onClick: () => handleDelete(row) }, { default: () => '删除' }),
      ])
    },
  },
]

async function loadGraphs() {
  loading.value = true
  try {
    const res = await graphApi.list()
    graphs.value = res.data.data
  } catch (error: any) {
    message.error(error.response?.data?.message || '加载失败')
  } finally {
    loading.value = false
  }
}

function handleEdit(id: number) {
  router.push(`/graph/${id}/edit`)
}

function handleDelete(graph: Graph) {
  dialog.warning({
    title: '确认删除',
    content: `确定要删除图 "${graph.name}" 吗？`,
    positiveText: '删除',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await graphApi.delete(graph.id)
        message.success('删除成功')
        await loadGraphs()
      } catch (error: any) {
        message.error(error.response?.data?.message || '删除失败')
      }
    },
  })
}

function openCreateModal() {
  createForm.value = { name: '', type: 'clue' }
  showCreateModal.value = true
}

async function handleCreate() {
  if (!createForm.value.name.trim()) {
    message.warning('请输入图名称')
    return
  }

  try {
    const res = await graphApi.create(createForm.value)
    message.success('创建成功')
    showCreateModal.value = false
    router.push(`/graph/${res.data.data.id}/edit`)
  } catch (error: any) {
    message.error(error.response?.data?.message || '创建失败')
  }
}

onMounted(() => {
  loadGraphs()
})
</script>

<template>
  <div class="p-6">
    <div class="flex justify-between items-center mb-4">
      <h1 class="text-2xl font-bold">图管理</h1>
      <NButton type="primary" @click="openCreateModal">新建图</NButton>
    </div>

    <NDataTable
      :columns="columns"
      :data="graphs"
      :loading="loading"
      :bordered="false"
    />

    <NModal
      v-model:show="showCreateModal"
      preset="dialog"
      title="新建图"
      positive-text="创建"
      negative-text="取消"
      @positive-click="handleCreate"
    >
      <NSpace vertical>
        <div>
          <div class="mb-2">名称</div>
          <NInput
            v-model:value="createForm.name"
            placeholder="请输入图名称"
          />
        </div>
        <div>
          <div class="mb-2">类型</div>
          <NSelect
            v-model:value="createForm.type"
            :options="typeOptions"
          />
        </div>
      </NSpace>
    </NModal>
  </div>
</template>
