<template>
  <div class="settings-page">
    <van-nav-bar
      title="设置"
      left-arrow
      fixed
      placeholder
      @click-left="$router.back()"
    />

    <div class="content">
      <div class="glass-panel">
        <van-cell title="个人信息" is-link @click="showProfileEdit = true" />
        <van-cell title="我的优惠券" is-link @click="$router.push('/coupons')" />
        <van-cell title="账号安全" is-link />
      </div>

      <div class="logout-section" v-if="user">
        <van-button block class="logout-btn" @click="handleLogout">
          退出登录
        </van-button>
      </div>
    </div>

    <!-- Edit Profile Dialog -->
    <van-dialog v-model:show="showProfileEdit" title="修改个人信息" show-cancel-button @confirm="saveProfile">
      <van-form class="profile-form">
        <van-field
          v-model="editForm.nickname"
          name="nickname"
          label="昵称"
          placeholder="请输入昵称"
        />
        <van-field
          v-model="editForm.phone"
          name="phone"
          label="手机号"
          placeholder="请输入手机号"
        />
        <van-field
          v-model="editForm.email"
          name="email"
          label="邮箱"
          placeholder="请输入邮箱"
        />
      </van-form>
    </van-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showDialog, showToast } from 'vant'
import api from '../api/axios'

const router = useRouter()
const user = ref<any>(null)
const showProfileEdit = ref(false)
const editForm = ref({
  nickname: '',
  phone: '',
  email: ''
})

onMounted(() => {
  const userStr = localStorage.getItem('client_user')
  if (userStr) {
    user.value = JSON.parse(userStr)
    editForm.value = { ...user.value }
  }
})

const saveProfile = async () => {
  try {
    // Assuming backend has update user API. For now, we mock update local storage.
    // In real app: await api.put('/users/me', editForm.value)
    
    // Mock update
    const updatedUser = { ...user.value, ...editForm.value }
    localStorage.setItem('client_user', JSON.stringify(updatedUser))
    user.value = updatedUser
    showToast('修改成功')
  } catch (error) {
    showToast('修改失败')
  }
}

const handleLogout = () => {
  showDialog({
    title: '提示',
    message: '确定要退出登录吗？',
    showCancelButton: true,
  }).then((action) => {
    if (action === 'confirm') {
      localStorage.removeItem('client_token')
      localStorage.removeItem('client_user')
      router.push('/login')
    }
  })
}
</script>

<style scoped>
.settings-page {
  min-height: 100vh;
  background-color: var(--bg-100);
}

.content {
  padding: 16px;
}

.glass-panel {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 24px;
}

.logout-btn {
  color: var(--danger);
  border: none;
  background: #fff;
  border-radius: 12px;
}

.profile-form {
  padding: 16px;
}
</style>
