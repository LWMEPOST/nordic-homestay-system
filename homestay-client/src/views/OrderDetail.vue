<template>
  <div class="order-detail-page" v-if="order">
    <van-nav-bar
      title="订单详情"
      left-arrow
      fixed
      placeholder
      @click-left="$router.back()"
    />

    <div class="content">
      <!-- Status Card -->
      <div class="glass-panel status-card">
        <div class="status-header">
          <van-icon :name="getStatusIcon(order.status)" size="24" :color="getStatusColor(order.status)" />
          <span class="status-text">{{ getStatusText(order.status) }}</span>
        </div>
        <p class="status-desc">{{ getStatusDesc(order.status) }}</p>
      </div>

      <!-- Room Info -->
      <div class="glass-panel room-card" @click="$router.push(`/rooms/${order.roomId}`)">
        <van-image
          width="100"
          height="80"
          radius="8"
          fit="cover"
          :src="order.roomImage || 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'"
          class="room-img"
        />
        <div class="room-info">
          <h3 class="room-name">{{ order.roomName }}</h3>
          <p class="room-address">房源ID: {{ order.roomId }}</p>
        </div>
        <van-icon name="arrow" color="#999" />
      </div>

      <!-- Booking Info -->
      <div class="glass-panel info-card">
        <h3 class="card-title">预订信息</h3>
        <div class="info-row">
          <span class="label">订单编号</span>
          <span class="value">{{ order.orderNo }}</span>
        </div>
        <div class="info-row">
          <span class="label">下单时间</span>
          <span class="value">{{ formatTime(order.createTime) }}</span>
        </div>
        <div class="info-row">
          <span class="label">入住日期</span>
          <span class="value">{{ formatDate(order.checkInDate) }}</span>
        </div>
        <div class="info-row">
          <span class="label">退房日期</span>
          <span class="value">{{ formatDate(order.checkOutDate) }}</span>
        </div>
        <div class="info-row">
          <span class="label">入住人</span>
          <span class="value">{{ order.guestName }}</span>
        </div>
        <div class="info-row">
          <span class="label">联系电话</span>
          <span class="value">{{ order.guestPhone }}</span>
        </div>
        <div class="info-row">
          <span class="label">入住人数</span>
          <span class="value">{{ order.guestCount }}人</span>
        </div>
        <div class="info-row">
          <span class="label">总金额</span>
          <span class="value price">¥{{ order.totalAmount }}</span>
        </div>
        <div class="info-row" v-if="order.remark">
          <span class="label">备注</span>
          <span class="value">{{ order.remark }}</span>
        </div>
      </div>

      <!-- Actions -->
      <div class="action-bar">
        <van-button 
          v-if="order.status === 0" 
          block 
          round 
          plain 
          type="default" 
          @click="cancelOrder"
          class="action-btn"
        >
          取消订单
        </van-button>
        <van-button 
          v-if="order.status === 0" 
          block 
          round 
          color="var(--wood-500)" 
          @click="payOrder"
          class="action-btn"
        >
          立即支付
        </van-button>
        <van-button 
          v-if="order.status === 1" 
          block 
          round 
          plain 
          type="default" 
          @click="cancelOrder"
          class="action-btn"
        >
          申请退款
        </van-button>
        <van-button 
           v-if="order.status === 3"
           block
           round
           type="primary"
           color="var(--wood-500)"
           @click="$router.push(`/rooms/${order.roomId}`)"
           class="action-btn"
        >
           再次预订
        </van-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '../api/axios'
import { showToast, showDialog } from 'vant'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()
const order = ref<any>(null)

const getStatusText = (status: number) => {
  const map: Record<number, string> = {
    0: '待支付',
    1: '待入住',
    2: '已入住',
    3: '已完成',
    4: '已取消'
  }
  return map[status] || '未知'
}

const getStatusDesc = (status: number) => {
  const map: Record<number, string> = {
    0: '请尽快完成支付，超时将自动取消',
    1: '支付成功，请按时入住',
    2: '祝您入住愉快',
    3: '期待您的再次光临',
    4: '订单已取消'
  }
  return map[status] || ''
}

const getStatusColor = (status: number) => {
  const map: Record<number, string> = {
    0: 'var(--warning)',
    1: 'var(--success)',
    2: 'var(--primary)',
    3: 'var(--slate-500)',
    4: 'var(--slate-300)'
  }
  return map[status] || 'var(--slate-500)'
}

const getStatusIcon = (status: number) => {
  const map: Record<number, string> = {
    0: 'clock-o',
    1: 'passed',
    2: 'hotel-o',
    3: 'completed',
    4: 'close'
  }
  return map[status] || 'info-o'
}

const formatDate = (date: string) => dayjs(date).format('YYYY年MM月DD日')
const formatTime = (time: string) => dayjs(time).format('YYYY-MM-DD HH:mm:ss')

const fetchOrder = async () => {
  try {
    // Since we don't have a single order API for users yet, we might need to fetch all and find one,
    // OR ideally implement GET /api/orders/{id} for user.
    // For now, let's assume GET /api/orders/my returns list and we find it, OR implement detail API.
    // Actually, Admin has GET /api/orders/{id}, maybe we can reuse or allow user access if owner.
    // Let's check backend controller. OrderController has no GET /{id} for user, only Admin list and User list.
    // I will add GET /{id} to OrderController for user access.
    
    // For now, let's try to filter from list (not efficient but works for MVP)
    const res = await api.get('/orders/my')
    const found = res.data.find((o: any) => o.id === Number(route.params.id))
    if (found) {
      order.value = found
    } else {
      showToast('订单不存在')
      router.back()
    }
  } catch (error) {
    showToast('获取详情失败')
  }
}

const payOrder = async () => {
  showToast({ message: '支付处理中...', type: 'loading' })
  try {
    await api.post(`/orders/${order.value.id}/pay`)
    showToast('支付成功')
    fetchOrder()
  } catch (error: any) {
    showToast(error.response?.data?.message || '支付失败')
  }
}

const cancelOrder = () => {
  showDialog({
    title: '提示',
    message: '确定要取消/退款该订单吗？',
    showCancelButton: true,
  }).then(async (action) => {
    if (action === 'confirm') {
      try {
        await api.post(`/orders/${order.value.id}/cancel`)
        showToast('取消成功')
        fetchOrder()
      } catch (error: any) {
        showToast(error.response?.data?.message || '取消失败')
      }
    }
  })
}

onMounted(() => {
  fetchOrder()
})
</script>

<style scoped>
.order-detail-page {
  min-height: 100vh;
  background-color: var(--bg-100);
  padding-bottom: 30px;
}

.content {
  padding: 16px;
}

.glass-panel {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
}

.status-card {
  text-align: center;
  padding: 24px;
}

.status-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 8px;
}

.status-text {
  font-size: 20px;
  font-weight: 600;
  color: var(--slate-900);
}

.status-desc {
  font-size: 14px;
  color: var(--slate-500);
}

.room-card {
  display: flex;
  align-items: center;
}

.room-img {
  flex-shrink: 0;
  margin-right: 12px;
  background: var(--bg-200);
}

.room-info {
  flex: 1;
}

.room-name {
  margin: 0 0 4px;
  font-size: 16px;
  font-weight: 600;
}

.room-address {
  font-size: 13px;
  color: var(--slate-500);
  margin: 0;
}

.card-title {
  margin: 0 0 16px;
  font-size: 16px;
  color: var(--wood-700);
  border-left: 4px solid var(--wood-500);
  padding-left: 8px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  font-size: 14px;
}

.info-row:last-child {
  margin-bottom: 0;
}

.label {
  color: var(--slate-500);
}

.value {
  color: var(--slate-900);
}

.price {
  font-size: 18px;
  font-weight: 600;
  color: var(--wood-500);
}

.action-bar {
  margin-top: 24px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
</style>
