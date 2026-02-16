import request from '@/utils/request'

export const getCommunityPosts = (params) => {
  return request.get('/community/posts', { params })
}

export const createPost = (data) => {
  return request.post('/community/post', data)
}

export const getPostDetail = (id) => {
  return request.get(`/community/post/${id}`)
}

export const likePost = (postId) => {
  return request.post(`/community/like/${postId}`)
}
