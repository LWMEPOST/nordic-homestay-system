import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    component: () => import('../layout/ClientLayout.vue'),
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('../views/Home.vue'),
        meta: { title: '房源推荐' }
      },
      {
        path: 'my',
        name: 'Profile',
        component: () => import('../views/Profile.vue'),
        meta: { title: '个人中心', requiresAuth: true }
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/rooms/:id',
    name: 'RoomDetail',
    component: () => import('../views/RoomDetail.vue')
  },
  {
    path: '/orders',
    name: 'OrderList',
    component: () => import('../views/OrderList.vue'),
    meta: { title: '我的订单', requiresAuth: true }
  },
  {
    path: '/booking',
    name: 'Booking',
    component: () => import('../views/Booking.vue'),
    meta: { title: '确认订单', requiresAuth: true }
  },
  {
    path: '/host/rooms',
    name: 'HostRoomList',
    component: () => import('../views/host/RoomList.vue'),
    meta: { title: '我的房源', requiresAuth: true }
  },
  {
    path: '/host/rooms/add',
    name: 'HostRoomAdd',
    component: () => import('../views/host/RoomEditor.vue'),
    meta: { title: '发布房源', requiresAuth: true }
  },
  {
    path: '/host/rooms/edit/:id',
    name: 'HostRoomEdit',
    component: () => import('../views/host/RoomEditor.vue'),
    meta: { title: '编辑房源', requiresAuth: true }
  },
  {
    path: '/orders/:id',
    name: 'OrderDetail',
    component: () => import('../views/OrderDetail.vue'),
    meta: { title: '订单详情', requiresAuth: true }
  },
  {
    path: '/settings',
    name: 'Settings',
    component: () => import('../views/Settings.vue'),
    meta: { title: '设置', requiresAuth: true }
  },
  {
    path: '/favorites',
    name: 'Favorites',
    component: () => import('../views/Favorites.vue'),
    meta: { title: '收藏夹', requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('client_token')
  if (to.meta.requiresAuth && !token) {
    next({
      path: '/login',
      query: { redirect: to.fullPath }
    })
  } else {
    next()
  }
})

export default router
