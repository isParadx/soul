import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import ElementPlus, { ElMessage } from 'element-plus';
import 'element-plus/dist/index.css';
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { customAxios } from './api/axios';
import './assets/main.css';

// 创建 Vue 应用实例
const app = createApp(App);

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
  }
app.use(router);
app.use(ElementPlus);

app.config.globalProperties.$axios = customAxios;
// 全局消息提示（Element Plus不会自动注册到globalProperties，需手动绑定）
app.config.globalProperties.$message = ElMessage;
// 全局错误提示：this.$error(msg)
app.config.globalProperties.$error = (msg: string) => {
  ElMessage.error(msg || '操作失败，请稍后重试');
};

// 挂载 Vue 应用实例
app.mount('#app');