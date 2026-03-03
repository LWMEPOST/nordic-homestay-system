<template>
  <div class="favorite-list-page">
    <van-nav-bar
      title="我的收藏"
      left-arrow
      fixed
      placeholder
      @click-left="$router.back()"
    />

    <div class="content">
      <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="onLoad"
      >
        <div v-for="room in list" :key="room.id" class="glass-panel room-card" @click="$router.push(`/rooms/${room.id}`)">
          <van-image
            :src="room.images?.[0] || 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'"
            width="100"
            height="80"
            radius="8"
            fit="cover"
            class="room-img"
          />
          <div class="room-info">
            <h3 class="room-name">{{ room.name }}</h3>
            <p class="room-price">¥{{ room.basePrice }} /晚</p>
            <div class="actions" @click.stop>
              <van-button size="mini" round plain type="danger" @click="removeFavorite(room.id)">取消收藏</van-button>
            </div>
          </div>
        </div>
      </van-list>
      <van-empty v-if="!loading && list.length === 0" description="暂无收藏" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api/axios'
import { showToast } from 'vant'

const router = useRouter()
const list = ref<any[]>([])
const loading = ref(false)
const finished = ref(false)

const onLoad = async () => {
  try {
    const favorites = JSON.parse(localStorage.getItem('favorites') || '[]')
    if (favorites.length === 0) {
      list.value = []
      finished.value = true
      return
    }

    // Since we don't have a batch get API, we fetch all rooms and filter
    // Or we could fetch one by one. For MVP, fetching all rooms is acceptable if data set is small.
    // Better: Fetch details for each ID.
    
    // Let's try to fetch all rooms and filter by ID for now as it's simpler without new backend API
    const res = await api.get('/rooms')
    const allRooms = res.data
    list.value = allRooms.filter((r: any) => favorites.includes(r.id))
    finished.value = true
  } catch (error) {
    showToast('加载失败')
    finished.value = true
  } finally {
    loading.value = false
  }
}

const removeFavorite = (id: number) => {
  const favorites = JSON.parse(localStorage.getItem('favorites') || '[]')
  const index = favorites.indexOf(id)
  if (index > -1) {
    favorites.splice(index, 1)
    localStorage.setItem('favorites', JSON.stringify(favorites))
    // Update list
    list.value = list.value.filter(item => item.id !== id)
    showToast('已取消收藏')
  }
}
</script>

<style scoped>
.favorite-list-page {
  min-height: 100vh;
  background-color: var(--bg-100);
}

.content {
  padding: 16px;
}

.glass-panel {
  background: #fff;
  border-radius: 12px;
  padding: 12px;
  margin-bottom: 12px;
}

.room-card {
  display: flex;
}

.room-img {
  flex-shrink: 0;
  margin-right: 12px;
}

.room-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.room-name {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--slate-900);
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.room-price {
  margin: 4px 0;
  color: var(--wood-500);
  font-weight: 600;
}

.actions {
  display: flex;
  justify-content: flex-end;
}
</style>
