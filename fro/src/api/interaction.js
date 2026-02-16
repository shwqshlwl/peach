import request from '@/utils/request'

export const getTreeDiaries = (treeId) => {
  return request.get(`/growth-diary/tree/${treeId}`)
}

export const performAction = (params) => {
  return request.post('/interaction/action', null, { params })
}

export const addDiary = (data) => {
  return request.post('/growth-diary/add', data)
}

export const updateDiary = (data) => {
  return request.put('/growth-diary/update', data)
}

export const deleteDiary = (id) => {
  return request.delete(`/growth-diary/delete/${id}`)
}
