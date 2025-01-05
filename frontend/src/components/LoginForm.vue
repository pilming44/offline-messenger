<template>
  <div class="flex flex-col items-center justify-center min-h-screen bg-gray-100 p-6">
    <h1 class="text-2xl font-bold mb-4">로그인</h1>
    <form @submit.prevent="handleLogin" class="flex flex-col gap-4 w-64">
      <input
          type="text"
          v-model="username"
          placeholder="아이디"
          class="border p-2 rounded"
      />
      <input
          type="password"
          v-model="password"
          placeholder="비밀번호"
          class="border p-2 rounded"
      />
      <button type="submit" class="bg-blue-500 text-white py-2 rounded hover:bg-blue-600">
        로그인
      </button>
      <button
          @click="$router.push('/signup')"
          class="bg-green-500 text-white py-2 px-4 rounded hover:bg-green-600"
      >
        회원가입
      </button>
    </form>

  </div>
</template>

<script>
import { login } from '@/api/auth.js';

export default {
  name: 'LoginForm',
  data() {
    return {
      username: '',
      password: '',
    };
  },
  methods: {
    async handleLogin() {
      try {
        const response = await login({
          username: this.username,
          password: this.password,
        });
        localStorage.setItem('token', response.data.token); // 토큰 저장
        this.$router.push('/'); // 로그인 성공 시 홈 화면으로 이동
      } catch (error) {
        console.error(error);
        alert('로그인 실패');
      }
    },
  },
};
</script>
