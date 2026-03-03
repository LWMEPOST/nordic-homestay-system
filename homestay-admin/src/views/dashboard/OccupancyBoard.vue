<template>
  <div class="occupancy-board">
    <div class="board-header">
      <div class="date-controls">
        <el-button-group>
          <el-button :icon="ArrowLeft" @click="prevPeriod" />
          <el-button @click="resetToToday">今天</el-button>
          <el-button :icon="ArrowRight" @click="nextPeriod" />
        </el-button-group>
        <span class="current-date">{{ dateRangeText }}</span>
      </div>
      <div class="legend">
        <span class="legend-item"><span class="color-box available"></span>空闲</span>
        <span class="legend-item"><span class="color-box booked"></span>已预订</span>
        <span class="legend-item"><span class="color-box maintenance"></span>维护中</span>
      </div>
    </div>

    <div class="matrix-container" v-loading="loading">
      <table class="matrix-table">
        <thead>
          <tr>
            <th class="room-header">房源 / 日期</th>
            <th v-for="day in dateHeaders" :key="day.dateStr" :class="{ 'is-weekend': day.isWeekend, 'is-today': day.isToday }">
              <div class="day-cell">
                <span class="day-name">{{ day.dayName }}</span>
                <span class="day-date">{{ day.dateDisplay }}</span>
              </div>
            </th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="room in rooms" :key="room.id">
            <td class="room-name">
              <div class="room-info">
                <span class="name">{{ room.name }}</span>
                <el-tag size="small" effect="plain">¥{{ room.basePrice }}</el-tag>
              </div>
            </td>
            <td v-for="day in dateHeaders" :key="day.dateStr" class="grid-cell" @click="handleCellClick(room, day)">
              <div class="cell-content" :class="getCellClass(room.id, day.dateStr)">
                <span v-if="getCellStatus(room.id, day.dateStr) === 'booked'" class="status-icon">📅</span>
                <span v-if="getCellStatus(room.id, day.dateStr) === 'maintenance'" class="status-icon">🔧</span>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Quick Action Dialog -->
    <el-dialog v-model="dialogVisible" title="房态操作" width="400px">
      <div class="action-info" v-if="selectedCell">
        <p><strong>房源:</strong> {{ selectedCell.room.name }}</p>
        <p><strong>日期:</strong> {{ selectedCell.date }}</p>
        <p><strong>当前状态:</strong> {{ getStatusText(selectedCell.status) }}</p>
      </div>
      <div class="action-buttons">
        <el-button type="primary" v-if="selectedCell?.status === 'available'" @click="handleManualBook">
          手动预订
        </el-button>
        <el-button type="warning" v-if="selectedCell?.status === 'available'" @click="handleLockMaintenance">
          设为维护
        </el-button>
        <el-button type="success" v-if="selectedCell?.status === 'maintenance'" @click="handleUnlock">
          解除维护
        </el-button>
        <el-button type="info" v-if="selectedCell?.status === 'booked'" disabled>
          查看订单 (开发中)
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { ArrowLeft, ArrowRight } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'
import api from '../../api/axios'
import { ElMessage } from 'element-plus'

dayjs.locale('zh-cn')

const props = defineProps<{
  initialDate?: string
}>()

const currentDate = ref(dayjs(props.initialDate || new Date()))
const daysToShow = 14
const loading = ref(false)
const rooms = ref<any[]>([])
const availabilityMap = ref<Record<string, number>>({}) // Key: roomId_date, Value: status
const dialogVisible = ref(false)
const selectedCell = ref<any>(null)

// Computed
const dateHeaders = computed(() => {
  const headers = []
  for (let i = 0; i < daysToShow; i++) {
    const d = currentDate.value.add(i, 'day')
    headers.push({
      dateStr: d.format('YYYY-MM-DD'),
      dateDisplay: d.format('MM-DD'),
      dayName: d.format('ddd'), // 周几
      isWeekend: d.day() === 0 || d.day() === 6,
      isToday: d.isSame(dayjs(), 'day')
    })
  }
  return headers
})

const dateRangeText = computed(() => {
  const start = dateHeaders.value[0].dateStr
  const end = dateHeaders.value[daysToShow - 1].dateStr
  return `${start} ~ ${end}`
})

// Methods
const fetchRooms = async () => {
  try {
    const res = await api.get('/rooms')
    rooms.value = res.data
  } catch (error) {
    console.error('Fetch rooms failed', error)
  }
}

const fetchAvailability = async () => {
  loading.value = true
  const startDate = dateHeaders.value[0].dateStr
  const endDate = dateHeaders.value[daysToShow - 1].dateStr
  
  try {
    // We need to fetch availability for all rooms.
    // Currently API supports by room ID. We might need to loop or update API.
    // For MVP, let's loop through rooms (not efficient but works for small number of rooms)
    // Or better, let's just assume we have an endpoint that takes a list or we fetch for each.
    // Actually, let's just fetch for each room for now.
    
    const promises = rooms.value.map(room => 
      api.get('/availability', { params: { roomId: room.id, startDate, endDate } })
    )
    
    const results = await Promise.all(promises)
    const newMap: Record<string, number> = {}
    
    results.forEach((res, index) => {
      const roomId = rooms.value[index].id
      res.data.forEach((item: any) => {
        newMap[`${roomId}_${item.date}`] = item.status
      })
    })
    
    availabilityMap.value = newMap
  } catch (error) {
    console.error('Fetch availability failed', error)
  } finally {
    loading.value = false
  }
}

const getCellStatus = (roomId: number, dateStr: string) => {
  const status = availabilityMap.value[`${roomId}_${dateStr}`]
  if (status === 1) return 'booked'
  if (status === 2) return 'maintenance'
  return 'available'
}

const getCellClass = (roomId: number, dateStr: string) => {
  return getCellStatus(roomId, dateStr)
}

const prevPeriod = () => {
  currentDate.value = currentDate.value.subtract(7, 'day')
}

const nextPeriod = () => {
  currentDate.value = currentDate.value.add(7, 'day')
}

const resetToToday = () => {
  currentDate.value = dayjs()
}

const handleCellClick = (room: any, day: any) => {
  const status = getCellStatus(room.id, day.dateStr)
  selectedCell.value = {
    room,
    date: day.dateStr,
    status
  }
  dialogVisible.value = true
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = { available: '空闲', booked: '已预订', maintenance: '维护中' }
  return map[status]
}

const handleManualBook = () => {
  ElMessage.info('请前往订单管理页面创建订单')
  dialogVisible.value = false
}

const handleLockMaintenance = async () => {
  // Need API support for locking
  ElMessage.warning('暂未实现维护锁定 API')
  dialogVisible.value = false
}

const handleUnlock = async () => {
  // Need API support for unlocking
  ElMessage.warning('暂未实现解除维护 API')
  dialogVisible.value = false
}

onMounted(async () => {
  await fetchRooms()
  fetchAvailability()
})

watch(currentDate, () => {
  if (rooms.value.length > 0) {
    fetchAvailability()
  }
})
</script>

<style scoped>
.occupancy-board {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.board-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.date-controls {
  display: flex;
  align-items: center;
  gap: 15px;
}

.current-date {
  font-weight: bold;
  color: #606266;
}

.legend {
  display: flex;
  gap: 15px;
}

.legend-item {
  display: flex;
  align-items: center;
  font-size: 12px;
  color: #606266;
}

.color-box {
  width: 16px;
  height: 16px;
  border-radius: 4px;
  margin-right: 5px;
}

.color-box.available { background-color: #f5f7fa; border: 1px solid #e4e7ed; }
.color-box.booked { background-color: #f0f9eb; border: 1px solid #67c23a; }
.color-box.maintenance { background-color: #fef0f0; border: 1px solid #f56c6c; }

.matrix-container {
  overflow-x: auto;
}

.matrix-table {
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed;
}

.matrix-table th, .matrix-table td {
  border: 1px solid #ebeef5;
  text-align: center;
  padding: 0;
}

.room-header {
  width: 150px;
  padding: 10px;
  background-color: #f5f7fa;
  color: #909399;
}

.day-cell {
  padding: 8px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.day-name {
  font-size: 12px;
  color: #909399;
}

.day-date {
  font-weight: bold;
  color: #606266;
}

.is-weekend .day-cell {
  background-color: #fdf6ec;
}

.is-today .day-cell {
  background-color: #ecf5ff;
  color: #409eff;
}

.room-name {
  padding: 10px;
  text-align: left;
  background-color: #fff;
}

.room-info {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.grid-cell {
  height: 60px;
  cursor: pointer;
  transition: all 0.2s;
}

.grid-cell:hover {
  background-color: #f2f6fc;
}

.cell-content {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cell-content.available { background-color: #fff; }
.cell-content.booked { background-color: #f0f9eb; color: #67c23a; }
.cell-content.maintenance { background-color: #fef0f0; color: #f56c6c; }

.status-icon {
  font-size: 20px;
}

.action-info p {
  margin: 5px 0;
}

.action-buttons {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
