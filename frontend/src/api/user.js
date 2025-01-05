import apiClient from './index.js'

export const getAllUsers = () => {
    // 백엔드: GET /users
    return apiClient.get('/users')
}
export const getCurrentUser = () => {
    return apiClient.get('/users/me');
};
