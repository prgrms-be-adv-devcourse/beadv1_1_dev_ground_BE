<template>
  <header class="bg-white shadow-sm sticky top-0 z-50">
    <!-- 상단 헤더 -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
      <div class="flex items-center gap-4">
        <!-- 로고 -->
        <a href="/" class="flex items-center gap-2 flex-shrink-0">
          <div
            class="w-8 h-8 bg-gradient-to-br from-indigo-600 to-purple-600 rounded-lg flex items-center justify-center"
          >
            <span class="text-white text-lg">⚡</span>
          </div>
          <span class="text-2xl font-bold text-indigo-600 hidden sm:block">dbay</span>
        </a>

        <!-- 네비게이션 (로그인 상태) -->
        <nav v-if="isLoggedIn" class="flex items-center gap-2 ml-auto">
          <div class="hidden sm:flex items-center gap-2">
            <!-- User -->
            <button
              @click="goToMyPage"
              class="group relative inline-flex items-center gap-2 h-10 px-3 rounded-xl hover:bg-gray-100 active:bg-gray-200 transition-colors focus:outline-none focus:ring-2 focus:ring-gray-300"
              aria-label="마이페이지"
            >
              <span class="text-lg leading-none">👤</span>
              <span class="leading-tight">
                <div class="text-sm font-semibold text-gray-800">
                  {{ userName }}테스트<span class="text-gray-400 font-medium">님</span>
                </div>
              </span>

              <!-- tooltip -->
              <span
                class="pointer-events-none absolute left-1/2 top-full z-50 mt-1 -translate-x-1/2 opacity-0 translate-y-1 transition group-hover:opacity-100 group-hover:translate-y-0 group-focus-visible:opacity-100 group-focus-visible:translate-y-0"
              >
                <span
                  class="relative whitespace-nowrap rounded-md bg-gray-900 px-2 py-1 text-[11px] font-semibold text-white shadow"
                >
                  마이페이지
                </span>
              </span>
            </button>

            <!-- divider -->
            <div class="mx-1 h-6 w-px bg-gray-200"></div>

            <div
              class="inline-flex items-center gap-2 h-10 px-3 rounded-xl text-gray-800 focus:outline-none focus:ring-2 focus:ring-gray-300"
              aria-label="예치금"
            >
              <span class="text-lg leading-none">💸</span>
              <span class="text-sm font-semibold">1,000 원 </span>
            </div>

            <!-- divider -->
            <div class="mx-1 h-6 w-px bg-gray-200"></div>

            <!-- Cart -->
            <button
              @click="goToCart"
              class="group relative inline-flex items-center justify-center h-10 w-10 rounded-xl hover:bg-gray-100 active:bg-gray-200 transition-colors focus:outline-none focus:ring-2 focus:ring-gray-300"
              aria-label="장바구니"
            >
              <span class="text-lg leading-none">🛒</span>
              <span
                v-if="cartCount > 0"
                class="absolute -top-1 -right-1 bg-red-500 text-white text-[10px] min-w-[18px] h-[18px] px-1 rounded-full flex items-center justify-center font-bold leading-none ring-2 ring-white"
              >
                {{ cartCount }}
              </span>
              <span
                class="pointer-events-none absolute left-1/2 top-full z-50 mt-1 -translate-x-1/2 opacity-0 translate-y-1 transition group-hover:opacity-100 group-hover:translate-y-0 group-focus-visible:opacity-100 group-focus-visible:translate-y-0"
              >
                <span
                  class="relative whitespace-nowrap rounded-md bg-gray-900 px-2 py-1 text-[11px] font-semibold text-white shadow"
                >
                  장바구니
                </span>
              </span>
            </button>

            <!-- divider -->
            <div class="mx-1 h-6 w-px bg-gray-200"></div>

            <!-- Chat -->
            <button
              @click="showChatModal = true"
              class="group relative inline-flex items-center justify-center h-10 w-10 rounded-xl hover:bg-gray-100 active:bg-gray-200 transition-colors focus:outline-none focus:ring-2 focus:ring-gray-300"
              aria-label="채팅"
            >
              <span class="text-lg leading-none">💬</span>
              <span
                v-if="unreadChatCount > 0"
                class="absolute -top-1 -right-1 bg-red-500 text-white text-[10px] min-w-[18px] h-[18px] px-1 rounded-full flex items-center justify-center font-bold leading-none ring-2 ring-white"
              >
                {{ unreadChatCount }}
              </span>
              <span
                class="pointer-events-none absolute left-1/2 top-full z-50 mt-1 -translate-x-1/2 opacity-0 translate-y-1 transition group-hover:opacity-100 group-hover:translate-y-0 group-focus-visible:opacity-100 group-focus-visible:translate-y-0"
              >
                <span
                  class="relative whitespace-nowrap rounded-md bg-gray-900 px-2 py-1 text-[11px] font-semibold text-white shadow"
                >
                  채팅
                </span>
              </span>
            </button>

            <!-- divider -->
            <div class="mx-1 h-6 w-px bg-gray-200"></div>

            <!-- Logout -->
            <button
              @click="handleLogout"
              class="group relative inline-flex items-center justify-center h-10 w-10 rounded-xl bg-white text-red-600 hover:bg-red-50 active:bg-red-100 transition-colors focus:outline-none focus:ring-2 focus:ring-red-200"
              aria-label="로그아웃"
            >
              <span class="text-lg leading-none">🚪</span>

              <!-- tooltip -->
              <span
                class="pointer-events-none absolute left-1/2 top-full z-50 mt-1 -translate-x-1/2 opacity-0 translate-y-1 transition group-hover:opacity-100 group-hover:translate-y-0 group-focus-visible:opacity-100 group-focus-visible:translate-y-0"
              >
                <span
                  class="relative whitespace-nowrap rounded-md bg-gray-900 px-2 py-1 text-[11px] font-semibold text-white shadow"
                >
                  로그아웃
                </span>
              </span>
            </button>
          </div>
        </nav>

        <!-- 네비게이션 (비로그인 상태) -->
        <nav v-else class="flex items-center gap-2 ml-auto">
          <button
            @click="goToLogin"
            class="bg-indigo-600 hover:bg-indigo-700 text-white px-6 py-2 rounded-lg transition-colors flex items-center gap-2"
          >
            <span class="text-sm font-medium">로그인</span>
          </button>
        </nav>
      </div>
    </div>
  </header>
  <ChatModal :open="showChatModal" @close="showChatModal = false" />
</template>

<script setup>
import router from '@/router'
import { ref, onMounted } from 'vue'
import ChatModal from '@/components/ChatModal.vue'

// 상태 관리
const showMobileMenu = ref(false)
const showChatModal = ref(false)

// 인증 관련 상태
const accessToken = ref(null)
const refreshToken = ref(null)
const userName = ref('')
const cartCount = ref(3)
const unreadChatCount = ref(3)

const isLoggedIn = ref(true)

// 로그인 여부 계산
// const isLoggedIn = computed(() => {
//   // return !!accessToken.value
//   return true
// })

// 메서드
const goToCart = () => {
  console.log('장바구니로 이동')
  showMobileMenu.value = false
  // router.push('/cart')
}

const goToMyPage = () => {
  console.log('마이페이지로 이동')
  showMobileMenu.value = false
  // router.push('/mypage')
}

const goToSell = () => {
  console.log('판매하기 페이지로 이동')
  // router.push('/sell')
}

const goToLogin = () => {
  console.log('로그인 페이지로 이동')
  router.push('/login')
}

const handleLogout = () => {
  if (confirm('로그아웃 하시겠습니까?')) {
    clearTokens()
    userName.value = ''
    cartCount.value = 0
    unreadChatCount.value = 0
    showMobileMenu.value = false
    console.log('로그아웃 완료')
    // router.push('/')
    isLoggedIn.value = false
  }
}

// 컴포넌트 마운트시 토큰 로드 및 유저 정보 가져오기
onMounted(() => {
  loadTokens()
  if (isLoggedIn.value) {
    fetchUserInfo()
  }
})

// 외부에서 사용할 수 있도록 로그인 함수 expose
// defineExpose({
//   saveTokens,
//   loadTokens,
//   clearTokens,
// })
</script>

<style scoped>
/* 스크롤바 숨김 */
.scrollbar-hide::-webkit-scrollbar {
  display: none;
}

.scrollbar-hide {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
</style>
