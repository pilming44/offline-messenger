<template>
  <div class="flex flex-col h-screen">
    <!-- 헤더 -->
    <header class="bg-gray-800 text-white p-4 flex justify-between items-center">
      <h1 class="text-xl font-bold">채팅방</h1>
      <button
          @click="$router.push('/')"
          class="bg-blue-500 px-4 py-2 rounded hover:bg-blue-600"
      >
        홈으로
      </button>
    </header>

    <!-- 콘텐츠 -->
    <div class="flex flex-1 overflow-hidden">
      <!-- 좌측 사용자 목록 -->
      <div class="w-1/4 p-4 bg-gray-200 h-full overflow-y-auto">
        <h2 class="text-xl font-bold mb-4">참여자 목록</h2>
        <ul class="space-y-2">
          <li class="flex justify-between items-center bg-white p-2 rounded">
            <div>
              <span class="font-semibold">ID:</span> {{ currentUserId }} ,
              <span class="font-semibold">Name:</span> {{ currentUserName }}
            </div>
            <span class="text-green-500 font-semibold">본인</span>
          </li>
          <li
              v-for="user in filteredUsers"
              :key="user.id"
              class="flex justify-between items-center bg-white p-2 rounded"
          >
            <div>
              <span class="font-semibold">ID:</span> {{ user.id }},
              <span class="font-semibold">Name:</span> {{ user.name }}
            </div>
          </li>
        </ul>
      </div>

      <!-- 우측 채팅 콘텐츠 -->
      <div class="w-3/4 p-4 flex flex-col">
        <h1 class="text-2xl font-bold mb-4">채팅방 내용</h1>

        <!-- 채팅 내용 표시 -->
        <div
            class="flex-1 overflow-y-auto bg-gray-100 p-4 rounded mb-4 transition-all duration-300"
            ref="chatContainer"
            :style="{ height: `calc(100% - ${messageInputHeight + 24}px)` }"
        >
          <div
              v-for="message in messages"
              :key="message.id"
              class="mb-2 flex items-start"
              :class="{
              'justify-end': message.senderId === currentUserId,
              'justify-start': message.senderId !== currentUserId,
            }"
          >
            <div
                class="max-w-xs p-3 rounded-lg shadow whitespace-pre-wrap break-words"
                :class="message.senderId === currentUserId ? 'bg-blue-500 text-white' : 'bg-gray-200 text-black'"
                @mouseover="showTimestamp(message.id)"
                @mouseleave="hideTimestamp(message.id)"
            >
              <div class="text-sm font-semibold flex items-center">
                {{ message.senderId === currentUserId ? '나' : message.senderName }}
                <span
                    v-if="message.showTimestamp"
                    class="text-xs text-gray-400 ml-2"
                >
                  {{ formatTimestamp(message.createdAt) }}
                </span>
              </div>
              <div class="text-sm break-all">{{ message.content }}</div>
            </div>
          </div>
        </div>

        <!-- 채팅 입력 -->
        <div class="flex items-center space-x-4 bg-white p-4 rounded shadow">
          <textarea
              v-model="messageInput"
              @input="handleInputChange"
              @keydown="handleKeydown"
              placeholder="메시지를 입력하세요..."
              class="border border-gray-300 rounded w-full p-3 resize-none focus:outline-none focus:ring-2 focus:ring-blue-400"
              :style="{ height: messageInputHeight + 'px' }"
          ></textarea>
          <button
              @click="sendMessage"
              class="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 whitespace-nowrap flex justify-center items-center font-semibold"
          >
            전송
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getAllUsersInRoom } from '@/api/chatRoom.js';
import { getCurrentUser } from '@/api/user.js';
import { sendMessage, getMessagesInRoom } from '@/api/message.js';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

export default {
  name: 'Chat',
  data() {
    return {
      stompClient: null,
      currentUserId: null,
      currentUserName: '',
      roomUsers: [], // 현재 채팅방의 사용자 목록
      messages: [], // 채팅 메시지 목록
      messageInput: '', // 입력 메시지
      messageInputHeight: 50, // 입력란 초기 높이
      maxInputHeight: 150, // 입력란 최대 높이
      maxMessageLength: 10000, // 최대 문자 길이
    };
  },
  async created() {
    this.roomId = this.$route.params.roomId;
    await this.fetchCurrentUser();
    await this.fetchRoomUsers();
    await this.fetchMessages();
    this.connectWebSocket();
    this.scrollToBottom();
  },
  computed: {
    filteredUsers() {
      return this.roomUsers.filter(user => user.id !== this.currentUserId);
    },
  },
  methods: {
    connectWebSocket() {
      const token = localStorage.getItem('token'); // JWT 토큰 예시
      console.log("token : " + token);
      // SockJS + STOMP 초기화
      const socket = new SockJS('http://localhost:8080/ws-stomp');
      this.stompClient = Stomp.over(socket);

      // 헤더 객체
      const headers = {
        Authorization: `Bearer ${token}`, // JWT 토큰을 Authorization 헤더에 추가
      };
      console.log("headers : " + headers.Authorization);
      // 연결 시도
      console.log(" -connect session ID:", this.stompClient.ws ? this.stompClient.ws.url : "WebSocket 미연결");
      this.stompClient.connect(
          headers, // ← 이 부분이 중요!
          frame => {
            console.log('WebSocket connected:', frame);

            // 채팅방 메세지 구독
            this.stompClient.subscribe(`/topic/room/${this.roomId}`, message => {
              const msgBody = JSON.parse(message.body);
              this.messages.push({
                ...msgBody,
                showTimestamp: false,
              });
              this.scrollToBottom();
            });
          },
          error => {
            console.error('WebSocket error:', error);
          }
      );
    },
    async fetchCurrentUser() {
      try {
        const response = await getCurrentUser();
        this.currentUserId = response.data.id;
        this.currentUserName = response.data.username;
      } catch (error) {
        console.error('현재 사용자 정보를 가져올 수 없습니다:', error);
        this.$router.push('/login');
      }
    },
    async fetchRoomUsers() {
      try {
        const response = await getAllUsersInRoom(this.roomId);
        this.roomUsers = response.data;
      } catch (error) {
        console.error('채팅방 사용자 정보를 가져올 수 없습니다:', error);
      }
    },
    async fetchMessages() {
      try {
        const response = await getMessagesInRoom(this.roomId);
        this.messages = response.data.map((msg) => ({
          ...msg,
          showTimestamp: false,
        }));
      } catch (error) {
        console.error(error);
      }
    },
    async sendMessage() {
      if (!this.messageInput.trim()) return;

      try {
        // stompClient.send(destination, headers, body)
        console.log(" -send session ID:", this.stompClient.ws ? this.stompClient.ws.url : "WebSocket 미연결");
        this.stompClient.send(
            '/app/chat.send', // @MessageMapping("/chat.send")에 매핑
            {},
            JSON.stringify({
              chatRoomId: this.roomId,
              content: this.messageInput,
            }),
        );

        // 입력창 비우기
        this.messageInput = '';
        this.scrollToBottom();
      } catch (error) {
        console.error('메시지 전송 실패:', error);
      }
    },
    scrollToBottom() {
      this.$nextTick(() => {
        const container = this.$refs.chatContainer;
        if (container) {
          container.scrollTop = container.scrollHeight;
        }
      });
    },
    showTimestamp(messageId) {
      const message = this.messages.find((msg) => msg.id === messageId);
      if (message) message.showTimestamp = true;
    },
    hideTimestamp(messageId) {
      const message = this.messages.find((msg) => msg.id === messageId);
      if (message) message.showTimestamp = false;
    },
    formatTimestamp(timestamp) {
      const date = new Date(timestamp);
      return `${date.getHours()}:${String(date.getMinutes()).padStart(2, '0')}`;
    },
    handleInputChange(event) {
      if (this.messageInput.length > this.maxMessageLength) {
        alert(`메시지는 최대 ${this.maxMessageLength}자까지 입력할 수 있습니다.`);
        this.messageInput = this.messageInput.slice(0, this.maxMessageLength);
      }
      this.adjustInputHeight(event.target);
    },
    handleKeydown(event) {
      if (event.key === 'Enter' && !event.shiftKey) {
        event.preventDefault();
        this.sendMessage();
      }
    },
    adjustInputHeight(target) {
      target.style.height = 'auto'; // 높이 초기화
      const newHeight = Math.min(target.scrollHeight, this.maxInputHeight);
      target.style.height = `${newHeight}px`;
      this.messageInputHeight = newHeight;
    }
  },
};
</script>
