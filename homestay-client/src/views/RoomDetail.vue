<template>
  <div class="room-detail" v-if="room">
    <!-- 顶部导航 -->
    <div class="nav-bar glass-panel">
      <div class="nav-icon" @click="$router.back()">
        <van-icon name="arrow-left" size="20" />
      </div>
      <div class="nav-actions">
        <div class="nav-icon" @click="toggleFavorite">
          <van-icon :name="isFavorite ? 'like' : 'like-o'" size="20" :color="isFavorite ? '#ff4d4f' : '#333'" />
        </div>
      </div>
    </div>

    <!-- 轮播图 -->
    <van-swipe :autoplay="0" class="gallery">
      <van-swipe-item v-for="(img, index) in room.images" :key="index">
        <van-image :src="img" fit="cover" width="100%" height="300" />
      </van-swipe-item>
      <template #indicator="{ active, total }">
        <div class="custom-indicator">{{ active + 1 }}/{{ total }}</div>
      </template>
    </van-swipe>

    <div class="content-wrapper">
      <!-- 头部信息 -->
      <div class="header-section">
        <div class="title-row">
          <h1 class="title">{{ room.name }}</h1>
        </div>
        <div class="tags-row">
          <span class="feature-tag">{{ room.city }}</span>
          <span class="feature-tag">{{ room.capacity }}人入住</span>
        </div>
      </div>

      <div class="divider"></div>

      <!-- 房东信息 -->
      <div class="host-section">
        <div class="host-avatar">
          <van-image round width="48" height="48" :src="room.hostAvatar || 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'" />
        </div>
        <div class="host-info">
          <div class="host-name">房东: {{ room.hostName || '未知' }}</div>
          <div class="host-desc">{{ room.hostPhone ? `联系电话: ${room.hostPhone}` : '回复率 100% · 1小时内回复' }}</div>
        </div>
        <van-button size="small" round plain type="primary" @click="contactHost">联系房东</van-button>
      </div>

      <div class="divider"></div>

      <!-- 设施 -->
      <div class="section">
        <h3>配套设施</h3>
        <div class="facilities-grid">
          <div v-for="(item, idx) in room.facilities" :key="idx" class="facility-item">
            <van-icon name="success" color="var(--wood-500)" />
            <span>{{ item }}</span>
          </div>
        </div>
      </div>

      <div class="divider"></div>

      <!-- 描述 -->
      <div class="section">
        <h3>关于此房源</h3>
        <p class="desc">{{ room.description }}</p>
      </div>

      <div class="divider"></div>

      <!-- 位置 -->
      <div class="section">
        <h3>位置</h3>
        <p class="address">{{ room.address }}</p>
      </div>
    </div>

    <!-- 底部固定栏 -->
    <div class="bottom-bar glass-panel">
      <template v-if="isHostOwner">
        <van-button 
          color="var(--wood-500)" 
          round 
          block 
          class="book-btn" 
          @click="$router.push(`/host/rooms/edit/${room.id}`)"
        >
          编辑房源
        </van-button>
      </template>
      <template v-else>
        <div class="price-info">
          <div class="price-row">
            <span class="currency">¥</span>
            <span class="amount">{{ room.basePrice }}</span>
            <span class="unit">/晚</span>
          </div>
          <div class="date-preview">请选择日期</div>
        </div>
        <van-button color="var(--wood-500)" round block class="book-btn" @click="showCalendar = true">
          立即预订
        </van-button>
      </template>
    </div>

    <!-- 日期选择 -->
    <van-calendar
      v-model:show="showCalendar"
      type="range"
      color="var(--wood-500)"
      :formatter="formatter"
      @confirm="onConfirmDate"
    />
    
    <!-- 确认订单弹窗 -->
    <van-dialog v-model:show="showConfirm" title="确认订单" show-cancel-button @confirm="createOrder" confirm-button-color="var(--wood-500)">
      <div class="order-confirm">
        <div class="confirm-item">
          <span>入住日期</span>
          <span class="value">{{ dateRange[0] }}</span>
        </div>
        <div class="confirm-item">
          <span>退房日期</span>
          <span class="value">{{ dateRange[1] }}</span>
        </div>
        <div class="confirm-item highlight">
          <span>总价</span>
          <span class="total-price">¥ {{ totalPrice }}</span>
        </div>
        <van-field v-model="guestCount" label="入住人数" type="digit" placeholder="请输入人数" />
        <van-field v-model="remark" label="备注" placeholder="选填" />
      </div>
    </van-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '../api/axios'
import { showSuccessToast, showFailToast } from 'vant'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()
const room = ref<any>(null)
const showCalendar = ref(false)
const showConfirm = ref(false)
const dateRange = ref<string[]>([])
const guestCount = ref(1)
const remark = ref('')

const totalPrice = computed(() => {
  if (!room.value || dateRange.value.length !== 2) return 0
  const days = dayjs(dateRange.value[1]).diff(dayjs(dateRange.value[0]), 'day')
  return room.value.basePrice * days
})

const isFavorite = ref(false)
const user = ref<any>(null)
const isHostOwner = computed(() => {
  return user.value && room.value && user.value.id === room.value.hostId
})

const fetchDetail = async () => {
  try {
    const res = await api.get(`/rooms/${route.params.id}`)
    room.value = res.data
    // Check favorite status
    const favorites = JSON.parse(localStorage.getItem('favorites') || '[]')
    isFavorite.value = favorites.includes(Number(route.params.id))
  } catch (error) {
    console.error(error)
  }
}

const toggleFavorite = () => {
  const favorites = JSON.parse(localStorage.getItem('favorites') || '[]')
  const id = Number(route.params.id)
  if (isFavorite.value) {
    const index = favorites.indexOf(id)
    if (index > -1) favorites.splice(index, 1)
    showSuccessToast('已取消收藏')
  } else {
    favorites.push(id)
    showSuccessToast('收藏成功')
  }
  localStorage.setItem('favorites', JSON.stringify(favorites))
  isFavorite.value = !isFavorite.value
}

const formatter = (day: any) => {
  return day
}

const onConfirmDate = (values: Date[]) => {
  const [start, end] = values
  showCalendar.value = false
  
  // Navigate to booking page
  router.push({
    path: '/booking',
    query: {
      roomId: room.value.id,
      start: dayjs(start).format('YYYY-MM-DD'),
      end: dayjs(end).format('YYYY-MM-DD')
    }
  })
}

const createOrder = async () => {
  try {
    await api.post('/orders', {
      roomId: room.value.id,
      checkInDate: dateRange.value[0],
      checkOutDate: dateRange.value[1],
      guestCount: Number(guestCount.value),
      remark: remark.value
    })
    showSuccessToast('预订成功')
    router.push('/my')
  } catch (error: any) {
    showFailToast(error.response?.data?.message || '预订失败')
  }
}

const contactHost = () => {
  if (room.value?.hostPhone) {
    window.location.href = `tel:${room.value.hostPhone}`
  } else {
    showFailToast('暂无联系方式')
  }
}

onMounted(() => {
  const userStr = localStorage.getItem('client_user')
  if (userStr) {
    user.value = JSON.parse(userStr)
  }
  fetchDetail()
})
</script>

<style scoped>
.room-detail {
  padding-bottom: 90px;
  background-color: var(--bg-100);
  min-height: 100vh;
}

.nav-bar {
  position: fixed;
  top: 16px;
  left: 16px;
  right: 16px;
  z-index: 100;
  display: flex;
  justify-content: space-between;
  padding: 8px 12px;
  border-radius: 30px;
}

.nav-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: rgba(255,255,255,0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.nav-actions {
  display: flex;
  gap: 12px;
}

.gallery {
  position: relative;
}

.custom-indicator {
  position: absolute;
  right: 16px;
  bottom: 16px;
  padding: 4px 12px;
  font-size: 12px;
  background: rgba(0, 0, 0, 0.6);
  color: #fff;
  border-radius: 12px;
}

.content-wrapper {
  background: var(--bg-100);
  border-radius: 24px 24px 0 0;
  margin-top: -24px;
  position: relative;
  z-index: 10;
  padding: 24px 20px;
}

.title {
  font-size: 24px;
  font-weight: 700;
  color: var(--slate-900);
  margin: 0 0 12px;
  line-height: 1.3;
}

.tags-row {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.feature-tag {
  font-size: 12px;
  color: var(--slate-700);
  background: var(--bg-200);
  padding: 4px 10px;
  border-radius: 6px;
}

.rating-row {
  display: flex;
  align-items: center;
  font-size: 14px;
}

.score {
  font-weight: 600;
  margin: 0 4px;
}

.reviews {
  color: var(--slate-500);
}

.divider {
  height: 1px;
  background: var(--bg-300);
  margin: 24px 0;
}

.host-section {
  display: flex;
  align-items: center;
}

.host-avatar {
  margin-right: 12px;
}

.host-info {
  flex: 1;
}

.host-name {
  font-weight: 600;
  color: var(--slate-900);
  margin-bottom: 4px;
}

.host-desc {
  font-size: 12px;
  color: var(--slate-500);
}

.section h3 {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 16px;
  color: var(--slate-900);
}

.facilities-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.facility-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: var(--slate-700);
}

.desc {
  font-size: 15px;
  line-height: 1.6;
  color: var(--slate-700);
}

.address {
  font-size: 15px;
  color: var(--slate-700);
  margin-bottom: 12px;
}

.map-placeholder {
  height: 120px;
  background: var(--bg-200);
  border-radius: var(--radius-md);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--slate-500);
  font-size: 12px;
  gap: 8px;
}

.bottom-bar {
  position: fixed;
  bottom: 16px;
  left: 16px;
  right: 16px;
  padding: 12px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  z-index: 99;
}

.price-info {
  display: flex;
  flex-direction: column;
}

.price-row {
  color: var(--wood-700);
  display: flex;
  align-items: baseline;
}

.amount {
  font-size: 20px;
  font-weight: 700;
}

.unit {
  font-size: 12px;
  color: var(--slate-500);
}

.date-preview {
  font-size: 11px;
  color: var(--slate-500);
}

.book-btn {
  width: 140px;
  height: 44px;
  font-weight: 600;
}

.order-confirm {
  padding: 20px;
}

.confirm-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
  font-size: 14px;
  color: var(--slate-500);
}

.confirm-item .value {
  color: var(--slate-900);
}

.highlight {
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px dashed var(--bg-300);
  align-items: center;
}

.total-price {
  color: var(--wood-700);
  font-weight: 700;
  font-size: 20px;
}
</style>
