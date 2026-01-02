// import { createApp } from 'vue'
// import App from './App.vue'

// createApp(App).mount('#app')
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
// import './services/api' // 导入API配置
import './api/studentApi'

const app = createApp(App)

app.use(router)
app.use(ElementPlus) // 添加这一行

app.mount('#app')