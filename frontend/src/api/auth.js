import apiClient from './index.js'

export const signup = (signupData) => {
    // 백엔드: POST /auth/signup
    return apiClient.post('/auth/signup', signupData)
}

export const login = (loginData) => {
    // 백엔드: POST /auth/login
    return apiClient.post('/auth/login', loginData)
}

export const logout = () => {
    // 백엔드: POST /auth/logout
    return apiClient.post('/auth/logout')
}
