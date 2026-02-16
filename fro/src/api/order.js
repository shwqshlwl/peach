import request from '@/utils/request'

export const createOrder = (data) => {
  return request.post('/orders/create', data)
}

export const payOrder = (orderId, paymentMethod) => {
  return request.post(`/orders/pay/${orderId}`, null, {
    params: { paymentMethod }
  })
}

export const getUserOrders = (userId) => {
  return request.get(`/orders/user/${userId}`)
}

export const getOrderDetail = (orderId) => {
  return request.get(`/orders/detail/${orderId}`)
}

export const getAllOrders = (params) => {
  return request.get('/orders/all', { params })
}
