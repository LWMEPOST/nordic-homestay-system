<script setup lang="ts">
</script>

<template>
  <router-view v-slot="{ Component }">
    <transition name="fade" mode="out-in">
      <component :is="Component" />
    </transition>
  </router-view>
</template>

<style>
body {
  margin: 0;
  padding: 0;
  font-family: 'Inter', 'Noto Sans SC', -apple-system, BlinkMacSystemFont, 'Helvetica Neue', Helvetica, Segoe UI, Arial, Roboto, 'PingFang SC', 'Microsoft Yahei', sans-serif;
  -webkit-font-smoothing: antialiased;
  background-color: var(--bg-100, #fafaf9);
  color: var(--slate-900, #44403c);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
