<template>
  <div class="pricing-rules">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>定价规则管理</span>
          <el-button type="primary" @click="handleAdd">添加规则</el-button>
        </div>
      </template>

      <el-form :inline="true" class="search-form">
        <el-form-item label="房源">
          <el-select v-model="searchRoomId" placeholder="选择房源" clearable @change="fetchRules">
            <el-option
              v-for="room in rooms"
              :key="room.id"
              :label="room.name"
              :value="room.id"
            />
          </el-select>
        </el-form-item>
      </el-form>

      <el-table :data="rules" v-loading="loading" style="width: 100%">
        <el-table-column prop="ruleType" label="类型" width="120">
          <template #default="scope">
            <el-tag :type="getRuleTypeTag(scope.row.ruleType)">
              {{ getRuleTypeText(scope.row.ruleType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="targetDate" label="目标日期" width="120">
          <template #default="scope">
            {{ scope.row.targetDate || '通用' }}
          </template>
        </el-table-column>
        <el-table-column prop="priceMultiplier" label="价格倍率" width="100" />
        <el-table-column prop="priceAdd" label="固定加价" width="100">
          <template #default="scope">
            {{ scope.row.priceAdd ? '¥ ' + scope.row.priceAdd : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="80" />
        <el-table-column prop="description" label="描述" />
        <el-table-column label="操作" width="100">
          <template #default="scope">
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Add Rule Dialog -->
    <el-dialog v-model="dialogVisible" title="添加定价规则" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="房源">
          <el-select v-model="form.roomId" placeholder="适用房源（留空为全局）" clearable>
            <el-option
              v-for="room in rooms"
              :key="room.id"
              :label="room.name"
              :value="room.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="规则类型">
          <el-select v-model="form.ruleType">
            <el-option label="周末 (周六/日)" value="WEEKEND" />
            <el-option label="节假日" value="HOLIDAY" />
            <el-option label="自定义日期" value="CUSTOM" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标日期" v-if="form.ruleType === 'CUSTOM'">
          <el-date-picker
            v-model="form.targetDate"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="价格倍率">
          <el-input-number v-model="form.priceMultiplier" :step="0.1" :min="0" />
          <div class="form-tip">例如 1.2 表示上浮 20%</div>
        </el-form-item>
        <el-form-item label="固定加价">
          <el-input-number v-model="form.priceAdd" :step="10" />
          <div class="form-tip">例如 50 表示加 50 元</div>
        </el-form-item>
        <el-form-item label="优先级">
          <el-input-number v-model="form.priority" :min="0" />
          <div class="form-tip">数字越大越优先</div>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" />
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
import { ref, reactive, onMounted } from 'vue'
import api from '../../api/axios'
import { ElMessage, ElMessageBox } from 'element-plus'

interface Room {
  id: number
  name: string
}

interface PricingRule {
  id?: number
  roomId?: number
  ruleType: string
  targetDate?: string
  priceMultiplier?: number
  priceAdd?: number
  priority: number
  description: string
}

const rules = ref<PricingRule[]>([])
const rooms = ref<Room[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const searchRoomId = ref<number | undefined>(undefined)

const form = reactive<PricingRule>({
  ruleType: 'WEEKEND',
  priority: 0,
  description: '',
  priceMultiplier: 1.0,
  priceAdd: 0
})

const fetchRooms = async () => {
  try {
    const res = await api.get('/rooms')
    rooms.value = res.data
  } catch (error) {
    console.error('Fetch rooms failed', error)
  }
}

const fetchRules = async () => {
  loading.value = true
  try {
    let url = '/pricing/rules/room/null' // Default endpoint for global rules or we need a list endpoint
    // Currently backend only supports getting rules by room ID
    // We might need to iterate or add an endpoint to get all rules.
    // For now, let's rely on searchRoomId. If not set, maybe clear or show nothing?
    // Wait, the backend has /pricing/rules/room/{roomId}.
    // If we want all rules, we might need a new endpoint.
    // Let's assume we search by room.
    
    if (searchRoomId.value) {
      const res = await api.get(`/pricing/rules/room/${searchRoomId.value}`)
      rules.value = res.data
    } else {
        // If no room selected, maybe show empty or try to fetch for a default room if exists
        rules.value = []
    }
  } catch (error) {
    ElMessage.error('获取规则失败')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  Object.assign(form, {
    roomId: searchRoomId.value,
    ruleType: 'WEEKEND',
    targetDate: '',
    priceMultiplier: 1.0,
    priceAdd: 0,
    priority: 0,
    description: ''
  })
  delete form.id
  dialogVisible.value = true
}

const submitForm = async () => {
  try {
    await api.post('/pricing/rules', form)
    ElMessage.success('创建成功')
    dialogVisible.value = false
    if (form.roomId) {
        searchRoomId.value = form.roomId
        fetchRules()
    }
  } catch (error) {
    ElMessage.error('创建失败')
  }
}

const handleDelete = (row: PricingRule) => {
  ElMessageBox.confirm('确定要删除该规则吗?', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await api.delete(`/pricing/rules/${row.id}`)
      ElMessage.success('删除成功')
      fetchRules()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

const getRuleTypeTag = (type: string) => {
  const map: Record<string, string> = { WEEKEND: 'warning', HOLIDAY: 'danger', CUSTOM: 'primary' }
  return map[type] || 'info'
}

const getRuleTypeText = (type: string) => {
  const map: Record<string, string> = { WEEKEND: '周末', HOLIDAY: '节假日', CUSTOM: '自定义' }
  return map[type] || type
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
.search-form {
  margin-bottom: 20px;
}
.form-tip {
  font-size: 12px;
  color: #909399;
  line-height: 1.2;
  margin-top: 4px;
}
</style>
