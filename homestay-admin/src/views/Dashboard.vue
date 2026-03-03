<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="card-header">
              <span>总订单数</span>
              <el-tag type="success">月</el-tag>
            </div>
          </template>
          <div class="card-value">1,234</div>
          <div class="card-desc">较上月 +12%</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="card-header">
              <span>总收入</span>
              <el-tag type="warning">月</el-tag>
            </div>
          </template>
          <div class="card-value">¥ 45,678</div>
          <div class="card-desc">较上月 +5%</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="card-header">
              <span>入住率</span>
              <el-tag>周</el-tag>
            </div>
          </template>
          <div class="card-value">85%</div>
          <div class="card-desc">较上周 +2%</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="card-header">
              <span>新增用户</span>
              <el-tag type="danger">日</el-tag>
            </div>
          </template>
          <div class="card-value">12</div>
          <div class="card-desc">较昨日 -1</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>近期订单</span>
              <el-button text>查看全部</el-button>
            </div>
          </template>
          <el-table :data="recentOrders" style="width: 100%">
            <el-table-column prop="orderNo" label="订单号" width="180" />
            <el-table-column prop="guestName" label="客人" width="120" />
            <el-table-column prop="roomName" label="房源" />
            <el-table-column prop="checkInDate" label="入住日期" width="120" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>房态概览</span>
            </div>
          </template>
          <div class="room-status-list">
            <div v-for="room in roomStatuses" :key="room.id" class="room-status-item">
              <span>{{ room.name }}</span>
              <el-tag :type="room.available ? 'success' : 'info'" size="small">
                {{ room.available ? '空闲' : '已订' }}
              </el-tag>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import OccupancyBoard from './dashboard/OccupancyBoard.vue'

const recentOrders = ref([
  { orderNo: 'ORD2024022601', guestName: '张三', roomName: '海景大床房', checkInDate: '2024-02-27', status: 1 },
  { orderNo: 'ORD2024022602', guestName: '李四', roomName: '山景双床房', checkInDate: '2024-02-28', status: 0 },
  { orderNo: 'ORD2024022603', guestName: '王五', roomName: '家庭套房', checkInDate: '2024-03-01', status: 2 },
])

const roomStatuses = ref([
  { id: 1, name: '海景大床房', available: true },
  { id: 2, name: '山景双床房', available: false },
  { id: 3, name: '家庭套房', available: true },
  { id: 4, name: '情侣圆床房', available: true },
  { id: 5, name: '商务标准间', available: false },
])

const getStatusType = (status: number) => {
  const map: Record<number, string> = { 0: 'warning', 1: 'success', 2: 'info', 4: 'danger' }
  return map[status] || 'info'
}

const getStatusText = (status: number) => {
  const map: Record<number, string> = { 0: '待支付', 1: '已支付', 2: '已入住', 4: '已取消' }
  return map[status] || '未知'
}
</script>

<style scoped>
.mt-20 {
  margin-top: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.card-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin: 10px 0;
}
.card-desc {
  font-size: 12px;
  color: #909399;
}
.room-status-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.room-status-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f0f2f5;
}
.room-status-item:last-child {
  border-bottom: none;
}
</style>
