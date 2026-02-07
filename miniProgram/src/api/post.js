import request from './request.js'

/**
 * @param {object} params type, sceneEra, tagIds, bountyMode, page, size
 */
export function listPosts(params = {}) {
  const { type, sceneEra, tagIds, bountyMode, page = 0, size = 20 } = params
  const query = new URLSearchParams()
  if (type) query.append('type', type)
  if (sceneEra) query.append('sceneEra', sceneEra)
  if (bountyMode) query.append('bountyMode', bountyMode)
  if (page !== undefined) query.append('page', page)
  if (size !== undefined) query.append('size', size)
  if (tagIds && tagIds.length) tagIds.forEach(id => query.append('tagIds', id))
  const qs = query.toString()
  return request({ url: `/api/posts${qs ? '?' + qs : ''}`, method: 'GET' })
}

/**
 * @param {object} params q, type, sceneEra, tagIds, bountyMode, page, size
 */
export function searchPosts(params = {}) {
  const { q, type, sceneEra, tagIds, bountyMode, page = 0, size = 20 } = params
  const query = new URLSearchParams()
  if (q) query.append('q', q)
  if (type) query.append('type', type)
  if (sceneEra) query.append('sceneEra', sceneEra)
  if (bountyMode) query.append('bountyMode', bountyMode)
  query.append('page', page)
  query.append('size', size)
  if (tagIds && tagIds.length) tagIds.forEach(id => query.append('tagIds', id))
  return request({ url: `/api/posts/search?${query.toString()}`, method: 'GET' })
}

export function getPost(id) {
  return request({ url: `/api/posts/${id}`, method: 'GET' })
}

export function createPost(data) {
  return request({ url: '/api/posts', method: 'POST', data })
}

export function updatePost(id, data) {
  return request({ url: `/api/posts/${id}`, method: 'PUT', data })
}
