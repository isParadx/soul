<template>
  <div class="start-container">
    <div class="start-form">
      <!-- 品牌区域 -->
      <div class="brand-area">
        <div class="brand-icon">
          <svg viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="24" cy="24" r="22" stroke="#5B8C8A" stroke-width="2" fill="#F7FAF9"/>
            <path d="M24 12C24 12 16 20 16 28C16 32.4183 19.5817 36 24 36C28.4183 36 32 32.4183 32 28C32 20 24 12 24 12Z" fill="#5B8C8A" opacity="0.15"/>
            <path d="M24 14V34M18 24H30" stroke="#5B8C8A" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </div>
        <h1 class="brand-title">心灵驿站</h1>
        <p class="brand-subtitle">学生心理咨询预约系统</p>
      </div>

      <!-- 登录/注册切换 -->
      <el-tabs v-model="activeTab" class="login-tabs">
        <el-tab-pane name="login" label="登录">
          <login-form v-if="activeTab === 'login'" />
        </el-tab-pane>
        <el-tab-pane name="register" label="注册">
          <register-form v-if="activeTab === 'register'" />
        </el-tab-pane>
      </el-tabs>
    </div>
    
    <!-- 底部提示 -->
    <div class="footer-hint">
      <p>如有紧急心理危机，请拨打全国心理援助热线：400-161-9995</p>
    </div>
  </div>
</template>

<script>
import LoginForm from '@/components/OutMain/Login.vue';
import RegisterForm from '@/components/OutMain/Register.vue';

export default {
  name: 'StartPage',
  components: {
    LoginForm,
    RegisterForm
  },
  data() {
    return {
      activeTab: 'login'
    };
  }
};
</script>

<style scoped>
html, body {
  height: 100%;
  margin: 0;
  padding: 0;
}

#app {
  height: 100%;
}

.start-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background-color: #F5F7F6;
  padding: 20px;
}

.start-form {
  width: 420px;
  max-width: 100%;
  background-color: #FFFFFF;
  border-radius: 8px;
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.06);
  padding: 40px 36px 32px;
}

/* 品牌区域 */
.brand-area {
  text-align: center;
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid #EEF1F0;
}

.brand-icon {
  width: 56px;
  height: 56px;
  margin: 0 auto 16px;
}

.brand-icon svg {
  width: 100%;
  height: 100%;
}

.brand-title {
  font-size: 24px;
  font-weight: 500;
  color: #2D3A3A;
  margin: 0 0 6px;
  letter-spacing: 2px;
}

.brand-subtitle {
  font-size: 13px;
  color: #8A9A98;
  margin: 0;
}

/* 标签页 */
.login-tabs {
  --el-tabs-header-height: 44px;
}

:deep(.el-tabs__header) {
  margin-bottom: 24px;
}

:deep(.el-tabs__nav-wrap::after) {
  height: 1px;
  background-color: #EEF1F0;
}

:deep(.el-tabs__item) {
  font-size: 15px;
  font-weight: 500;
  color: #7A8A88;
  padding: 0 20px;
  height: 44px;
  line-height: 44px;
  transition: color 0.2s;
}

:deep(.el-tabs__item:hover) {
  color: #5B8C8A;
}

:deep(.el-tabs__item.is-active) {
  color: #3D6A68;
}

:deep(.el-tabs__active-bar) {
  height: 2px;
  background-color: #5B8C8A;
}

/* 底部提示 */
.footer-hint {
  margin-top: 28px;
  text-align: center;
}

.footer-hint p {
  font-size: 12px;
  color: #9AAA98;
  margin: 0;
  line-height: 1.6;
}
</style>
