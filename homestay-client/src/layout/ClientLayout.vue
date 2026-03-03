<template>
  <div class="client-layout">
    <router-view v-slot="{ Component }">
      <transition name="fade" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
    
    <van-tabbar 
      v-model="active"
      fixed 
      placeholder
      active-color="var(--wood-500)"
      inactive-color="var(--slate-500)"
      class="glass-tabbar"
    >
      <van-tabbar-item name="home" to="/" icon="wap-home-o">首页</van-tabbar-item>
      <van-tabbar-item name="profile" to="/my" icon="user-o">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const active = ref('home')

watch(() => route.path, (newPath) => {
  if (newPath === '/') {
    active.value = 'home'
  } else if (newPath.startsWith('/my')) {
    active.value = 'profile'
  }
}, { immediate: true })
</script>

<style scoped>
.client-layout {
  min-height: 100vh;
  background-color: var(--bg-100);
}

.glass-tabbar {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-top: 1px solid rgba(0,0,0,0.05);
  z-index: 9999 !important;
  bottom: 0;
  position: fixed;
  width: 100%;
}
</style>
