import request from './request.js'

export function addFavorite(postId) {
  return request({ url: `/api/favorites/posts/${postId}`, method: 'POST' })
}

export function removeFavorite(postId) {
  return request({ url: `/api/favorites/posts/${postId}`, method: 'DELETE' })
}

export function listMyFavorites(page = 0, size = 20) {
  return request({
    url: `/api/favorites/posts?page=${page}&size=${size}`,
    method: 'GET',
  })
}
