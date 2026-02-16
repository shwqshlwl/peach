import request from '@/utils/request'

// 创建JSAPI支付订单（公众号/小程序支付）
export const createWeChatJsapiOrder = (data) => {
  return request.post('/payment/wechat/jsapi/create', null, { params: data })
}

// 创建Native支付订单（扫码支付）
export const createWeChatNativeOrder = (orderId) => {
  return request.post('/payment/wechat/native/create', null, { params: { orderId } })
}

// 查询支付状态
export const queryPaymentStatus = (orderNo) => {
  return request.get(`/payment/wechat/query/${orderNo}`)
}

// 关闭订单
export const closePaymentOrder = (orderNo) => {
  return request.post(`/payment/wechat/close/${orderNo}`)
}
