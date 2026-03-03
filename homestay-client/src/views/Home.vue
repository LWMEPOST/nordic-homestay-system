<template>
  <div class="home">
    <div class="header glass-panel">
      <div class="brand">
        <span class="title">Homestay</span>
        <span class="subtitle">寻找你的理想栖息地</span>
      </div>
      <van-icon name="search" size="24" color="var(--slate-700)" />
    </div>
    
    <div class="content">
      <div class="section-title">
        <h2>热门推荐</h2>
        <span class="more">查看全部 <van-icon name="arrow" /></span>
      </div>

      <van-swipe class="banner" :autoplay="3000" indicator-color="white">
        <van-swipe-item v-for="(img, index) in banners" :key="index">
          <div class="banner-item">
            <img :src="img" />
            <div class="banner-overlay">
              <span class="banner-text">感受自然的呼吸</span>
            </div>
          </div>
        </van-swipe-item>
      </van-swipe>

      <div class="section-title mt-24">
        <h2>精选房源</h2>
      </div>

      <div class="room-list">
        <van-list
          v-model:loading="loading"
          :finished="finished"
          finished-text="没有更多了"
          @load="onLoad"
        >
          <div v-for="room in list" :key="room.id" class="room-card card" @click="goDetail(room.id)">
            <div class="room-image-wrapper">
              <van-image :src="room.images?.[0]" height="200" fit="cover" />
              <div class="room-price-tag">
                <span class="currency">¥</span>
                <span class="amount">{{ room.basePrice }}</span>
              </div>
            </div>
            <div class="room-info">
              <div class="room-tags">
                <span class="tag">{{ room.city }}</span>
                <span class="tag">{{ room.capacity }}人居</span>
              </div>
              <div class="room-name">{{ room.name }}</div>
              <div class="room-meta">
                <span class="rating"><van-icon name="star" color="#f6c85f" /> 4.9</span>
                <span class="reviews">128条评价</span>
              </div>
            </div>
          </div>
        </van-list>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api/axios'

const router = useRouter()
const list = ref<any[]>([])
const loading = ref(false)
const finished = ref(false)

const banners = [
  'https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
  'https://images.unsplash.com/photo-1566073771259-6a8506099945?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80'
]

const onLoad = async () => {
  try {
    const res = await api.get('/rooms')
    list.value = res.data
    finished.value = true
  } catch (error) {
    console.error(error)
    finished.value = true
  } finally {
    loading.value = false
  }
}

const goDetail = (id: number) => {
  router.push(`/rooms/${id}`)
}
</script>

<style scoped>
.home {
  padding-bottom: 60px;
}

.header {
  position: sticky;
  top: 0;
  z-index: 100;
  padding: 12px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 10px 16px;
}

.brand {
  display: flex;
  flex-direction: column;
}

.title {
  font-size: 20px;
  font-weight: 700;
  color: var(--wood-700);
  font-family: 'Georgia', serif;
}

.subtitle {
  font-size: 12px;
  color: var(--slate-500);
}

.content {
  padding: 0 16px;
}

.section-title {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 16px;
}

.section-title h2 {
  font-size: 20px;
  font-weight: 600;
  color: var(--slate-900);
  margin: 0;
}

.more {
  font-size: 13px;
  color: var(--slate-500);
  display: flex;
  align-items: center;
}

.mt-24 {
  margin-top: 24px;
}

.banner {
  border-radius: var(--radius-md);
  overflow: hidden;
  box-shadow: var(--card-shadow);
}

.banner-item {
  position: relative;
  height: 200px;
}

.banner-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.banner-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20px;
  background: linear-gradient(to top, rgba(0,0,0,0.6), transparent);
}

.banner-text {
  color: #fff;
  font-size: 18px;
  font-weight: 500;
}

.room-card {
  margin-bottom: 20px;
  transition: transform 0.2s;
}

.room-card:active {
  transform: scale(0.98);
}

.room-image-wrapper {
  position: relative;
}

.room-price-tag {
  position: absolute;
  bottom: 12px;
  right: 12px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(4px);
  padding: 4px 12px;
  border-radius: 20px;
  color: var(--wood-700);
  font-weight: 700;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.currency {
  font-size: 12px;
  margin-right: 2px;
}

.room-info {
  padding: 16px;
}

.room-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.tag {
  font-size: 11px;
  color: var(--slate-500);
  background: var(--bg-200);
  padding: 2px 8px;
  border-radius: 4px;
}

.room-name {
  font-size: 17px;
  font-weight: 600;
  color: var(--slate-900);
  margin-bottom: 8px;
  line-height: 1.4;
}

.room-meta {
  display: flex;
  align-items: center;
  font-size: 13px;
  color: var(--slate-500);
}

.rating {
  display: flex;
  align-items: center;
  margin-right: 12px;
  color: var(--slate-700);
  font-weight: 500;
}

.rating .van-icon {
  margin-right: 4px;
}
</style>
