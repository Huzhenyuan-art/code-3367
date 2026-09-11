import request from './request'

export const userApi = {
  register(data) {
    return request.post('/api/users/register', data)
  },
  login(data) {
    return request.post('/api/users/login', data)
  },
  getUserInfo() {
    return request.get('/api/users/info')
  },
  recharge(amount) {
    return request.post('/api/users/recharge', { amount })
  }
}

export const dishApi = {
  getList() {
    return request.get('/api/dishes/list')
  },
  getById(id) {
    return request.get(`/api/dishes/${id}`)
  },
  add(data) {
    return request.post('/api/dishes/add', data)
  },
  update(id, data) {
    return request.put(`/api/dishes/${id}`, data)
  },
  delete(id) {
    return request.delete(`/api/dishes/${id}`)
  }
}

export const orderApi = {
  create(data) {
    return request.post('/api/orders/create', data)
  },
  getMyOrders() {
    return request.get('/api/orders/my')
  },
  getAllOrders() {
    return request.get('/api/orders/all')
  },
  getOrderItems(orderId) {
    return request.get(`/api/orders/${orderId}/items`)
  },
  updateStatus(orderId, status) {
    return request.put(`/api/orders/${orderId}/status`, { status })
  }
}
