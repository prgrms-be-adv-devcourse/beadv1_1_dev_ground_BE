<template>
  <teleport to="body">
    <div v-if="open" class="fixed inset-0 z-[100] flex items-center justify-center bg-black/50">
      <div class="bg-white w-full max-w-5xl h-[600px] rounded-2xl shadow-2xl overflow-hidden flex">
        <!-- Rooms -->
        <div class="w-1/3 border-r border-gray-200 flex flex-col">
          <div class="p-4 border-b border-gray-200 flex items-center gap-2">
            <input
              v-model="userCode"
              class="flex-1 h-10 px-3 rounded-lg border border-gray-200 focus:outline-none focus:ring-2 focus:ring-indigo-200"
              placeholder="X-CODE / 사용자 코드"
            />
            <button
              class="h-10 px-3 rounded-lg bg-indigo-600 text-white text-sm font-semibold hover:bg-indigo-700 transition"
              @click="loadRooms"
            >
              불러오기
            </button>
            <button class="h-10 px-3 rounded-lg text-gray-500 hover:bg-gray-100" @click="$emit('close')">
              ✕
            </button>
          </div>
          <div class="p-4 border-b border-gray-200 flex items-center gap-2">
            <input
              v-model="roomFilter"
              class="w-full h-9 px-3 rounded-lg border border-gray-200 focus:outline-none focus:ring-2 focus:ring-indigo-200 text-sm"
              placeholder="방/상품/참여자 검색"
            />
          </div>
          <div class="flex-1 overflow-auto">
            <div
              v-if="roomsLoading"
              class="px-4 py-6 text-sm text-gray-500 flex items-center justify-center"
            >
              방 목록 불러오는 중...
            </div>
            <div v-else-if="filteredRooms.length === 0" class="px-4 py-6 text-sm text-gray-500 text-center">
              방이 없습니다.
            </div>
            <ul v-else class="divide-y divide-gray-100">
              <li
                v-for="room in filteredRooms"
                :key="room.id"
                @click="selectRoom(room)"
                class="p-4 cursor-pointer hover:bg-indigo-50 transition flex flex-col gap-1"
                :class="selectedRoom?.id === room.id ? 'bg-indigo-50' : ''"
              >
                <div class="flex items-center justify-between gap-2">
                  <div class="text-sm font-semibold text-gray-900">방 ID: {{ room.id }}</div>
                  <span
                    v-if="room.unreadCount > 0"
                    class="text-xs bg-red-500 text-white px-2 py-0.5 rounded-full"
                  >
                    {{ room.unreadCount }}
                  </span>
                </div>
                <div class="text-xs text-gray-500">
                  상품 {{ room.productCode }} · 판매 {{ room.sellerCode }} · 구매 {{ room.buyerCode }}
                </div>
                <div class="text-sm text-gray-700 truncate" v-if="room.lastMessage">
                  {{ room.lastMessage }}
                </div>
                <div class="text-xs text-gray-400" v-if="room.lastMessageAt">
                  {{ formatTime(room.lastMessageAt) }}
                </div>
              </li>
            </ul>
          </div>
        </div>

        <!-- Messages -->
        <div class="flex-1 flex flex-col">
          <div class="p-4 border-b border-gray-200 flex items-center justify-between">
            <div>
              <div class="text-sm font-semibold text-gray-900">
                {{ selectedRoom ? `방: ${selectedRoom.id}` : '방을 선택하세요' }}
              </div>
              <div class="text-xs text-gray-500" v-if="selectedRoom">
                상품 {{ selectedRoom.productCode }} · 판매 {{ selectedRoom.sellerCode }} · 구매
                {{ selectedRoom.buyerCode }}
              </div>
            </div>
            <div class="text-xs text-gray-500">
              <span class="inline-flex items-center gap-1">
                <span class="w-2 h-2 rounded-full" :class="stompConnected ? 'bg-green-500' : 'bg-gray-300'"></span>
                {{ stompConnected ? '실시간 연결됨' : '연결 안 됨' }}
              </span>
            </div>
          </div>

          <div class="flex-1 overflow-auto p-4 space-y-3" ref="messageListRef">
            <div v-if="!selectedRoom" class="text-sm text-gray-500">채팅방을 선택하세요.</div>
            <div v-else-if="messagesLoading" class="text-sm text-gray-500">메시지 불러오는 중...</div>
            <div v-else-if="messages.length === 0" class="text-sm text-gray-500">메시지가 없습니다.</div>
            <div
              v-else
              v-for="(msg, idx) in messages"
              :key="idx"
              class="flex"
              :class="msg.senderCode === userCode ? 'justify-end' : 'justify-start'"
            >
              <div
                class="max-w-[70%] rounded-2xl px-3 py-2 shadow-sm"
                :class="msg.senderCode === userCode ? 'bg-indigo-600 text-white' : 'bg-gray-100 text-gray-900'"
              >
                <div class="text-xs opacity-80">{{ msg.senderCode }}</div>
                <div class="text-sm whitespace-pre-wrap break-words">{{ msg.message }}</div>
                <div class="text-[11px] opacity-70 mt-1">
                  {{ msg.createdAt ? formatTime(msg.createdAt) : '' }}
                </div>
              </div>
            </div>
          </div>

          <div class="p-4 border-t border-gray-200 flex items-center gap-2">
            <input
              v-model="messageInput"
              class="flex-1 h-11 px-3 rounded-lg border border-gray-200 focus:outline-none focus:ring-2 focus:ring-indigo-200"
              placeholder="메시지를 입력하세요"
              @keyup.enter="sendMessage"
              :disabled="!selectedRoom"
            />
            <button
              class="h-11 px-4 rounded-lg bg-indigo-600 text-white font-semibold hover:bg-indigo-700 transition disabled:opacity-50"
              :disabled="!selectedRoom || !messageInput.trim()"
              @click="sendMessage"
            >
              전송
            </button>
          </div>
        </div>
      </div>
    </div>
  </teleport>
</template>

<script setup>
import { ref, computed, watch, onMounted, onBeforeUnmount, nextTick } from 'vue'
import api from '@/api'

const props = defineProps({
  open: { type: Boolean, default: false },
})
const emit = defineEmits(['close'])

const userCode = ref('')
const roomFilter = ref('')
const rooms = ref([])
const roomsLoading = ref(false)
const selectedRoom = ref(null)
const messages = ref([])
const messagesLoading = ref(false)
const messageInput = ref('')

const stompClient = ref(null)
const stompConnected = ref(false)
const messageListRef = ref(null)

const filteredRooms = computed(() => {
  const term = roomFilter.value.trim().toLowerCase()
  if (!term) return rooms.value
  return rooms.value.filter(
    (r) =>
      r.id.toLowerCase().includes(term) ||
      (r.productCode && r.productCode.toLowerCase().includes(term)) ||
      (r.sellerCode && r.sellerCode.toLowerCase().includes(term)) ||
      (r.buyerCode && r.buyerCode.toLowerCase().includes(term))
  )
})

const formatTime = (iso) => {
  try {
    const d = new Date(iso)
    return d.toLocaleString()
  } catch (e) {
    return iso
  }
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messageListRef.value) {
      messageListRef.value.scrollTop = messageListRef.value.scrollHeight
    }
  })
}

const ensureStomp = () =>
  new Promise((resolve, reject) => {
    if (window.Stomp) return resolve(window.Stomp)
    const script = document.createElement('script')
    script.src = 'https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js'
    script.onload = () => resolve(window.Stomp)
    script.onerror = reject
    document.body.appendChild(script)
  })

const disconnectStomp = () => {
  if (stompClient.value) {
    stompClient.value.disconnect(() => {
      stompConnected.value = false
    })
    stompClient.value = null
  }
}

const connectStomp = async (chatId) => {
  try {
    const Stomp = await ensureStomp()
    disconnectStomp()
    const wsProtocol = location.protocol === 'https:' ? 'wss' : 'ws'
    const socket = new WebSocket(`${wsProtocol}://${location.host}/ws-chat`)
    const client = Stomp.over(socket)
    client.debug = () => {}
    client.connect({}, () => {
      stompConnected.value = true
      client.subscribe(`/topic/chat/${chatId}`, (message) => {
        try {
          const payload = JSON.parse(message.body)
          messages.value.push({ ...payload, read: false })
          scrollToBottom()
        } catch (e) {
          console.warn('메시지 파싱 실패', e)
        }
      })
      client.subscribe(`/topic/chat/${chatId}/read`, () => {
        messages.value = messages.value.map((m) => ({ ...m, read: true }))
      })
    })
    stompClient.value = client
  } catch (e) {
    console.error('STOMP 연결 실패', e)
  }
}

const loadRooms = async () => {
  if (!userCode.value.trim()) return
  roomsLoading.value = true
  try {
    const { data } = await api.get('/chat/rooms', {
      headers: { 'X-CODE': userCode.value },
      params: { status: 'OPEN' },
    })
    rooms.value = Array.isArray(data) ? data : data?.data || []
  } catch (e) {
    console.error('방 목록 불러오기 실패', e)
  } finally {
    roomsLoading.value = false
  }
}

const loadMessages = async (chatId) => {
  messagesLoading.value = true
  try {
    const { data } = await api.get(`/chat/rooms/${chatId}/messages`, {
      headers: { 'X-CODE': userCode.value },
    })
    messages.value = Array.isArray(data) ? data : data?.data || []
    scrollToBottom()
  } catch (e) {
    console.error('메시지 불러오기 실패', e)
  } finally {
    messagesLoading.value = false
  }
}

const selectRoom = (room) => {
  selectedRoom.value = room
  messages.value = []
  loadMessages(room.id)
  connectStomp(room.id)
}

const sendMessage = () => {
  if (!selectedRoom.value || !messageInput.value.trim() || !userCode.value.trim() || !stompClient.value) return
  try {
    stompClient.value.send(
      '/app/chat/messages',
      { 'X-CODE': userCode.value },
      JSON.stringify({
        chatId: selectedRoom.value.id,
        senderCode: userCode.value,
        message: messageInput.value.trim(),
      })
    )
    messageInput.value = ''
  } catch (e) {
    console.error('메시지 전송 실패', e)
  }
}

watch(
  () => props.open,
  (val) => {
    if (val) {
      loadRooms()
    } else {
      selectedRoom.value = null
      messages.value = []
      disconnectStomp()
    }
  }
)

onMounted(() => {
  // 기본 X-CODE를 미리 채워놓고 싶다면 여기 설정
})

onBeforeUnmount(() => {
  disconnectStomp()
})
</script>
