import request from '@/utils/request'

export const getUserAddresses = (userId) => {
  return request.get(`/address/user/${userId}`)
}

export const addAddress = (data) => {
  return request.post('/address/add', data)
}

export const updateAddress = (data) => {
  return request.put('/address/update', data)
}

export const deleteAddress = (id) => {
  return request.delete(`/address/delete/${id}`)
}
