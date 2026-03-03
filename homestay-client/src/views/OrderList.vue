<template>
  <div class="order-list-page">
    <van-nav-bar
      title="我的订单"
      left-arrow
      fixed
      placeholder
      @click-left="$router.back()"
      :border="false"
      class="glass-nav"
    />

    <van-tabs v-model:active="activeTab" sticky offset-top="46" background="transparent" color="var(--wood-500)" title-active-color="var(--wood-700)">
      <van-tab title="全部" :name="-1"></van-tab>
      <van-tab title="待支付" :name="0"></van-tab>
      <van-tab title="待入住" :name="1"></van-tab>
      <van-tab title="已入住" :name="2"></van-tab>
      <van-tab title="已完成" :name="3"></van-tab>
      <van-tab title="已取消" :name="4"></van-tab>
    </van-tabs>

    <div class="order-content">
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
        <van-list
          v-model:loading="loading"
          :finished="finished"
          finished-text="没有更多了"
          @load="onLoad"
        >
          <div v-for="order in filteredOrders" :key="order.id" class="glass-panel order-card" @click="$router.push(`/orders/${order.id}`)">
            <div class="card-header">
              <span class="order-no">订单号: {{ order.orderNo }}</span>
              <van-tag :type="getStatusType(order.status)" plain>{{ getStatusText(order.status) }}</van-tag>
            </div>
            
            <div class="card-body">
              <van-image
                width="80"
                height="80"
                radius="8"
                fit="cover"
                :src="order.roomImage || 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'"
                class="room-img"
              />
              <div class="order-info">
                <h3 class="room-name">{{ order.roomName || '未知房源' }}</h3>
                <p class="order-dates">{{ formatDate(order.checkInDate) }} - {{ formatDate(order.checkOutDate) }}</p>
                <p class="order-price">
                  总价: <span class="price-num">¥{{ order.totalAmount }}</span>
                </p>
              </div>
            </div>

            <div class="card-footer" @click.stop>
              <span class="create-time">{{ formatTime(order.createTime) }}</span>
              <div class="actions">
                <van-button 
                  v-if="order.status === 0" 
                  size="small" 
                  round 
                  plain 
                  type="default" 
                  @click="cancelOrder(order.id)"
                >
                  取消
                </van-button>
                <van-button 
                  v-if="order.status === 0" 
                  size="small" 
                  round 
                  color="var(--wood-500)" 
                  @click="payOrder(order.id)"
                >
                  去支付
                </van-button>
                <van-button 
                  v-if="order.status === 1" 
                  size="small" 
                  round 
                  plain 
                  type="default" 
                  @click="cancelOrder(order.id)"
                >
                  申请退款
                </van-button>
                <van-button 
                   v-if="order.status === 3"
                   size="small"
                   round
                   plain
                   type="primary"
                   @click="$router.push(`/rooms/${order.roomId}`)"
                >
                   再次预订
                </van-button>
              </div>
            </div>
          </div>
        </van-list>
      </van-pull-refresh>
      
      <van-empty v-if="!loading && filteredOrders.length === 0" description="暂无订单" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import api from '../api/axios'
import { showToast, showDialog, type TagType } from 'vant'
import dayjs from 'dayjs'

const activeTab = ref(-1)
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
const list = ref<any[]>([])

const filteredOrders = computed(() => {
  if (activeTab.value === -1) {
    return list.value
  }
  return list.value.filter(item => item.status === activeTab.value)
})

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

const getStatusType = (status: number): TagType => {
  const map: Record<number, TagType> = {
    0: 'warning',
    1: 'success',
    2: 'primary',
    3: 'default',
    4: 'danger'
  }
  return map[status] || 'default'
}

const formatDate = (date: string) => {
  return dayjs(date).format('MM月DD日')
}

const formatTime = (time: string) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

const onLoad = async () => {
  if (refreshing.value) {
    list.value = []
    refreshing.value = false
  }

  try {
    const res = await api.get('/orders/my')
    // Sort by createTime desc
    list.value = (res.data || []).sort((a: any, b: any) => 
      new Date(b.createTime).getTime() - new Date(a.createTime).getTime()
    )
    finished.value = true
  } catch (error) {
    console.error(error)
    showToast('加载失败')
    finished.value = true
  } finally {
    loading.value = false
  }
}

const onRefresh = () => {
  finished.value = false
  loading.value = true
  onLoad()
}

const payOrder = async (id: number) => {
  showToast({ message: '支付处理中...', type: 'loading' })
  try {
    await api.post(`/orders/${id}/pay`)
    showToast('支付成功')
    onRefresh()
  } catch (error: any) {
    showToast(error.response?.data?.message || '支付失败')
  }
}

const cancelOrder = (id: number) => {
  showDialog({
    title: '提示',
    message: '确定要取消/退款该订单吗？',
    showCancelButton: true,
    confirmButtonColor: 'var(--wood-500)'
  }).then(async (action) => {
    if (action === 'confirm') {
      try {
        await api.post(`/orders/${id}/cancel`)
        showToast('取消成功')
        onRefresh()
      } catch (error: any) {
        showToast(error.response?.data?.message || '取消失败')
      }
    }
  })
}
</script>

<style scoped>
.order-list-page {
  min-height: 100vh;
  background-color: var(--bg-100);
}

.glass-nav {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
}

:deep(.van-nav-bar__title) {
  font-weight: 600;
  color: var(--wood-700);
}

:deep(.van-icon-arrow-left) {
  color: var(--wood-700);
}

.order-content {
  padding: 16px;
}

.order-card {
  margin-bottom: 16px;
  padding: 0;
  overflow: hidden;
  background: #fff;
}

.card-header {
  padding: 12px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid var(--bg-200);
}

.order-no {
  font-size: 12px;
  color: var(--slate-500);
}

.card-body {
  padding: 16px;
  display: flex;
}

.room-img {
  flex-shrink: 0;
  margin-right: 12px;
  background: var(--bg-200);
}

.order-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.room-name {
  margin: 0 0 4px;
  font-size: 16px;
  color: var(--slate-900);
  font-weight: 600;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.order-dates {
  font-size: 13px;
  color: var(--slate-500);
  margin: 0;
}

.order-price {
  font-size: 13px;
  color: var(--slate-700);
  margin: 0;
  text-align: right;
}

.price-num {
  font-size: 18px;
  font-weight: 600;
  color: var(--wood-500);
}

.card-footer {
  padding: 12px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: var(--bg-100);
}

.create-time {
  font-size: 12px;
  color: var(--slate-400);
}

.actions {
  display: flex;
  gap: 8px;
}
</style>
