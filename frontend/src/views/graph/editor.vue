<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { VueFlow, useVueFlow, type Node, type Edge, type Connection } from '@vue-flow/core'
import '@vue-flow/core/dist/style.css'
import '@vue-flow/core/dist/theme-default.css'
import { NButton, NInput, NModal, useMessage } from 'naive-ui'
import PropertyPanel from '@/components/graph/PropertyPanel.vue'
import { graphApi, type Graph, type GraphNode } from '@/api/graph'

const route = useRoute()
const router = useRouter()
const message = useMessage()

const graphId = computed(() => route.params.id as string)
const graph = ref<Graph | null>(null)
const loading = ref(false)

const nodes = ref<Node[]>([])
const edges = ref<Edge[]>([])
const selectedNode = ref<GraphNode | null>(null)

const showNodeModal = ref(false)
const newNodeName = ref('')
const newNodePosition = ref({ x: 0, y: 0 })

const { onConnect, onNodeClick, onPaneClick, addNodes, addEdges, removeNodes, removeEdges, updateNode } = useVueFlow()

async function loadGraph() {
  loading.value = true
  try {
    const res = await graphApi.get(graphId.value)
    graph.value = res.data.data

    nodes.value = (res.data.data.nodes || []).map((n: any) => ({
      id: n.id,
      type: 'default',
      position: { x: n.x ?? n.position?.x ?? 0, y: n.y ?? n.position?.y ?? 0 },
      label: n.label,
      data: {
        label: n.label,
        description: n.description,
        tags: n.tags,
      },
    }))

    edges.value = (res.data.data.edges || []).map((e) => ({
      id: e.id,
      source: e.source,
      target: e.target,
      type: 'smoothstep',
    }))
  } catch (error: any) {
    message.error(error.response?.data?.message || '加载失败')
    router.push('/graph')
  } finally {
    loading.value = false
  }
}

onConnect((connection: Connection) => {
  const edge: Edge = {
    id: `e${connection.source}-${connection.target}`,
    source: connection.source,
    target: connection.target,
    type: 'smoothstep',
  }
  addEdges([edge])
})

onNodeClick((event) => {
  const node = event.node
  selectedNode.value = {
    id: node.id,
    label: node.data.label || node.label || '',
    description: node.data.description || '',
    tags: node.data.tags || [],
    position: node.position,
  }
})

onPaneClick((event) => {
  if (event.detail === 2) {
    newNodePosition.value = {
      x: event.clientX - 200,
      y: event.clientY - 100,
    }
    newNodeName.value = ''
    showNodeModal.value = true
  } else {
    selectedNode.value = null
  }
})

function handleCreateNode() {
  if (!newNodeName.value.trim()) {
    message.warning('请输入节点名称')
    return
  }

  const nodeId = `node-${Date.now()}`
  const newNode: Node = {
    id: nodeId,
    type: 'default',
    position: newNodePosition.value,
    label: newNodeName.value,
    data: {
      label: newNodeName.value,
      description: '',
      tags: [],
    },
  }

  addNodes([newNode])
  showNodeModal.value = false
  message.success('节点已创建')
}

function handleUpdateNode(updatedNode: GraphNode) {
  updateNode(updatedNode.id, {
    label: updatedNode.label,
    data: {
      label: updatedNode.label,
      description: updatedNode.description,
      tags: updatedNode.tags,
    },
  })
  selectedNode.value = updatedNode
}

function handleDeleteNode() {
  if (!selectedNode.value) return
  removeNodes([selectedNode.value.id])
  selectedNode.value = null
  message.success('节点已删除')
}

async function handleSave() {
  if (!graph.value) return

  try {
    const graphNodes = nodes.value.map((n) => ({
      id: n.id,
      label: n.data.label || n.label || '',
      description: n.data.description || '',
      tags: n.data.tags || [],
      x: n.position.x,
      y: n.position.y,
    }))

    const graphEdges = edges.value.map((e) => ({
      id: e.id,
      source: e.source,
      target: e.target,
    }))

    await graphApi.update(graphId.value, {
      nodes: graphNodes,
      edges: graphEdges,
    })

    message.success('保存成功')
  } catch (error: any) {
    message.error(error.response?.data?.message || '保存失败')
  }
}

function handleBack() {
  router.push('/graph')
}

onMounted(() => {
  loadGraph()
})
</script>

<template>
  <div class="h-screen flex flex-col">
    <div class="flex items-center justify-between p-4 border-b">
      <div class="flex items-center gap-4">
        <NButton @click="handleBack">返回</NButton>
        <h1 class="text-xl font-bold">{{ graph?.name || '加载中...' }}</h1>
      </div>
      <NButton type="primary" @click="handleSave" :loading="loading">保存</NButton>
    </div>

    <div class="flex-1 flex">
      <div class="flex-1">
        <VueFlow
          v-model:nodes="nodes"
          v-model:edges="edges"
          :default-edge-options="{ type: 'smoothstep' }"
          fit-view-on-init
        >
          <template #node-default="{ data }">
            <div class="px-4 py-2 bg-white border-2 border-blue-500 rounded shadow">
              {{ data.label }}
            </div>
          </template>
        </VueFlow>
      </div>

      <div class="w-80 border-l p-4">
        <PropertyPanel
          :node="selectedNode"
          @update="handleUpdateNode"
          @delete="handleDeleteNode"
        />
      </div>
    </div>

    <NModal
      v-model:show="showNodeModal"
      preset="dialog"
      title="新建节点"
      positive-text="创建"
      negative-text="取消"
      @positive-click="handleCreateNode"
    >
      <div>
        <div class="mb-2">节点名称</div>
        <NInput
          v-model:value="newNodeName"
          placeholder="请输入节点名称"
          @keyup.enter="handleCreateNode"
        />
      </div>
    </NModal>
  </div>
</template>

<style scoped>
.vue-flow {
  background: #f5f5f5;
}
</style>
