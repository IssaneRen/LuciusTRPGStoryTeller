<script setup lang="ts">
import { ref, computed } from 'vue'
import { VueFlow, PanOnScrollMode, type Node, type Edge } from '@vue-flow/core'
import dagre from '@dagrejs/dagre'
import '@vue-flow/core/dist/style.css'
import NodeCard from './NodeCard.vue'
import NodeDetail from './NodeDetail.vue'

interface NodeData {
  label: string
  tags?: string[]
  description?: string
}

interface Props {
  nodes: Array<{ id: string; data: NodeData }>
  edges: Array<{ id: string; source: string; target: string; label?: string }>
  selectedTags?: string[]
}

const props = withDefaults(defineProps<Props>(), {
  selectedTags: () => []
})

const selectedNode = ref<{ id: string; data: NodeData; position: { x: number; y: number } } | null>(null)

function getLayoutedElements(nodes: any[], edges: any[]) {
  const g = new dagre.graphlib.Graph()
  g.setDefaultEdgeLabel(() => ({}))
  g.setGraph({
    rankdir: 'LR',
    nodesep: 60,
    ranksep: 250,
    edgesep: 30,
    marginx: 40,
    marginy: 40,
  })

  nodes.forEach(node => {
    g.setNode(node.id, { width: 200, height: 72 })
  })

  edges.forEach(edge => {
    g.setEdge(edge.source, edge.target)
  })

  dagre.layout(g)

  return nodes.map(node => {
    const pos = g.node(node.id)
    return {
      ...node,
      position: { x: pos.x - 100, y: pos.y - 36 },
      sourcePosition: 'right',
      targetPosition: 'left',
    }
  })
}

function isDimmed(data: NodeData): boolean {
  if (props.selectedTags.length === 0) return false
  if (!data.tags || data.tags.length === 0) return true
  return !data.tags.some(tag => props.selectedTags.includes(tag))
}

function isEdgeHidden(edge: { source: string; target: string }): boolean {
  if (props.selectedTags.length === 0) return false
  const sourceNode = props.nodes.find(n => n.id === edge.source)
  const targetNode = props.nodes.find(n => n.id === edge.target)
  if (!sourceNode || !targetNode) return true
  return isDimmed(sourceNode.data) || isDimmed(targetNode.data)
}

const layoutedNodes = computed<Node[]>(() => {
  const baseNodes = props.nodes.map(node => ({
    ...node,
    type: 'custom',
  }))
  return getLayoutedElements(baseNodes, props.edges)
})

const layoutedEdges = computed<Edge[]>(() => {
  return props.edges.map(edge => ({
    ...edge,
    type: 'smoothstep',
    animated: !isEdgeHidden(edge),
    hidden: isEdgeHidden(edge),
    style: {
      stroke: '#8b5cf6',
      strokeWidth: 2,
    },
    labelStyle: { fill: '#94a3b8', fontSize: 11 },
    labelBgStyle: { fill: '#1e1e2e', fillOpacity: 0.9 },
    label: edge.label || '',
  }))
})

function selectNode(nodeProps: any) {
  const node = layoutedNodes.value.find(n => n.id === nodeProps.id)
  if (node && !isDimmed(node.data)) {
    if (selectedNode.value?.id === node.id) {
      selectedNode.value = null
    } else {
      selectedNode.value = {
        id: node.id,
        data: node.data as NodeData,
        position: node.position,
      }
    }
  }
}

function closeDetail() {
  selectedNode.value = null
}

function handlePaneClick() {
  selectedNode.value = null
}

function handleWheel(e: WheelEvent) {
  if (e.shiftKey) return
}
</script>

<template>
  <div class="relative w-full h-full graph-container" @wheel.passive="handleWheel">
    <VueFlow
      :nodes="layoutedNodes"
      :edges="layoutedEdges"
      class="graph-canvas"
      :fit-view-on-init="true"
      :min-zoom="0.3"
      :max-zoom="3"
      :default-edge-options="{ type: 'smoothstep' }"
      :pan-on-scroll="true"
      :pan-on-scroll-mode="PanOnScrollMode.Horizontal"
      @pane-click="handlePaneClick"
    >
      <template #node-custom="nodeProps">
        <NodeCard
          :data="nodeProps.data"
          :dimmed="isDimmed(nodeProps.data)"
          @click="selectNode(nodeProps)"
        />
      </template>
    </VueFlow>

    <Transition name="detail">
      <NodeDetail
        v-if="selectedNode"
        :data="selectedNode.data"
        :position="selectedNode.position"
        @close="closeDetail"
      />
    </Transition>
  </div>
</template>

<style>
.graph-canvas {
  background: #0f0f1a !important;
}
.graph-canvas .vue-flow__edge-path {
  stroke-linecap: round;
  stroke-linejoin: round;
}
.graph-canvas .vue-flow__edge-text {
  font-size: 11px;
}
.graph-container {
  overflow-x: auto;
  overflow-y: hidden;
}
.detail-enter-active, .detail-leave-active {
  transition: opacity 0.15s ease, transform 0.15s ease;
}
.detail-enter-from, .detail-leave-to {
  opacity: 0;
  transform: translateX(-8px);
}
</style>
