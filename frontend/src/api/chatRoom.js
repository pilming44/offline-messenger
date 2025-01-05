import apiClient from './index.js'

export const createRoom = (roomData) => {
    // 백엔드: POST /rooms
    // ChatRoomRequest { roomName: string, userIds: Long[] }
    return apiClient.post('/rooms', roomData)
}

export const getRoom = (roomId) => {
    // 백엔드: GET /rooms/{roomId}
    return apiClient.get(`/rooms/${roomId}`)
}

export const getAllRooms = () => {
    // 백엔드: GET /rooms
    return apiClient.get('/rooms')
}

// 사용자 채팅방 목록 가져오기
export const getUsersRoom = (userId) => {
    return apiClient.get(`/rooms/users/${userId}`);
};

// 채팅방 사용자 목록 가져오기
export const getAllUsersInRoom = (roomId) => {
    return apiClient.get(`/rooms/${roomId}/users`);
};

export const leaveRoom = (roomId, userId) => {
    // 백엔드: DELETE /rooms/{roomId}/users/{userId}
    return apiClient.delete(`/rooms/${roomId}/users/${userId}`)
}
