<template>
  <div class="room-editor">
    <van-nav-bar
      :title="isEdit ? '编辑房源' : '发布房源'"
      left-arrow
      fixed
      placeholder
      @click-left="$router.back()"
    />

    <van-form @submit="onSubmit">
      <div class="content">
        <div class="glass-panel form-card">
          <van-field
            v-model="form.name"
            name="name"
            label="标题"
            placeholder="给房源起个好听的名字"
            :rules="[{ required: true, message: '请填写标题' }]"
          />
          <van-field
            v-model="form.city"
            name="city"
            label="城市"
            placeholder="例如：北京"
            :rules="[{ required: true, message: '请填写城市' }]"
          />
          <van-field
            v-model="form.address"
            name="address"
            label="地址"
            placeholder="详细地址"
            :rules="[{ required: true, message: '请填写地址' }]"
          />
          <van-field
            v-model="form.basePrice"
            name="basePrice"
            label="价格"
            type="number"
            placeholder="每晚价格"
            :rules="[{ required: true, message: '请填写价格' }]"
          />
          <van-field name="capacity" label="入住人数">
            <template #input>
              <van-stepper v-model="form.capacity" min="1" max="10" />
            </template>
          </van-field>
          <van-field
            v-model="form.description"
            name="description"
            label="描述"
            type="textarea"
            placeholder="介绍一下你的房源..."
            rows="3"
            autosize
          />
        </div>

        <div class="glass-panel form-card">
          <h3 class="card-title">房源图片</h3>
          <van-uploader
            v-model="fileList"
            multiple
            :max-count="5"
            :after-read="afterRead"
            @delete="onDelete"
          />
        </div>

        <div class="glass-panel form-card">
          <h3 class="card-title">设施服务</h3>
          <div class="facilities-grid">
            <div 
              v-for="item in facilitiesList" 
              :key="item.name" 
              class="facility-chip"
              :class="{ active: form.facilities.includes(item.name) }"
              @click="toggleFacility(item.name)"
            >
              <van-icon :name="item.icon" size="20" :color="form.facilities.includes(item.name) ? '#fff' : 'var(--wood-500)'" />
              <span class="facility-name">{{ item.name }}</span>
            </div>
          </div>
        </div>

        <div class="submit-bar">
          <van-button round block type="primary" color="var(--wood-500)" native-type="submit" :loading="loading">
            {{ isEdit ? '保存修改' : '立即发布' }}
          </van-button>
        </div>
      </div>
    </van-form>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '../../api/axios'
import { showToast, type UploaderFileListItem } from 'vant'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const isEdit = computed(() => !!route.params.id)

const facilitiesList = [
  { name: 'WiFi', icon: 'wifi' },
  { name: '空调', icon: 'fire-o' }, // Vant doesn't have AC icon, using fire/snow
  { name: '热水', icon: 'hot-o' },
  { name: '厨房', icon: 'shop-o' },
  { name: '停车位', icon: 'logistics' },
  { name: '洗衣机', icon: 'umbrella-circle' },
  { name: '电视', icon: 'tv-o' },
  { name: '冰箱', icon: 'bag-o' }
]

const form = ref({
  name: '',
  city: '',
  address: '',
  basePrice: '',
  capacity: 2,
  description: '',
  facilities: [] as string[],
  images: [] as string[],
  status: 1
})

const fileList = ref<UploaderFileListItem[]>([])

const fetchRoom = async () => {
  if (!isEdit.value) return
  try {
    const res = await api.get(`/rooms/${route.params.id}`)
    const data = res.data
    form.value = {
      ...data,
      basePrice: String(data.basePrice),
      facilities: data.facilities || []
    }
    // Initialize fileList from existing images
    if (data.images) {
      fileList.value = data.images.map((url: string) => ({ url, isImage: true }))
    }
  } catch (error) {
    showToast('获取详情失败')
  }
}

const afterRead = async (file: any) => {
  file.status = 'uploading'
  file.message = '上传中...'

  const formData = new FormData()
  formData.append('file', file.file)

  try {
    const res = await api.post('/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    file.status = 'done'
    file.message = '上传成功'
    
    // Backend returns relative path like "/uploads/xxx.jpg"
    // Since we proxy /uploads in vite.config.ts, we can use it directly as src
    // Or if we need absolute URL for some reason (e.g. saving to DB), relative is usually better for portability
    // But for preview in Vant Uploader, we might need full URL if proxy doesn't apply to img src automatically in all cases (it should)
    // Let's use the returned path.
    file.url = res.data
  } catch (error) {
    file.status = 'failed'
    file.message = '上传失败'
  }
}

const onDelete = (_file: any) => {
  // Optional: call delete API if needed
}

const toggleFacility = (name: string) => {
  if (!form.value.facilities) form.value.facilities = []
  const index = form.value.facilities.indexOf(name)
  if (index > -1) {
    form.value.facilities.splice(index, 1)
  } else {
    form.value.facilities.push(name)
  }
}

const onSubmit = async () => {
  loading.value = true
  
  // Collect images from fileList
  form.value.images = fileList.value
    .filter(item => item.status === 'done' || item.url)
    .map(item => item.url || '')

  try {
    if (isEdit.value) {
      await api.put(`/rooms/${route.params.id}`, form.value)
      showToast('修改成功')
    } else {
      await api.post('/rooms', form.value)
      showToast('发布成功')
    }
    router.back()
  } catch (error: any) {
    showToast(error.response?.data?.message || '提交失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchRoom()
})
</script>

<style scoped>
.room-editor {
  min-height: 100vh;
  background-color: var(--bg-100);
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

.card-title {
  margin: 0 0 12px;
  font-size: 16px;
  color: var(--wood-700);
}

.submit-bar {
  margin-top: 32px;
  margin-bottom: 32px;
}

.facilities-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.facility-chip {
  width: calc(50% - 6px);
  padding: 12px;
  background: var(--bg-100);
  border-radius: 8px;
  display: flex;
  align-items: center;
  transition: all 0.3s;
  border: 1px solid transparent;
}

.facility-chip.active {
  background: var(--wood-500);
  color: #fff;
  border-color: var(--wood-600);
}

.facility-name {
  margin-left: 8px;
  font-size: 14px;
}

.upload-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

:deep(.custom-uploader .van-uploader__upload) {
  background-color: var(--bg-100) !important;
  border-radius: 8px !important;
}

:deep(.custom-uploader .van-uploader__preview-image) {
  border-radius: 8px !important;
}

.upload-text {
  font-size: 10px;
  color: var(--slate-500);
  margin-top: 4px;
}
</style>
