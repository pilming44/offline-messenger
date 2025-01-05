import apiClient from './index.js'

export const getMessagesInRoom = (roomId) => {
    // 백엔드: GET /messages/room/{roomId}
    return apiClient.get(`/messages/room/${roomId}`)
}

export const sendMessage = (messageData) => {
    // 백엔드: POST /messages
    // MessageRequest { chatRoomId, content }
    return apiClient.post('/messages', messageData)
}
