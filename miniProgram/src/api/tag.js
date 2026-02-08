import request from './request.js'

/**
 * @param {string} category 可选
 */
export function listTags(category) {
  const qs = category ? `?category=${encodeURIComponent(category)}` : ''
  return request({ url: `/api/tags${qs}`, method: 'GET' })
}
