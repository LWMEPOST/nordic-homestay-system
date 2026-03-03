import axios from 'axios'
import router from '../router'
import { showToast } from 'vant'

const api = axios.create({
  baseURL: '/api',
  timeout: 5000
})

api.interceptors.request.use(config => {
  const token = localStorage.getItem('client_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  response => response,
  error => {
    if (error.response) {
      if (error.response.status === 401 || error.response.status === 403) {
        localStorage.removeItem('client_token')
        localStorage.removeItem('client_user')
        
        // Don't redirect if already on login page
        if (router.currentRoute.value.path !== '/login') {
          router.push({
            path: '/login',
            query: { redirect: router.currentRoute.value.fullPath }
          })
        }
      }
      showToast(error.response.data?.message || '请求失败')
    } else {
      showToast('网络错误')
    }
    return Promise.reject(error)
  }
)

export default api
