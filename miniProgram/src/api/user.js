import request from './request.js'

export function getMe() {
  return request({ url: '/api/users/me', method: 'GET' })
}

export function getMyPosts(page = 0, size = 20) {
  return request({
    url: `/api/users/me/posts?page=${page}&size=${size}`,
    method: 'GET',
  })
}

export function getMyAnswers(page = 0, size = 20) {
  return request({
    url: `/api/users/me/answers?page=${page}&size=${size}`,
    method: 'GET',
  })
}
