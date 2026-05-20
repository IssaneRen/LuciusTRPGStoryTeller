import { api } from '@/utils/request'

export interface Graph {
  id: string
  name: string
  type: 'clue' | 'module'
  nodes: GraphNode[]
  edges: GraphEdge[]
  nodeCount?: number
}

export interface GraphNode {
  id: string
  label: string
  description?: string
  tags?: string[]
  position: { x: number; y: number }
}

export interface GraphEdge {
  id: string
  source: string
  target: string
}

export interface CreateGraphInput {
  name: string
  type: 'clue' | 'module'
}

export interface UpdateGraphInput {
  name?: string
  type?: 'clue' | 'module'
  nodes?: Array<{ id: string; label: string; description?: string; tags?: string[]; x: number; y: number }>
  edges?: Array<{ id: string; source: string; target: string; label?: string }>
}

interface ApiResponse<T> {
  code: number
  data: T
  message?: string
}

export const graphApi = {
  list: () => api.get<ApiResponse<Graph[]>>('/api/graphs'),
  get: (id: string) => api.get<ApiResponse<Graph>>(`/api/graphs/${id}`),
  create: (data: CreateGraphInput) => api.post<ApiResponse<{ id: string }>>('/api/graphs', data),
  update: (id: string, data: UpdateGraphInput) => api.put<ApiResponse<null>>(`/api/graphs/${id}`, data),
  delete: (id: string) => api.delete<ApiResponse<null>>(`/api/graphs/${id}`),
}
