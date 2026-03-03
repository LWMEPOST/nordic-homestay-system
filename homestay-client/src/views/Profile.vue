<template>
  <div class="profile-page">
    <!-- Header Background -->
    <div class="profile-header">
      <div class="header-content">
        <h2>个人中心</h2>
        <p class="greeting" v-if="user">很高兴再次见到您，{{ user.username }}</p>
        <p class="greeting" v-else>欢迎来到 Nordic Homestay</p>
      </div>
    </div>

    <div class="profile-content">
      <!-- User Card -->
      <div class="glass-panel user-card" @click="!user && $router.push('/login')">
        <div class="avatar-section">
          <van-image
            round
            width="80px"
            height="80px"
            fit="cover"
            :src="user?.avatar || 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'"
            class="avatar-img"
          />
          <div class="user-status" v-if="user">
            <van-tag type="primary" color="var(--wood-500)">已认证会员</van-tag>
          </div>
        </div>
        
        <div class="user-info">
          <template v-if="user">
            <h3 class="username">{{ user.username }}</h3>
            <p class="role-badge">{{ user.role === 'ADMIN' ? '管理员' : '尊贵会员' }}</p>
          </template>
          <template v-else>
            <h3 class="username">立即登录</h3>
            <p class="sub-text">登录后查看订单与优惠</p>
          </template>
        </div>
        
        <van-icon name="arrow" class="arrow-icon" v-if="!user" />
      </div>

      <!-- Menu Grid -->
      <div class="menu-section">
        <div class="menu-grid">
          <div class="menu-item glass-panel" @click="$router.push('/orders')">
            <van-icon name="orders-o" size="28" color="var(--wood-500)" />
            <span>我的订单</span>
          </div>
          <div v-if="user?.role === 'HOST' || user?.role === 'ADMIN'" class="menu-item glass-panel" @click="$router.push('/host/rooms')">
            <van-icon name="hotel-o" size="28" color="var(--wood-500)" />
            <span>我的房源</span>
          </div>
          <div class="menu-item glass-panel" @click="$router.push('/favorites')">
            <van-icon name="like-o" size="28" color="var(--wood-500)" />
            <span>收藏夹</span>
          </div>
          <div class="menu-item glass-panel">
            <van-icon name="coupon-o" size="28" color="var(--wood-500)" />
            <span>优惠券</span>
          </div>
          <div class="menu-item glass-panel" @click="contactSupport">
            <van-icon name="service-o" size="28" color="var(--wood-500)" />
            <span>联系客服</span>
          </div>
        </div>
      </div>

      <!-- Settings List -->
      <div class="settings-section">
        <h4 class="section-title">更多服务</h4>
        <div class="glass-panel settings-list">
          <van-cell title="设置" is-link icon="setting-o" @click="$router.push('/settings')" />
        </div>
      </div>

      <!-- Logout Button -->
      <div class="logout-section" v-if="user">
        <van-button block class="logout-btn" @click="handleLogout">
          退出登录
        </van-button>
      </div>
    </div>
    
    <!-- Tabbar Placeholder -->
    <div style="height: 60px;"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showDialog } from 'vant'

const router = useRouter()
const user = ref<any>(null)

onMounted(() => {
  const userStr = localStorage.getItem('client_user')
  if (userStr) {
    user.value = JSON.parse(userStr)
  }
})

const handleLogout = () => {
  showDialog({
    title: '提示',
    message: '确定要退出登录吗？',
    showCancelButton: true,
    confirmButtonColor: 'var(--wood-500)'
  }).then((action) => {
    if (action === 'confirm') {
      localStorage.removeItem('client_token')
      localStorage.removeItem('client_user')
      user.value = null
      router.push('/login')
    }
  })
}

const contactSupport = () => {
  showDialog({
    title: '联系客服',
    message: '客服电话：10086',
    confirmButtonText: '呼叫',
    showCancelButton: true
  }).then((action) => {
    if (action === 'confirm') {
      window.location.href = 'tel:10086'
    }
  })
}
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background-color: var(--bg-100);
}

.profile-header {
  height: 240px;
  background: linear-gradient(135deg, var(--wood-700), var(--wood-500));
  padding: 40px 24px;
  color: #fff;
  position: relative;
  overflow: hidden;
}

.profile-header::after {
  content: '';
  position: absolute;
  bottom: -20px;
  left: 0;
  right: 0;
  height: 40px;
  background: var(--bg-100);
  border-radius: 50% 50% 0 0 / 100% 100% 0 0;
}

.header-content h2 {
  margin: 0 0 8px;
  font-family: 'Playfair Display', serif;
  font-size: 28px;
}

.greeting {
  opacity: 0.9;
  font-size: 14px;
  margin: 0;
}

.profile-content {
  padding: 0 20px;
  margin-top: -60px;
  position: relative;
  z-index: 1;
}

.user-card {
  display: flex;
  align-items: center;
  padding: 24px;
  margin-bottom: 24px;
  background: rgba(255, 255, 255, 0.95);
}

.avatar-section {
  position: relative;
  margin-right: 20px;
}

.avatar-img {
  border: 4px solid #fff;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.user-status {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  white-space: nowrap;
}

.user-info {
  flex: 1;
}

.username {
  margin: 0 0 4px;
  font-size: 20px;
  color: var(--wood-700);
}

.sub-text, .role-badge {
  margin: 0;
  font-size: 13px;
  color: var(--slate-500);
}

.arrow-icon {
  color: var(--slate-500);
}

.menu-section {
  margin-bottom: 24px;
}

.menu-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.menu-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 16px 8px;
  background: #fff;
  font-size: 12px;
  color: var(--slate-700);
  gap: 8px;
  transition: transform 0.2s;
}

.menu-item:active {
  transform: scale(0.95);
}

.section-title {
  font-size: 16px;
  color: var(--wood-700);
  margin: 0 0 12px;
  padding-left: 4px;
}

.settings-list {
  background: #fff;
  overflow: hidden;
  padding: 0;
}

.logout-section {
  margin-top: 32px;
}

.logout-btn {
  background: transparent;
  color: var(--slate-500);
  border: 1px solid var(--slate-300);
}
</style>
