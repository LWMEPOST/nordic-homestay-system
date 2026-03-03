<template>
  <div class="login-page">
    <div class="login-bg"></div>
    
    <div class="login-container">
      <div class="glass-panel login-card">
        <div class="brand-section">
          <div class="logo-icon">
            <van-icon name="wap-home-o" size="48" color="var(--wood-500)" />
          </div>
          <h1>{{ isRegister ? '创建账号' : '欢迎回家' }}</h1>
          <p class="subtitle">{{ isRegister ? '加入我们，开启旅程' : '登录以管理您的预订' }}</p>
        </div>
        
        <van-form @submit="onSubmit">
          <div class="form-fields">
            <van-field
              v-model="username"
              name="username"
              placeholder="请输入用户名"
              :rules="[{ required: true, message: '请填写用户名' }]"
              class="custom-field"
            >
              <template #left-icon>
                <van-icon name="manager-o" class="field-icon" />
              </template>
            </van-field>
            
            <van-field
              v-model="password"
              type="password"
              name="password"
              placeholder="请输入密码"
              :rules="[{ required: true, message: '请填写密码' }]"
              class="custom-field"
            >
              <template #left-icon>
                <van-icon name="lock" class="field-icon" />
              </template>
            </van-field>

            <van-field
              v-if="isRegister"
              v-model="confirmPassword"
              type="password"
              name="confirmPassword"
              placeholder="确认密码"
              :rules="[{ required: true, message: '请确认密码' }, { validator: validatePass, message: '两次输入密码不一致' }]"
              class="custom-field"
            >
              <template #left-icon>
                <van-icon name="shield-o" class="field-icon" />
              </template>
            </van-field>

            <div v-if="isRegister" class="role-selector">
              <span class="role-label">我是：</span>
              <van-radio-group v-model="role" direction="horizontal">
                <van-radio name="GUEST" checked-color="var(--wood-500)">房客</van-radio>
                <van-radio name="HOST" checked-color="var(--wood-500)">房东</van-radio>
              </van-radio-group>
            </div>
          </div>

          <div class="submit-section">
            <van-button 
              round 
              block 
              color="var(--wood-500)" 
              native-type="submit"
              class="submit-btn"
              :loading="loading"
            >
              {{ isRegister ? '注册' : '登录' }}
            </van-button>
          </div>
        </van-form>
        
        <div class="actions">
          <span class="switch-mode" @click="toggleMode">
            {{ isRegister ? '已有账号？立即登录' : '没有账号？免费注册' }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import api from '../api/axios'
import { showSuccessToast, showFailToast } from 'vant'

const router = useRouter()
const route = useRoute()
const username = ref('')
const password = ref('')
const confirmPassword = ref('')
const role = ref('GUEST')
const isRegister = ref(false)
const loading = ref(false)

const validatePass = (val: string) => {
  return val === password.value
}

const toggleMode = () => {
  isRegister.value = !isRegister.value
  username.value = ''
  password.value = ''
  confirmPassword.value = ''
}

const onSubmit = async (values: any) => {
  loading.value = true
  try {
    const url = isRegister.value ? '/auth/register' : '/auth/login'
    const payload = isRegister.value 
      ? { username: values.username, password: values.password, role: role.value }
      : { username: values.username, password: values.password }

    const res = await api.post(url, payload)
    
    if (isRegister.value) {
      showSuccessToast('注册成功，请登录')
      toggleMode()
    } else {
      localStorage.setItem('client_token', res.data.token)
      localStorage.setItem('client_user', JSON.stringify({
        username: res.data.username,
        role: res.data.role,
        userId: res.data.userId
      }))
      
      showSuccessToast('登录成功')
      const redirect = route.query.redirect as string
      router.replace(redirect || '/')
    }
  } catch (error: any) {
    showFailToast(error.response?.data?.message || '操作失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  background-color: var(--bg-200);
}

/* Abstract background shapes */
.login-bg::before, .login-bg::after {
  content: '';
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  z-index: 0;
}

.login-bg::before {
  width: 300px;
  height: 300px;
  background: rgba(139, 69, 19, 0.15); /* wood-500 with opacity */
  top: -50px;
  left: -50px;
}

.login-bg::after {
  width: 250px;
  height: 250px;
  background: rgba(120, 113, 108, 0.1); /* slate-500 with opacity */
  bottom: -50px;
  right: -50px;
}

.login-container {
  width: 100%;
  padding: 20px;
  z-index: 1;
}

.login-card {
  padding: 40px 24px;
  /* Extra glass effect specifically for login */
  background: rgba(255, 255, 255, 0.85);
}

.brand-section {
  text-align: center;
  margin-bottom: 40px;
}

.logo-icon {
  margin-bottom: 16px;
  display: inline-block;
  padding: 12px;
  background: rgba(139, 69, 19, 0.05);
  border-radius: 50%;
}

h1 {
  font-family: 'Playfair Display', serif; /* Use serif if available, else fallback */
  font-size: 28px;
  color: var(--wood-700);
  margin: 0 0 8px;
  font-weight: 600;
}

.subtitle {
  color: var(--slate-500);
  font-size: 14px;
  margin: 0;
}

.form-fields {
  margin-bottom: 24px;
}

.custom-field {
  background: transparent;
  border-bottom: 1px solid rgba(0,0,0,0.05);
  padding: 16px 0;
  margin-bottom: 8px;
}

.custom-field:last-child {
  border-bottom: none;
}

.field-icon {
  font-size: 20px;
  color: var(--wood-400);
  margin-right: 8px;
}

.role-selector {
  margin-top: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.role-label {
  font-size: 14px;
  color: var(--slate-500);
  margin-right: 12px;
}

.submit-btn {
  font-weight: 600;
  letter-spacing: 1px;
  box-shadow: 0 4px 12px rgba(139, 69, 19, 0.2);
}

.actions {
  margin-top: 24px;
  text-align: center;
}

.switch-mode {
  color: var(--wood-500);
  font-size: 14px;
  cursor: pointer;
  transition: opacity 0.2s;
}

.switch-mode:active {
  opacity: 0.7;
}

:deep(.van-field__control) {
  font-size: 16px;
}
</style>
