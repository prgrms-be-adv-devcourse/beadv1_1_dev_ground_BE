<template>
  <div
    class="min-h-screen bg-gray-50 flex items-center justify-center px-4 sm:px-6 lg:px-8 py-8 sm:py-12"
  >
    <div class="w-full max-w-2xl">
      <!-- 회원가입 폼 -->
      <div class="bg-white rounded-2xl shadow-lg p-6 sm:p-10">
        <!-- 타이틀 -->
        <div class="text-center mb-8">
          <h2 class="text-3xl font-bold text-gray-900 mb-2">회원가입</h2>
          <p class="text-sm text-gray-600">dbay와 함께 안전한 중고거래를 시작하세요</p>
        </div>

        <form @submit.prevent="handleSignup" class="space-y-6">
          <!-- 프로필 이미지 -->
          <div class="flex flex-col items-center">
            <div class="relative">
              <div
                class="w-24 h-24 rounded-full bg-gray-100 border-2 border-gray-200 flex items-center justify-center overflow-hidden"
              >
                <img
                  v-if="profilePreview"
                  :src="profilePreview"
                  alt="프로필 이미지"
                  class="w-full h-full object-cover"
                />
                <span v-else class="text-4xl text-gray-400">👤</span>
              </div>
              <label
                for="profileImage"
                class="absolute bottom-0 right-0 w-8 h-8 bg-indigo-600 hover:bg-indigo-700 rounded-full flex items-center justify-center cursor-pointer transition-colors shadow-md"
              >
                <span class="text-white text-sm">📷</span>
                <input
                  id="profileImage"
                  type="file"
                  accept="image/*"
                  @change="handleImageChange"
                  class="hidden"
                />
              </label>
            </div>
            <p class="mt-2 text-xs text-gray-500">프로필 사진 (선택)</p>
          </div>

          <!-- 2열 레이아웃 -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
            <!-- 이름 -->
            <div>
              <label for="name" class="block text-sm font-semibold text-gray-700 mb-2">
                이름 <span class="text-red-500">*</span>
              </label>
              <input
                id="name"
                v-model="signupForm.name"
                type="text"
                required
                placeholder="홍길동"
                class="w-full px-4 py-3.5 border-2 border-gray-200 rounded-xl focus:outline-none focus:border-indigo-600 focus:ring-4 focus:ring-indigo-100 transition-all text-sm"
                :class="{ 'border-red-500': errors.name }"
              />
              <p v-if="errors.name" class="mt-2 text-xs text-red-600">{{ errors.name }}</p>
            </div>

            <!-- 닉네임 -->
            <div>
              <label for="nickname" class="block text-sm font-semibold text-gray-700 mb-2">
                닉네임 <span class="text-red-500">*</span>
              </label>
              <div class="relative">
                <input
                  id="nickname"
                  v-model="signupForm.nickname"
                  type="text"
                  required
                  placeholder="닉네임"
                  class="w-full px-4 py-3.5 border-2 border-gray-200 rounded-xl focus:outline-none focus:border-indigo-600 focus:ring-4 focus:ring-indigo-100 transition-all text-sm"
                  :class="{ 'border-red-500': errors.nickname }"
                />
              </div>
              <p v-if="errors.nickname" class="mt-2 text-xs text-red-600">{{ errors.nickname }}</p>
            </div>
          </div>

          <!-- 이메일 -->
          <div>
            <label for="email" class="block text-sm font-semibold text-gray-700 mb-2">
              이메일 <span class="text-red-500">*</span>
            </label>
            <input
              id="email"
              v-model="signupForm.email"
              type="email"
              required
              placeholder="example@email.com"
              class="w-full px-4 py-3.5 border-2 border-gray-200 rounded-xl focus:outline-none focus:border-indigo-600 focus:ring-4 focus:ring-indigo-100 transition-all text-sm"
              :class="{ 'border-red-500': errors.email }"
            />
            <p v-if="errors.email" class="mt-2 text-xs text-red-600">{{ errors.email }}</p>
          </div>

          <!-- 비밀번호 -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
            <div>
              <label for="password" class="block text-sm font-semibold text-gray-700 mb-2">
                비밀번호 <span class="text-red-500">*</span>
              </label>
              <div class="relative">
                <input
                  id="password"
                  v-model="signupForm.password"
                  :type="showPassword ? 'text' : 'password'"
                  required
                  placeholder="최소 8자 이상"
                  class="w-full px-4 py-3.5 border-2 border-gray-200 rounded-xl focus:outline-none focus:border-indigo-600 focus:ring-4 focus:ring-indigo-100 transition-all text-sm pr-12"
                  :class="{ 'border-red-500': errors.password }"
                />
                <button
                  type="button"
                  @click="showPassword = !showPassword"
                  class="absolute right-4 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 transition-colors"
                >
                  <span v-if="showPassword" class="text-lg">🙈</span>
                  <span v-else class="text-lg">👁️</span>
                </button>
              </div>
              <p v-if="errors.password" class="mt-2 text-xs text-red-600">{{ errors.password }}</p>
            </div>

            <div>
              <label for="passwordConfirm" class="block text-sm font-semibold text-gray-700 mb-2">
                비밀번호 확인 <span class="text-red-500">*</span>
              </label>
              <div class="relative">
                <input
                  id="passwordConfirm"
                  v-model="signupForm.passwordConfirm"
                  :type="showPasswordConfirm ? 'text' : 'password'"
                  required
                  placeholder="비밀번호 재입력"
                  class="w-full px-4 py-3.5 border-2 border-gray-200 rounded-xl focus:outline-none focus:border-indigo-600 focus:ring-4 focus:ring-indigo-100 transition-all text-sm pr-12"
                  :class="{ 'border-red-500': errors.passwordConfirm }"
                />
                <button
                  type="button"
                  @click="showPasswordConfirm = !showPasswordConfirm"
                  class="absolute right-4 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 transition-colors"
                >
                  <span v-if="showPasswordConfirm" class="text-lg">🙈</span>
                  <span v-else class="text-lg">👁️</span>
                </button>
              </div>
              <p v-if="errors.passwordConfirm" class="mt-2 text-xs text-red-600">
                {{ errors.passwordConfirm }}
              </p>
            </div>
          </div>

          <!-- 연락처 -->
          <div>
            <label for="phone" class="block text-sm font-semibold text-gray-700 mb-2">
              연락처 <span class="text-red-500">*</span>
            </label>
            <input
              id="phone"
              v-model="signupForm.phone"
              type="tel"
              required
              placeholder="010-1234-5678"
              class="w-full px-4 py-3.5 border-2 border-gray-200 rounded-xl focus:outline-none focus:border-indigo-600 focus:ring-4 focus:ring-indigo-100 transition-all text-sm"
              :class="{ 'border-red-500': errors.phone }"
            />
            <p v-if="errors.phone" class="mt-2 text-xs text-red-600">{{ errors.phone }}</p>
          </div>

          <!-- 주소 -->
          <div>
            <label for="address" class="block text-sm font-semibold text-gray-700 mb-2">
              주소 <span class="text-red-500">*</span>
            </label>
            <div class="flex gap-2">
              <input
                id="address"
                v-model="signupForm.address"
                type="text"
                required
                placeholder="기본 주소"
                readonly
                class="flex-1 px-4 py-3.5 border-2 border-gray-200 rounded-xl focus:outline-none focus:border-indigo-600 focus:ring-4 focus:ring-indigo-100 transition-all text-sm bg-gray-50"
                :class="{ 'border-red-500': errors.address }"
              />
              <button
                type="button"
                @click="searchAddress"
                class="px-5 py-3.5 bg-gray-700 hover:bg-gray-800 text-white font-medium rounded-xl transition-colors whitespace-nowrap text-sm"
              >
                주소 검색
              </button>
            </div>
            <p v-if="errors.address" class="mt-2 text-xs text-red-600">{{ errors.address }}</p>
          </div>

          <!-- 상세주소 -->
          <div>
            <label for="addressDetail" class="block text-sm font-semibold text-gray-700 mb-2">
              상세주소
            </label>
            <input
              id="addressDetail"
              v-model="signupForm.addressDetail"
              type="text"
              placeholder="상세 주소 입력 (선택)"
              class="w-full px-4 py-3.5 border-2 border-gray-200 rounded-xl focus:outline-none focus:border-indigo-600 focus:ring-4 focus:ring-indigo-100 transition-all text-sm"
            />
          </div>

          <!-- 약관 동의 -->
          <div class="space-y-3 pt-2">
            <label class="flex items-start gap-3 cursor-pointer">
              <input
                v-model="signupForm.agreeAll"
                @change="toggleAgreeAll"
                type="checkbox"
                class="w-5 h-5 text-indigo-600 border-gray-300 rounded focus:ring-2 focus:ring-indigo-500 mt-0.5"
              />
              <span class="text-sm font-semibold text-gray-900">전체 동의</span>
            </label>

            <div class="pl-8 space-y-2">
              <label class="flex items-start gap-3 cursor-pointer">
                <input
                  v-model="signupForm.agreeTerms"
                  type="checkbox"
                  class="w-4 h-4 text-indigo-600 border-gray-300 rounded focus:ring-2 focus:ring-indigo-500 mt-0.5"
                />
                <span class="text-sm text-gray-700">
                  <span class="text-red-500">*</span> 이용약관 동의
                  <a href="#" class="text-indigo-600 hover:underline ml-1">(보기)</a>
                </span>
              </label>

              <label class="flex items-start gap-3 cursor-pointer">
                <input
                  v-model="signupForm.agreePrivacy"
                  type="checkbox"
                  class="w-4 h-4 text-indigo-600 border-gray-300 rounded focus:ring-2 focus:ring-indigo-500 mt-0.5"
                />
                <span class="text-sm text-gray-700">
                  <span class="text-red-500">*</span> 개인정보처리방침 동의
                  <a href="#" class="text-indigo-600 hover:underline ml-1">(보기)</a>
                </span>
              </label>

              <label class="flex items-start gap-3 cursor-pointer">
                <input
                  v-model="signupForm.agreeMarketing"
                  type="checkbox"
                  class="w-4 h-4 text-indigo-600 border-gray-300 rounded focus:ring-2 focus:ring-indigo-500 mt-0.5"
                />
                <span class="text-sm text-gray-700"> 마케팅 정보 수신 동의 (선택) </span>
              </label>
            </div>
            <p v-if="errors.terms" class="text-xs text-red-600 pl-8">{{ errors.terms }}</p>
          </div>

          <!-- 회원가입 버튼 -->
          <button
            type="submit"
            :disabled="isLoading"
            class="w-full bg-indigo-600 hover:bg-indigo-700 disabled:bg-gray-400 disabled:cursor-not-allowed text-white font-semibold py-3.5 rounded-xl transition-all shadow-sm hover:shadow-md flex items-center justify-center gap-2 mt-8"
          >
            <span v-if="isLoading">
              <svg class="animate-spin h-5 w-5" viewBox="0 0 24 24">
                <circle
                  class="opacity-25"
                  cx="12"
                  cy="12"
                  r="10"
                  stroke="currentColor"
                  stroke-width="4"
                  fill="none"
                ></circle>
                <path
                  class="opacity-75"
                  fill="currentColor"
                  d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                ></path>
              </svg>
            </span>
            <span>{{ isLoading ? '가입 중...' : '회원가입' }}</span>
          </button>
        </form>

        <!-- 로그인 링크 -->
        <p class="mt-8 text-center text-sm text-gray-600">
          이미 계정이 있으신가요?
          <router-link
            to="/login"
            class="text-indigo-600 hover:text-indigo-700 font-semibold ml-1 transition-colors"
          >
            로그인
          </router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
// import { useRouter } from 'vue-router'

// const router = useRouter()

// 상태 관리
const isLoading = ref(false)
const showPassword = ref(false)
const showPasswordConfirm = ref(false)
const profilePreview = ref(null)
const profileImage = ref(null)

const signupForm = reactive({
  name: '',
  nickname: '',
  email: '',
  password: '',
  passwordConfirm: '',
  phone: '',
  address: '',
  addressDetail: '',
  agreeAll: false,
  agreeTerms: false,
  agreePrivacy: false,
  agreeMarketing: false,
})

const errors = reactive({
  name: '',
  nickname: '',
  email: '',
  password: '',
  passwordConfirm: '',
  phone: '',
  address: '',
  terms: '',
})

// 전체 동의 토글
const toggleAgreeAll = () => {
  const checked = signupForm.agreeAll
  signupForm.agreeTerms = checked
  signupForm.agreePrivacy = checked
  signupForm.agreeMarketing = checked
}

// 개별 동의 상태 감시
watch(
  () => [signupForm.agreeTerms, signupForm.agreePrivacy, signupForm.agreeMarketing],
  ([terms, privacy, marketing]) => {
    signupForm.agreeAll = terms && privacy && marketing
  },
)

// 프로필 이미지 변경
const handleImageChange = (e) => {
  const file = e.target.files[0]
  if (file) {
    profileImage.value = file
    const reader = new FileReader()
    reader.onload = (e) => {
      profilePreview.value = e.target.result
    }
    reader.readAsDataURL(file)
  }
}

// 주소 검색 (Daum 우편번호 API)
const searchAddress = () => {
  // Daum 우편번호 API 사용
  new window.daum.Postcode({
    oncomplete: function (data) {
      signupForm.address = data.address
    },
  }).open()

  // 또는 백엔드 API 호출
  // const response = await fetch('/api/address/search')
}

// 폼 검증
const validateForm = () => {
  // 에러 초기화
  Object.keys(errors).forEach((key) => (errors[key] = ''))

  let isValid = true

  // 이름 검증
  if (!signupForm.name.trim()) {
    errors.name = '이름을 입력해주세요.'
    isValid = false
  }

  // 닉네임 검증
  if (!signupForm.nickname.trim()) {
    errors.nickname = '닉네임을 입력해주세요.'
    isValid = false
  } else if (signupForm.nickname.length < 2) {
    errors.nickname = '닉네임은 최소 2자 이상이어야 합니다.'
    isValid = false
  }

  // 이메일 검증
  if (!signupForm.email) {
    errors.email = '이메일을 입력해주세요.'
    isValid = false
  } else {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    if (!emailRegex.test(signupForm.email)) {
      errors.email = '올바른 이메일 형식이 아닙니다.'
      isValid = false
    }
  }

  // 비밀번호 검증
  if (!signupForm.password) {
    errors.password = '비밀번호를 입력해주세요.'
    isValid = false
  } else if (signupForm.password.length < 8) {
    errors.password = '비밀번호는 최소 8자 이상이어야 합니다.'
    isValid = false
  }

  // 비밀번호 확인 검증
  if (!signupForm.passwordConfirm) {
    errors.passwordConfirm = '비밀번호를 다시 입력해주세요.'
    isValid = false
  } else if (signupForm.password !== signupForm.passwordConfirm) {
    errors.passwordConfirm = '비밀번호가 일치하지 않습니다.'
    isValid = false
  }

  // 연락처 검증
  if (!signupForm.phone) {
    errors.phone = '연락처를 입력해주세요.'
    isValid = false
  } else {
    const phoneRegex = /^01[0-9]-?[0-9]{3,4}-?[0-9]{4}$/
    if (!phoneRegex.test(signupForm.phone.replace(/-/g, ''))) {
      errors.phone = '올바른 연락처 형식이 아닙니다.'
      isValid = false
    }
  }

  // 주소 검증
  if (!signupForm.address) {
    errors.address = '주소를 입력해주세요.'
    isValid = false
  }

  // 필수 약관 동의 검증
  if (!signupForm.agreeTerms || !signupForm.agreePrivacy) {
    errors.terms = '필수 약관에 동의해주세요.'
    isValid = false
  }

  return isValid
}

// 회원가입
const handleSignup = async () => {
  if (!validateForm()) return

  isLoading.value = true

  try {
    // FormData 생성 (프로필 이미지 포함)
    const formData = new FormData()
    formData.append('name', signupForm.name)
    formData.append('nickname', signupForm.nickname)
    formData.append('email', signupForm.email)
    formData.append('password', signupForm.password)
    formData.append('phone', signupForm.phone)
    formData.append('address', signupForm.address)
    formData.append('addressDetail', signupForm.addressDetail)
    formData.append('agreeMarketing', signupForm.agreeMarketing)

    if (profileImage.value) {
      formData.append('profileImage', profileImage.value)
    }

    // 실제 API 호출
    const response = await fetch('/api/auth/signup', {
      method: 'POST',
      body: formData,
    })

    if (response.ok) {
      const data = await response.json()
      console.log('회원가입 성공:', data)
      alert('회원가입이 완료되었습니다!')

      // 로그인 페이지로 이동
      // router.push('/login')
    } else {
      const error = await response.json()
      alert(error.message || '회원가입에 실패했습니다.')
    }
  } catch (error) {
    console.error('회원가입 오류:', error)
    alert('회원가입 중 오류가 발생했습니다.')
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
/* 추가 스타일이 필요한 경우 */
</style>
