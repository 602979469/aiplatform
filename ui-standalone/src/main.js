import Vue from 'vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import App from './App.vue'
import router from './router'
import store from './store'
import SvgIcon from '@/components/SvgIcon/index.vue'
import hasPermi from '@/directive/permission/hasPermi'
import auth from '@/plugins/auth'

Vue.use(ElementUI)
Vue.use(auth)
Vue.component('svg-icon', SvgIcon)
Vue.directive('hasPermi', hasPermi)
Vue.config.productionTip = false

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
