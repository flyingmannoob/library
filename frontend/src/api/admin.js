import request from '@/utils/request'

// 获取所有用户
export function getUsers() {
  return request({
    url: '/vue-admin-template/admin/users',
    method: 'get'
  })
}

// 创建用户
export function createUser(data) {
  return request({
    url: '/vue-admin-template/admin/users',
    method: 'post',
    data
  })
}

// 更新用户信息
export function updateUser(id, data) {
  return request({
    url: `/vue-admin-template/admin/users/${id}`,
    method: 'put',
    data
  })
}

// 删除用户
export function deleteUser(id) {
  return request({
    url: `/vue-admin-template/admin/users/${id}`,
    method: 'delete'
  })
}
