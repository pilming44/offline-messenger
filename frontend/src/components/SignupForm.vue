<template>
  <div class="flex flex-col items-center justify-center min-h-screen bg-gray-100 p-6">
    <h1 class="text-2xl font-bold mb-4">회원가입</h1>
    <form @submit.prevent="handleSignup" class="flex flex-col gap-4 w-64">
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
      <input
          type="text"
          v-model="name"
          placeholder="이름"
          class="border p-2 rounded"
      />
      <button type="submit" class="bg-green-500 text-white py-2 rounded hover:bg-green-600">
        회원가입
      </button>
      <button
          @click="$router.push('/login')"
          class="bg-blue-500 text-white py-2 px-4 rounded hover:bg-blue-600"
      >
        로그인으로 돌아가기
      </button>
    </form>
  </div>
</template>

<script>
import { signup } from '@/api/auth.js';

export default {
  name: 'SignupForm',
  data() {
    return {
      username: '',
      password: '',
      name: '',
    };
  },
  methods: {
    async handleSignup() {
      try {
        await signup({
          username: this.username,
          password: this.password,
          name: this.name,
        });
        alert('회원가입 성공!');
        this.$router.push('/login'); // 가입 후 로그인 화면으로 이동
      } catch (error) {
        console.error(error);
        alert('회원가입 실패');
      }
    },
  },
};
</script>
