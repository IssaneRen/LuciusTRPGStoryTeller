<script setup lang="ts">
import { ref, computed } from 'vue'
import { VueFlow, type Node, type Edge } from '@vue-flow/core'
import dagre from '@dagrejs/dagre'
import '@vue-flow/core/dist/style.css'
import NodeCard from './NodeCard.vue'
import NodeDetail from './NodeDetail.vue'

interface NodeData {
  label: string
  tags?: string[]
  description?: string
  dependencies?: string[]
}

interface Props {
  nodes: Array<{ id: string; data: NodeData }>
  edges: Array<{ id: string; source: string; target: string }>
  selectedTags?: string[]
}

const props = withDefaults(defineProps<Props>(), {
  selectedTags: () => []
})

const selectedNode = ref<{ id: string; data: NodeData; position: { x: number; y: number } } | null>(null)

function getLayoutedElements(nodes: any[], edges: any[]) {
  const g = new dagre.graphlib.Graph()
  g.setDefaultEdgeLabel(() => ({}))
  g.setGraph({ rankdir: 'LR', nodesep: 80, ranksep: 200 })

  nodes.forEach(node => {
    g.setNode(node.id, { width: 180, height: 60 })
  })

  edges.forEach(edge => {
    g.setEdge(edge.source, edge.target)
  })

  dagre.layout(g)

  return nodes.map(node => {
    const pos = g.node(node.id)
    return {
      ...node,
      position: { x: pos.x - 90, y: pos.y - 30 }
    }
  })
}

const layoutedNodes = computed(() => {
  const baseNodes = props.nodes.map(node => ({
    ...node,
    type: 'custom'
  }))
  return getLayoutedElements(baseNodes, props.edges)
})

const layoutedEdges = computed(() => {
  return props.edges.map(edge => ({
    ...edge,
    type: 'smoothstep',
    animated: true,
    style: {
      stroke: '#8b5cf6',
      strokeWidth: 2
    }
  }))
})

function isDimmed(data: NodeData): boolean {
  if (props.selectedTags.length === 0) return false
  if (!data.tags || data.tags.length === 0) return true
  return !data.tags.some(tag => props.selectedTags.includes(tag))
}

function selectNode(nodeProps: any) {
  const node = layoutedNodes.value.find(n => n.id === nodeProps.id)
  if (node && !isDimmed(node.data)) {
    selectedNode.value = {
      id: node.id,
      data: node.data,
      position: node.position
    }
  }
}

function closeDetail() {
  selectedNode.value = null
}
</script>

<template>
  <div class="relative w-full h-full">
    <VueFlow
      :nodes="layoutedNodes"
      :edges="layoutedEdges"
      class="bg-[#0f0f1a]"
      :fit-view-on-init="true"
      :min-zoom="0.2"
      :max-zoom="4"
    >
      <template #node-custom="nodeProps">
        <NodeCard
          :data="nodeProps.data"
          :dimmed="isDimmed(nodeProps.data)"
          @click="selectNode(nodeProps)"
        />
      </template>
    </VueFlow>

    <NodeDetail
      v-if="selectedNode"
      :data="selectedNode.data"
      :position="selectedNode.position"
      @close="closeDetail"
    />
  </div>
</template>
