<template>
  <div class="order-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>订单管理</span>
          <el-button type="primary" @click="fetchOrders">刷新</el-button>
        </div>
      </template>
      
      <el-table :data="orders" v-loading="loading" style="width: 100%" border stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column label="房源/用户" width="200">
          <template #default="{ row }">
            <div>房源ID: {{ row.roomId }}</div>
            <div>用户ID: {{ row.userId }}</div>
          </template>
        </el-table-column>
        <el-table-column label="入住时间" width="200">
          <template #default="{ row }">
            <div>入: {{ row.checkInDate }}</div>
            <div>离: {{ row.checkOutDate }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="guestCount" label="人数" width="80" />
        <el-table-column prop="totalAmount" label="总金额" width="120">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: bold;">¥ {{ row.totalAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" fixed="right" width="250">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="viewDetail(row)">详情</el-button>
            
            <el-button 
              v-if="row.status === 1" 
              size="small" 
              type="success" 
              @click="handleCheckIn(row)"
            >
              办理入住
            </el-button>
            
            <el-button 
              v-if="row.status === 2" 
              size="small" 
              type="warning" 
              @click="handleCheckOut(row)"
            >
              办理退房
            </el-button>

            <el-popconfirm title="确定取消该订单吗？" @confirm="handleCancel(row)">
              <template #reference>
                <el-button 
                  v-if="row.status === 0 || row.status === 1" 
                  size="small" 
                  type="danger" 
                  link
                >
                  取消
                </el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="订单详情" width="500px">
      <el-descriptions :column="1" border v-if="currentOrder">
        <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="房源ID">{{ currentOrder.roomId }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ currentOrder.userId }}</el-descriptions-item>
        <el-descriptions-item label="入住日期">{{ currentOrder.checkInDate }}</el-descriptions-item>
        <el-descriptions-item label="退房日期">{{ currentOrder.checkOutDate }}</el-descriptions-item>
        <el-descriptions-item label="入住人数">{{ currentOrder.guestCount }}</el-descriptions-item>
        <el-descriptions-item label="总金额">¥ {{ currentOrder.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ getStatusText(currentOrder.status) }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ currentOrder.remark || '无' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentOrder.createTime }}</el-descriptions-item>
        <el-descriptions-item label="支付时间">{{ currentOrder.payTime || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import api from '../../api/axios'
import { ElMessage } from 'element-plus'

interface Order {
  id: number
  orderNo: string
  userId: number
  roomId: number
  checkInDate: string
  checkOutDate: string
  guestCount: number
  totalAmount: number
  status: number
  remark: string
  createTime: string
  payTime?: string
}

const orders = ref<Order[]>([])
const loading = ref(false)
const detailVisible = ref(false)
const currentOrder = ref<Order | null>(null)

const fetchOrders = async () => {
  loading.value = true
  try {
    const res = await api.get('/orders')
    // Sort by createTime desc
    orders.value = res.data.sort((a: Order, b: Order) => 
      new Date(b.createTime).getTime() - new Date(a.createTime).getTime()
    )
  } catch (error) {
    ElMessage.error('获取订单失败')
  } finally {
    loading.value = false
  }
}

const getStatusType = (status: number) => {
  const map: Record<number, string> = { 
    0: 'warning',   // 待支付
    1: 'success',   // 已支付
    2: 'primary',   // 已入住
    3: 'info',      // 已完成
    4: 'danger'     // 已取消
  }
  return map[status] || 'info'
}

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

const handleCheckIn = async (row: Order) => {
  try {
    await api.post(`/orders/${row.id}/check-in`)
    ElMessage.success('办理入住成功')
    fetchOrders()
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '办理失败')
  }
}

const handleCheckOut = async (row: Order) => {
  try {
    await api.post(`/orders/${row.id}/check-out`)
    ElMessage.success('办理退房成功')
    fetchOrders()
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '办理失败')
  }
}

const handleCancel = async (row: Order) => {
  try {
    await api.post(`/orders/${row.id}/cancel`)
    ElMessage.success('订单已取消')
    fetchOrders()
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '取消失败')
  }
}

const viewDetail = (row: Order) => {
  currentOrder.value = row
  detailVisible.value = true
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
