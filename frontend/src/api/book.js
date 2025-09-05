import request from '@/utils/request'

// 获取书籍列表
export function getBooks() {
  return request({
    url: '/api/books', // 前端请求路径，前缀 /api 会被代理去掉
    method: 'get'
  }).then(res => {
    // res 是后端返回的整个对象 { code, data, message }
    // 直接返回 data，前端组件直接使用数组
    return res.data
  })
}

export function recommendBooks(requestData) {
  return request({
    url: '/api/books/recommend',
    method: 'post',
    data: requestData,
    headers: {
      'Content-Type': 'application/json'
    }
  }).then(res => {
    return res
  })
}
