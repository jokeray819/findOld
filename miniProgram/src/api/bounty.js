import request from './request.js'

export function createBountyOrder(data) {
  return request({ url: '/api/bounty/orders', method: 'POST', data })
}

export function listMyBountyOrders(page = 0, size = 20) {
  return request({
    url: `/api/bounty/orders?page=${page}&size=${size}`,
    method: 'GET',
  })
}

export function escrowBountyOrder(id) {
  return request({ url: `/api/bounty/orders/${id}/escrow`, method: 'POST' })
}

export function confirmPayBountyOrder(id) {
  return request({ url: `/api/bounty/orders/${id}/confirm-pay`, method: 'POST' })
}
