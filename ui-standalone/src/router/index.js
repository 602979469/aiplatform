import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
  { path: '/', redirect: '/chat' },
  { path: '/chat', name: 'Chat', component: () => import('@/views/ai/chat/index.vue') },
  { path: '/mirror', name: 'Mirror', component: () => import('@/views/ai/mirror/index.vue') }
]

export default new VueRouter({
  mode: 'history',
  routes
})
