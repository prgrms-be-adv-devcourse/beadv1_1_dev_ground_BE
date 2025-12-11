<template>
  <footer class="bg-gray-900 text-gray-300">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
        <!-- 회사 정보 -->
        <div>
          <div class="flex items-center gap-2 mb-4">
            <div
              class="w-8 h-8 bg-gradient-to-br from-indigo-600 to-purple-600 rounded-lg flex items-center justify-center"
            >
              <span class="text-white text-lg">⚡</span>
            </div>
            <span class="text-xl font-bold text-white">dbay</span>
          </div>
          <p class="text-sm text-gray-400 mb-4">믿을 수 있는 전자기기 중고거래 플랫폼</p>
          <div class="flex gap-3">
            <a
              href="#"
              class="w-9 h-9 bg-gray-800 hover:bg-gray-700 rounded-lg flex items-center justify-center transition-colors"
            >
              <span class="text-lg">📘</span>
            </a>
            <a
              href="#"
              class="w-9 h-9 bg-gray-800 hover:bg-gray-700 rounded-lg flex items-center justify-center transition-colors"
            >
              <span class="text-lg">📷</span>
            </a>
            <a
              href="#"
              class="w-9 h-9 bg-gray-800 hover:bg-gray-700 rounded-lg flex items-center justify-center transition-colors"
            >
              <span class="text-lg">🐦</span>
            </a>
          </div>
        </div>

        <!-- 고객지원 -->
        <div>
          <h3 class="text-white font-semibold mb-4">고객지원</h3>
          <ul class="space-y-3">
            <li>
              <a href="#" class="text-sm hover:text-white transition-colors">공지사항</a>
            </li>
            <li>
              <a href="#" class="text-sm hover:text-white transition-colors">자주 묻는 질문</a>
            </li>
            <li>
              <a href="#" class="text-sm hover:text-white transition-colors">1:1 문의</a>
            </li>
            <li>
              <a href="#" class="text-sm hover:text-white transition-colors">신고하기</a>
            </li>
          </ul>
        </div>

        <!-- 이용안내 -->
        <div>
          <h3 class="text-white font-semibold mb-4">이용안내</h3>
          <ul class="space-y-3">
            <li>
              <a href="#" class="text-sm hover:text-white transition-colors">이용약관</a>
            </li>
            <li>
              <a href="#" class="text-sm hover:text-white transition-colors">개인정보처리방침</a>
            </li>
            <li>
              <a href="#" class="text-sm hover:text-white transition-colors"
                >위치기반서비스 이용약관</a
              >
            </li>
            <li>
              <a href="#" class="text-sm hover:text-white transition-colors">청소년 보호정책</a>
            </li>
          </ul>
        </div>

        <!-- 사업자 정보 -->
        <div>
          <h3 class="text-white font-semibold mb-4">사업자 정보</h3>
          <ul class="space-y-2 text-sm">
            <li class="text-gray-400">대표: 홍길동</li>
            <li class="text-gray-400">사업자등록번호: 123-45-67890</li>
            <li class="text-gray-400">통신판매업신고: 제2024-서울강남-12345호</li>
            <li class="text-gray-400">주소: 서울특별시 강남구 테헤란로 123</li>
            <li class="text-gray-400">이메일: support@dbay.com</li>
            <li class="text-gray-400">고객센터: 1588-0000</li>
          </ul>
        </div>
      </div>

      <!-- 하단 정보 -->
      <div class="border-t border-gray-800 mt-8 pt-8">
        <div class="flex flex-col md:flex-row justify-between items-center gap-4">
          <p class="text-sm text-gray-500">© 2025 dbay. All rights reserved.</p>
          <div class="flex gap-6 text-sm">
            <a href="#" class="text-gray-500 hover:text-white transition-colors"> 광고문의 </a>
            <a href="#" class="text-gray-500 hover:text-white transition-colors"> 제휴문의 </a>
          </div>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup>
// Footer는 정적 컴포넌트라 특별한 로직이 필요 없음
</script>

<style scoped>
/* 필요시 추가 스타일 */
</style>
