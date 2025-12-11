import axios from 'axios'

// 모든 요청의 baseURL 및 기본 헤더 설정
const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
})

export default api
