import request from './request.js'

/**
 * @param {object} params status, page, size
 */
export function listExperts(params = {}) {
  const { status, page = 0, size = 20 } = params
  const query = new URLSearchParams()
  if (status) query.append('status', status)
  query.append('page', String(page))
  query.append('size', String(size))
  return request({ url: `/api/experts?${query.toString()}`, method: 'GET' })
}

export function applyExpert() {
  return request({ url: '/api/experts/apply', method: 'POST' })
}

export function getExpertMe() {
  return request({ url: '/api/experts/me', method: 'GET' })
}
