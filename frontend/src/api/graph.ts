import { api } from '@/utils/request'

export interface Graph {
  id: number
  name: string
  type: 'clue' | 'module'
  nodes: GraphNode[]
  edges: GraphEdge[]
  createdAt: string
  updatedAt: string
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
  nodes?: GraphNode[]
  edges?: GraphEdge[]
}

interface ApiResponse<T> {
  code: number
  data: T
  message?: string
}

export const graphApi = {
  list: () => api.get<ApiResponse<Graph[]>>('/api/graphs'),
  get: (id: number) => api.get<ApiResponse<Graph>>(`/api/graphs/${id}`),
  create: (data: CreateGraphInput) => api.post<ApiResponse<Graph>>('/api/graphs', data),
  update: (id: number, data: UpdateGraphInput) => api.put<ApiResponse<Graph>>(`/api/graphs/${id}`, data),
  delete: (id: number) => api.delete<ApiResponse<null>>(`/api/graphs/${id}`),
}
