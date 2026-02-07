import request from './request.js'

export function followExpert(userId) {
  return request({ url: `/api/follows/experts/${userId}`, method: 'POST' })
}

export function unfollowExpert(userId) {
  return request({ url: `/api/follows/experts/${userId}`, method: 'DELETE' })
}

export function listMyFollowings(page = 0, size = 20) {
  return request({
    url: `/api/follows/experts?page=${page}&size=${size}`,
    method: 'GET',
  })
}
