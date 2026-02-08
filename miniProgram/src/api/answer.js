import request from './request.js'

/**
 * @param {number} postId
 * @param {object} data { postId, contentPublic, contentPrivate }
 */
export function createAnswer(postId, data) {
  return request({
    url: `/api/posts/${postId}/answers`,
    method: 'POST',
    data: { ...data, postId },
  })
}

/**
 * @param {number} postId
 * @param {number} page
 * @param {number} size
 */
export function listAnswers(postId, page = 0, size = 20) {
  return request({
    url: `/api/posts/${postId}/answers?page=${page}&size=${size}`,
    method: 'GET',
  })
}

export function acceptAnswer(postId, answerId) {
  return request({
    url: `/api/posts/${postId}/answers/${answerId}/accept`,
    method: 'POST',
  })
}
