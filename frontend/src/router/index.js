import { createRouter, createWebHistory } from 'vue-router';
import Home from '../pages/Home.vue';
import Chat from '../pages/Chat.vue';
import LoginForm from '../components/LoginForm.vue';
import SignupForm from '../components/SignupForm.vue';

// 라우트 정의
const routes = [
    { path: '/', name: 'Home', component: Home, meta: { requiresAuth: true } },
    { path: '/login', name: 'Login', component: LoginForm },
    { path: '/signup', name: 'Signup', component: SignupForm },
    { path: '/chat/:roomId', name: 'Chat', component: Chat, meta: { requiresAuth: true } },
];

// 라우터 생성
const router = createRouter({
    history: createWebHistory(),
    routes,
});

// 전역 가드 설정
router.beforeEach((to, from, next) => {
    const isAuthenticated = !!localStorage.getItem('token'); // 토큰 기반 로그인 상태 확인

    if (to.meta.requiresAuth && !isAuthenticated) {
        // 로그인이 필요한 페이지인데 비로그인 상태라면 로그인 페이지로 리다이렉트
        next('/login');
    } else if (to.path === '/login' && isAuthenticated) {
        // 로그인 상태에서 로그인 페이지로 접근하려 하면 홈으로 리다이렉트
        next('/');
    } else {
        // 그 외에는 정상적으로 진행
        next();
    }
});

export default router;