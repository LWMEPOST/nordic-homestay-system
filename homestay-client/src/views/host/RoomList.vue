<template>
  <div class="host-room-list">
    <van-nav-bar
      title="我的房源"
      left-arrow
      fixed
      placeholder
      @click-left="$router.back()"
      @click-right="$router.push('/host/rooms/add')"
    >
      <template #right>
        <van-icon name="plus" size="18" color="var(--wood-500)" />
      </template>
    </van-nav-bar>

    <div class="content">
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
        <van-list
          v-model:loading="loading"
          :finished="finished"
          finished-text="没有更多了"
          @load="onLoad"
        >
          <div v-for="room in list" :key="room.id" class="glass-panel room-card">
            <van-image
              :src="room.images?.[0]"
              width="100"
              height="80"
              radius="8"
              fit="cover"
              class="room-img"
            />
            <div class="room-info">
              <div class="room-header">
                <h3 class="room-name">{{ room.name }}</h3>
                <van-tag :type="room.status === 1 ? 'success' : 'danger'">
                  {{ room.status === 1 ? '已上架' : '已下架' }}
                </van-tag>
              </div>
              <p class="room-price">¥{{ room.basePrice }} /晚</p>
              <div class="room-actions">
                <van-button size="mini" round plain type="primary" @click="editRoom(room.id)">编辑</van-button>
                <van-button size="mini" round plain type="danger" @click="deleteRoom(room.id)">删除</van-button>
              </div>
            </div>
          </div>
        </van-list>
      </van-pull-refresh>
      <van-empty v-if="!loading && list.length === 0" description="暂无房源，快去发布吧" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../../api/axios'
import { showToast, showDialog } from 'vant'

const router = useRouter()
const list = ref<any[]>([])
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)

const onLoad = async () => {
  if (refreshing.value) {
    list.value = []
    refreshing.value = false
  }

  try {
    const res = await api.get('/rooms/my')
    list.value = res.data
    finished.value = true
  } catch (error) {
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

const editRoom = (id: number) => {
  router.push(`/host/rooms/edit/${id}`)
}

const deleteRoom = (id: number) => {
  showDialog({
    title: '提示',
    message: '确定要删除该房源吗？',
    showCancelButton: true,
  }).then(async (action) => {
    if (action === 'confirm') {
      try {
        await api.delete(`/rooms/${id}`)
        showToast('删除成功')
        onRefresh()
      } catch (error) {
        showToast('删除失败')
      }
    }
  })
}
</script>

<style scoped>
.host-room-list {
  min-height: 100vh;
  background-color: var(--bg-100);
}

.content {
  padding: 16px;
}

.room-card {
  display: flex;
  padding: 12px;
  margin-bottom: 12px;
  background: #fff;
  border-radius: 8px;
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

.room-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
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

.room-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
</style>
