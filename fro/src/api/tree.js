import request from '@/utils/request'

export const getTreeList = (params) => {
  return request.get('/peach-trees/list', { params })
}

export const getTreeDetail = (id) => {
  return request.get(`/peach-trees/detail/${id}`)
}

export const getAllTrees = () => {
  return request.get('/peach-trees/all')
}

export const saveTree = (data) => {
  return request.post('/peach-trees/save', data)
}

export const deleteTree = (id) => {
  return request.delete(`/peach-trees/delete/${id}`)
}
