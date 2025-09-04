import request from '@/utils/request'

// 获取书籍列表
export function getBooks() {
  return request({
    url: '/api/books', // 前端请求路径，前缀 /api 会被代理去掉
    method: 'get',
    baseURL: '' // 覆盖 axios 默认 /dev-api 前缀
  }).then(res => {
    // res 是后端返回的整个对象 { code, data, message }
    // 直接返回 data，前端组件直接使用数组
    return res.data
  })
}
