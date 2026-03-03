<template>
  <div class="room-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>房源列表</span>
          <el-button type="primary" @click="handleAdd">添加房源</el-button>
        </div>
      </template>
      
      <el-table :data="rooms" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="图片" width="100">
          <template #default="scope">
            <el-image 
              v-if="scope.row.images && scope.row.images.length"
              :src="scope.row.images[0]" 
              :preview-src-list="scope.row.images"
              style="width: 60px; height: 60px"
              fit="cover"
            />
            <span v-else>无图</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="basePrice" label="基准价格" width="120">
          <template #default="scope">¥ {{ scope.row.basePrice }}</template>
        </el-table-column>
        <el-table-column prop="capacity" label="容纳人数" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button 
              size="small" 
              :type="scope.row.status === 1 ? 'warning' : 'success'"
              @click="toggleStatus(scope.row)"
            >
              {{ scope.row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Dialog for Add/Edit -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑房源' : '添加房源'"
      width="50%"
    >
      <el-form :model="form" label-width="100px">
        <el-form-item label="名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>
        <el-form-item label="基准价格">
          <el-input-number v-model="form.basePrice" :min="0" />
        </el-form-item>
        <el-form-item label="容纳人数">
          <el-input-number v-model="form.capacity" :min="1" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="form.address" />
        </el-form-item>
        <el-form-item label="城市">
          <el-input v-model="form.city" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import api from '../../api/axios'
import { ElMessage, ElMessageBox } from 'element-plus'

interface Room {
  id?: number
  name: string
  description: string
  basePrice: number
  capacity: number
  images: string[]
  address: string
  city: string
  status: number
}

const rooms = ref<Room[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = reactive<Room>({
  name: '',
  description: '',
  basePrice: 0,
  capacity: 2,
  images: [],
  address: '',
  city: '',
  status: 1
})

const fetchRooms = async () => {
  loading.value = true
  try {
    const res = await api.get('/rooms')
    rooms.value = res.data
  } catch (error) {
    ElMessage.error('获取房源失败')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, {
    name: '',
    description: '',
    basePrice: 0,
    capacity: 2,
    images: [],
    address: '',
    city: '',
    status: 1
  })
  delete form.id
  dialogVisible.value = true
}

const handleEdit = (row: Room) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const submitForm = async () => {
  try {
    if (isEdit.value) {
      await api.put(`/rooms/${form.id}`, form)
      ElMessage.success('更新成功')
    } else {
      await api.post('/rooms', form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchRooms()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const toggleStatus = async (row: Room) => {
  try {
    const newStatus = row.status === 1 ? 0 : 1
    await api.put(`/rooms/${row.id}`, { ...row, status: newStatus })
    ElMessage.success('状态更新成功')
    fetchRooms()
  } catch (error) {
    ElMessage.error('状态更新失败')
  }
}

const handleDelete = (row: Room) => {
  ElMessageBox.confirm('确定要删除该房源吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await api.delete(`/rooms/${row.id}`)
      ElMessage.success('删除成功')
      fetchRooms()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

onMounted(() => {
  fetchRooms()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
