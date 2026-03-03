<template>
  <div class="booking-page" v-if="room">
    <van-nav-bar title="确认订单" left-arrow @click-left="$router.back()" fixed placeholder />
    
    <div class="content">
      <!-- 房源信息 -->
      <div class="glass-panel room-card">
        <van-image :src="room.images[0]" width="100" height="80" radius="8" fit="cover" />
        <div class="room-info">
          <div class="room-title">{{ room.name }}</div>
          <div class="room-dates">{{ formatDate(startDate) }} - {{ formatDate(endDate) }}</div>
          <div class="room-price">
            <span class="nights">共 {{ nights }} 晚</span>
            <span class="total">¥{{ totalPrice }}</span>
          </div>
        </div>
      </div>

      <!-- 入住人信息 -->
      <div class="glass-panel form-card">
        <h3 class="card-title">入住信息</h3>
        <van-form @submit="onSubmit">
          <van-field
            v-model="guestName"
            name="guestName"
            label="入住人"
            placeholder="请填写入住人姓名"
            :rules="[{ required: true, message: '请填写入住人姓名' }]"
          />
          <van-field
            v-model="guestPhone"
            name="guestPhone"
            label="手机号"
            placeholder="请填写联系手机"
            :rules="[{ required: true, message: '请填写手机号' }]"
          />
          <van-field
            v-model="guestCount"
            name="guestCount"
            label="入住人数"
            type="digit"
            :rules="[{ required: true, message: '请填写人数' }]"
          >
            <template #button>
              <van-stepper v-model="guestCount" min="1" :max="room.capacity" />
            </template>
          </van-field>
          <van-field
            v-model="remark"
            name="remark"
            label="备注"
            placeholder="如有特殊需求请留言"
            type="textarea"
            rows="2"
            autosize
          />
        </van-form>
      </div>

      <!-- 支付方式 -->
      <div class="glass-panel pay-card">
        <h3 class="card-title">支付方式</h3>
        <van-radio-group v-model="payMethod">
          <van-cell-group inset>
            <van-cell title="微信支付" clickable @click="payMethod = 'wechat'">
              <template #right-icon>
                <van-radio name="wechat" checked-color="#07c160" />
              </template>
              <template #icon>
                <van-icon name="wechat" color="#07c160" size="24" style="margin-right: 8px" />
              </template>
            </van-cell>
            <van-cell title="支付宝" clickable @click="payMethod = 'alipay'">
              <template #right-icon>
                <van-radio name="alipay" checked-color="#1989fa" />
              </template>
              <template #icon>
                <van-icon name="alipay" color="#1989fa" size="24" style="margin-right: 8px" />
              </template>
            </van-cell>
          </van-cell-group>
        </van-radio-group>
      </div>
    </div>

    <!-- 底部提交栏 -->
    <van-submit-bar
      :price="totalPrice * 100"
      button-text="提交订单"
      @submit="onSubmit"
      :loading="loading"
      button-color="var(--wood-500)"
    />

    <!-- 支付模拟弹窗 -->
    <van-popup v-model:show="showPay" round position="bottom" :style="{ height: '40%' }">
      <div class="pay-popup">
        <div class="pay-header">
          <h3>确认支付</h3>
          <van-icon name="cross" @click="showPay = false" />
        </div>
        <div class="pay-amount">¥{{ totalPrice }}</div>
        <div class="pay-desc">订单号: {{ createdOrderId }}</div>
        <van-button block type="primary" color="#07c160" @click="confirmPay" :loading="payLoading">
          立即支付
        </van-button>
      </div>
    </van-popup>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '../api/axios'
import { showSuccessToast, showFailToast } from 'vant'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()

const roomId = route.query.roomId
const startDate = route.query.start as string
const endDate = route.query.end as string

const room = ref<any>(null)
const guestName = ref('')
const guestPhone = ref('')
const guestCount = ref(1)
const remark = ref('')
const payMethod = ref('wechat')
const loading = ref(false)
const showPay = ref(false)
const createdOrderId = ref<number | null>(null)
const payLoading = ref(false)

const nights = computed(() => {
  if (!startDate || !endDate) return 0
  return dayjs(endDate).diff(dayjs(startDate), 'day')
})

const totalPrice = computed(() => {
  if (!room.value) return 0
  return room.value.basePrice * nights.value
})

const formatDate = (date: string) => dayjs(date).format('MM月DD日')

const fetchRoom = async () => {
  try {
    const res = await api.get(`/rooms/${roomId}`)
    room.value = res.data
    guestCount.value = 1
  } catch (error) {
    showFailToast('获取房源信息失败')
  }
}

const onSubmit = async () => {
  if (!guestName.value || !guestPhone.value) {
    showFailToast('请完善入住信息')
    return
  }
  
  loading.value = true
  try {
    const res = await api.post('/orders', {
      roomId: room.value.id,
      checkInDate: startDate,
      checkOutDate: endDate,
      guestCount: Number(guestCount.value),
      guestName: guestName.value,
      guestPhone: guestPhone.value,
      remark: remark.value
    })
    createdOrderId.value = res.data.id
    showPay.value = true
  } catch (error: any) {
    showFailToast(error.response?.data?.message || '下单失败')
  } finally {
    loading.value = false
  }
}

const confirmPay = async () => {
  if (!createdOrderId.value) return
  payLoading.value = true
  try {
    // Simulate payment delay
    await new Promise(resolve => setTimeout(resolve, 1500))
    await api.post(`/orders/${createdOrderId.value}/pay`)
    showSuccessToast('支付成功')
    router.replace('/orders')
  } catch (error: any) {
    showFailToast(error.response?.data?.message || '支付失败')
  } finally {
    payLoading.value = false
    showPay.value = false
  }
}

onMounted(() => {
  if (!roomId || !startDate || !endDate) {
    showFailToast('参数错误')
    router.back()
    return
  }
  fetchRoom()
  
  // Pre-fill user info if available
  const userStr = localStorage.getItem('client_user')
  if (userStr) {
    const user = JSON.parse(userStr)
    // If user object had phone/name we could use it
  }
})
</script>

<style scoped>
.booking-page {
  min-height: 100vh;
  background-color: var(--bg-100);
  padding-bottom: 60px;
}

.content {
  padding: 16px;
}

.glass-panel {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.room-card {
  display: flex;
  align-items: center;
}

.room-info {
  margin-left: 12px;
  flex: 1;
}

.room-title {
  font-weight: 600;
  font-size: 16px;
  margin-bottom: 4px;
}

.room-dates {
  font-size: 13px;
  color: var(--slate-500);
  margin-bottom: 4px;
}

.room-price {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.nights {
  font-size: 12px;
  color: var(--slate-400);
}

.total {
  font-size: 18px;
  font-weight: 600;
  color: var(--wood-500);
}

.card-title {
  margin: 0 0 16px;
  font-size: 16px;
  color: var(--wood-700);
}

.pay-popup {
  padding: 24px;
  text-align: center;
}

.pay-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.pay-amount {
  font-size: 36px;
  font-weight: 600;
  margin-bottom: 8px;
}

.pay-desc {
  font-size: 14px;
  color: var(--slate-500);
  margin-bottom: 32px;
}
</style>
