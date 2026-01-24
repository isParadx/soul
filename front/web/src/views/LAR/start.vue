<template>
  <div class="start-container">
    <div class="start-form">
      <el-tabs v-model="activeTab" class="tab-bar">
        <el-tab-pane name="login" label="登录">
          <login-form v-if="activeTab === 'login'" class="form-container" />
        </el-tab-pane>
        <el-tab-pane name="register" label="注册">
          <register-form v-if="activeTab === 'register'" class="form-container" />
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script>
import LoginForm from '@/components/OutMain/Login.vue';
import RegisterForm from '@/components/OutMain/Register.vue';

export default {
  components: {
    LoginForm,
    RegisterForm
  },
  data() {
    return {
      activeTab: 'login' // 默认显示登录标签
    };
  }
};
</script>

<style scoped>
/* 确保html和body的高度为100% */
html, body {
  height: 100%;
  margin: 0;
  padding: 0;
  font-family: 'Arial', sans-serif; /* 设置默认字体 */
}
#app {
  height: 100%;
}

.start-container {
  display: flex;
  flex-direction: column;
  align-items: center; 
  height: 98vh;
  background-image: url('../../assets/loginback.png');
  background-size: 100vw 100vh; 
  background-position: center; 
  background-repeat: no-repeat; 
}
.start-form{
  position: absolute; 
  left: 40vw; 
  top: 30vh; 
  display: flex;
  flex-direction: column;
  align-items: center; 
  padding: 20px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  border-radius: 12px; 
  background: rgba(255, 255, 255, 0.8); 
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.5);
}

.tab-bar {
  border: none;
}

.el-tabs__nav-wrap::after,
.el-tabs__nav-scroll::after {
  height: 0;
}

.el-tabs__item {
  border: 1px solid #dcdfe6;
  border-bottom: none;
  transition: color 0.3s;
}

.el-tabs__item.is-active {
  border-color: #dcdfe6;
  border-bottom: 2px solid #409eff;
  color: #409eff;
}

.el-tabs__content {
  border: none;
  flex-grow: 1;
}

.el-tab-pane {
  padding: 20px 0;
  width: 90%; 
}

.form-container {
  width: 100%;
  background: #fff; 
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2); 
  padding: 10px; 
}

/* 输入框和按钮的样式 */
.form-container input {
  width: calc(100% - 20px); 
  padding: 10px;
  margin-bottom: 15px;
  border: 1px solid #dcdfe6; 
  border-radius: 4px; 
  transition: border-color 0.3s; 
}

.form-container input:focus {
  border-color: #409eff; 
  outline: none; 
}

.form-container button {
  width: 100%;
  padding: 10px;
  border: none;
  border-radius: 4px;
  background-color: #409eff;
  color: white;
  cursor: pointer;
  transition: background-color 0.3s;
}

.form-container button:hover {
  background-color: #337ecc; 
}

/* 登录/注册切换按钮的样式 */
.el-tabs__item {
  padding: 10px 20px;
  margin: 0 5px;
  border-radius: 4px; 
  background-color: rgba(255, 255, 255, 0.5); 
  color: #333;
  font-weight: bold;
}
</style>