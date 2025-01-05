<template>
  <div class="flex flex-col h-screen">
    <!-- 헤더 -->
    <header class="bg-gray-800 text-white p-4 flex justify-between items-center">
      <h1 class="text-xl font-bold">채팅 서비스</h1>
      <button
          @click="handleLogout"
          class="bg-red-500 px-4 py-2 rounded hover:bg-red-600"
      >
        로그아웃
      </button>
    </header>

    <!-- 콘텐츠 -->
    <div class="flex flex-1">
      <!-- 좌측 사용자 목록 -->
      <div class="w-1/4 p-4 bg-gray-200 h-full">
        <h2 class="text-xl font-bold mb-4">사용자 목록</h2>
        <ul class="space-y-2">
          <!-- 본인 계정 표시 -->
          <li v-if="currentUserId" class="flex justify-between items-center bg-white p-2 rounded">
            <div>
              <span class="font-semibold">ID:</span> {{ currentUserId }},
              <span class="font-semibold">Name:</span> {{ currentUserName }}
            </div>
            <span class="text-green-500 font-semibold">본인</span>
          </li>
          <!-- 나머지 사용자 목록 -->
          <li
              v-for="user in filteredUsers"
              :key="user.id"
              class="flex justify-between items-center bg-white p-2 rounded"
          >
            <div>
              <span class="font-semibold">ID:</span> {{ user.id }},
              <span class="font-semibold">Name:</span> {{ user.name }}
            </div>
            <button
                class="bg-blue-500 text-white px-2 py-1 rounded hover:bg-blue-600"
                @click="toggleUserSelection(user.id)"
            >
              {{ selectedUsers.includes(user.id) ? '선택 해제' : '선택' }}
            </button>
          </li>
        </ul>
      </div>

      <!-- 우측 채팅방 목록 -->
      <div class="w-3/4 p-4">
        <h1 class="text-2xl font-bold mb-4">채팅방 목록</h1>
        <div class="mb-4">
          <input
              v-model="roomName"
              placeholder="채팅방 이름"
              class="border p-2 rounded mr-2"
          />
          <button
              @click="createNewRoom"
              class="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600"
          >
            새 채팅방 생성
          </button>
        </div>

        <ul class="space-y-2">
          <li
              v-for="room in rooms"
              :key="room.id"
              class="bg-gray-100 p-2 rounded flex justify-between"
          >
            <div>
              <span class="font-semibold">ID:</span> {{ room.id }},
              <span class="font-semibold">Name:</span> {{ room.name }}
            </div>
            <button
                class="text-blue-500 underline"
                @click="goToRoom(room.id)"
            >
              입장
            </button>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
import { getUsersRoom, createRoom } from '@/api/chatRoom.js';
import { getAllUsers } from '@/api/user.js';
import { getCurrentUser } from '@/api/user.js';

export default {
  name: 'Home',
  data() {
    return {
      rooms: [],
      roomName: '',
      allUsers: [], // 전체 사용자 목록
      selectedUsers: [], // 선택된 사용자 ID
      currentUserId: null, // 현재 로그인된 사용자 ID
      currentUserName: '', // 현재 로그인된 사용자 이름
    };
  },
  async created() {
    await this.fetchCurrentUser(); // 현재 사용자 정보 가져오기
    await this.fetchRooms();
    await this.fetchUsers();
  },
  computed: {
    // 본인을 제외한 사용자 목록
    filteredUsers() {
      return this.allUsers.filter(user => user.id !== this.currentUserId);
    },
  },
  methods: {
    async fetchCurrentUser() {
      try {
        const response = await getCurrentUser();
        this.currentUserId = response.data.id;
        this.currentUserName = response.data.name;
      } catch (error) {
        console.error('현재 사용자 정보를 가져올 수 없습니다:', error);
        this.$router.push('/login'); // 로그인 정보 없으면 로그인 화면으로 이동
      }
    },
    async fetchRooms() {
      try {
        const response = await getUsersRoom(this.currentUserId);
        this.rooms = response.data;
      } catch (error) {
        console.error(error);
      }
    },
    async fetchUsers() {
      try {
        const response = await getAllUsers();
        this.allUsers = response.data;
      } catch (error) {
        console.error(error);
      }
    },
    toggleUserSelection(userId) {
      if (this.selectedUsers.includes(userId)) {
        this.selectedUsers = this.selectedUsers.filter(id => id !== userId);
      } else {
        this.selectedUsers.push(userId);
      }
    },
    async createNewRoom() {
      try {
        if (!this.roomName.trim()) {
          alert('채팅방 이름을 입력해주세요.');
          return;
        }

        const participants = [this.currentUserId, ...this.selectedUsers];
        const requestData = {
          roomName: this.roomName,
          userIds: participants,
        };

        const response = await createRoom(requestData);
        alert(`방 생성 완료: ID=${response.data.id}`);
        this.roomName = '';
        this.selectedUsers = [];
        this.fetchRooms();
      } catch (error) {
        console.error(error);
      }
    },
    goToRoom(roomId) {
      this.$router.push(`/chat/${roomId}`);
    },
    handleLogout() {
      localStorage.removeItem('token'); // 토큰 삭제
      this.$router.push('/login'); // 로그인 화면으로 이동
    },
  },
};
</script>
