import request from '@/utils/request'

export const login = (data) => {
  return request.post('/auth/login', data)
}

export const register = (data) => {
  return request.post('/auth/register', data)
}

export const getUserInfo = (id) => {
  return request.get(`/auth/user/${id}`)
}

export const updateUserInfo = (data) => {
  return request.put('/auth/user/update', data)
}
