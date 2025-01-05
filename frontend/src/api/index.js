import axios from 'axios'

// 기본 인스턴스 생성
const apiClient = axios.create({
    baseURL: '/',   // vite.config.js에서 proxy 설정된 경로
    headers: {
        'Content-Type': 'application/json',
    }
})

// 토큰이 필요하다면 요청 헤더에 추가할 수도 있음
apiClient.interceptors.request.use(config => {
    const token = localStorage.getItem('token') // 로그인 후 저장되는 토큰
    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    }
    return config
})

export default apiClient
