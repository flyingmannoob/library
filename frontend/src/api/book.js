import request from '@/utils/request'

// 获取图书列表
export function getBooks(params) {
  return request({
    url: '/vue-admin-template/book/list',
    method: 'get',
    params
  })
}

// 获取图书详情
export function getBook(id) {
  return request({
    url: `/vue-admin-template/book/${id}`,
    method: 'get'
  })
}

// 创建图书
export function createBook(data) {
  return request({
    url: '/vue-admin-template/book/create',
    method: 'post',
    data
  })
}

// 更新图书
export function updateBook(id, data) {
  return request({
    url: `/vue-admin-template/book/${id}`,
    method: 'put',
    data
  })
}

// 删除图书
export function deleteBook(id) {
  return request({
    url: `/vue-admin-template/book/${id}`,
    method: 'delete'
  })
}

// 获取分类列表
export function getCategories() {
  return request({
    url: '/vue-admin-template/categories',
    method: 'get'
  })
}
