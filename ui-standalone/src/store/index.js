import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

// 最小 store：仅提供聊天页头像取数（无登录，头像为空时页面显示文字头像）
export default new Vuex.Store({
  modules: {
    user: {
      state: {
        avatar: ''
      }
    }
  }
})
