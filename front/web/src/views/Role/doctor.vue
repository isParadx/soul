<template>
  <el-container class="doctor-layout">
    <!-- 顶部导航 -->
    <el-header class="doctor-header">
      <div class="header-brand">
        <span class="brand-icon">◈</span>
        <span class="brand-text">心灵驿站 · 医生工作台</span>
      </div>
      <div class="header-user">
        <span class="user-name">{{ username || '医生' }}</span>
        <el-dropdown trigger="click" @command="handleCommand">
          <el-avatar :size="32" class="user-avatar">{{ username ? username.charAt(0) : '医' }}</el-avatar>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout">
                <el-icon><SwitchButton /></el-icon>退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>

    <el-container class="doctor-body">
      <!-- 侧边栏 -->
      <el-aside width="200px" class="doctor-aside">
        <el-menu
          :default-active="activeMenu"
          background-color="#FAFBFB"
          text-color="#5A6A68"
          active-text-color="#5B8C8A"
          class="side-menu"
          router
        >
          <el-menu-item index="/doctor/appointments">
            <el-icon><Calendar /></el-icon>
            <span>预约管理</span>
          </el-menu-item>
          <el-menu-item index="/doctor/docinfo">
            <el-icon><UserFilled /></el-icon>
            <span>个人信息</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主内容区 -->
      <el-main class="doctor-main">
        <router-view v-slot="{ Component }">
          <transition name="page-fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>

    <!-- AI助手 -->
    <CozeChat />
  </el-container>
</template>

<script>
import { Calendar, UserFilled, SwitchButton } from '@element-plus/icons-vue'
import CozeChat from '../../components/CozeChat/CozeChat.vue';

export default {
  name: 'DoctorLayout',
  components: {
    Calendar,
    UserFilled,
    SwitchButton,
    CozeChat
  },
  data() {
    return {
      username: '',
      activeMenu: '/doctor/appointments'
    }
  },
  created() {
    const userInfo = sessionStorage.getItem('userInfo');
    if (userInfo) {
      try {
        this.username = JSON.parse(userInfo).nickname || '医生';
      } catch (e) {
        this.username = '医生';
      }
    }
    this.activeMenu = this.$route.path;
  },
  watch: {
    '$route.path'(path) {
      this.activeMenu = path;
    }
  },
  methods: {
    handleCommand(cmd) {
      if (cmd === 'logout') {
        this.$success('已退出登录');
        localStorage.removeItem('token');
        sessionStorage.clear();
        this.$router.push('/');
      }
    }
  }
}
</script>

<style scoped>
.doctor-layout {
  height: 100vh;
  overflow: hidden;
}

/* 顶部 */
.doctor-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 56px;
  padding: 0 24px;
  background-color: #FFFFFF;
  border-bottom: 1px solid #E8EDEB;
}

.header-brand {
  display: flex;
  align-items: center;
  gap: 10px;
}

.brand-icon {
  font-size: 20px;
  color: #5B8C8A;
}

.brand-text {
  font-size: 15px;
  font-weight: 500;
  color: #2D3A3A;
  letter-spacing: 1px;
}

.header-user {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-name {
  font-size: 14px;
  color: #5A6A68;
}

.user-avatar {
  background-color: #7AA8A6;
  cursor: pointer;
  font-size: 14px;
}

/* 主体区域 */
.doctor-body {
  height: calc(100vh - 56px);
}

/* 侧边栏 */
.doctor-aside {
  background-color: #FAFBFB;
  border-right: 1px solid #E8EDEB;
}

.side-menu {
  border-right: none;
  height: 100%;
  padding: 12px 0;
}

.side-menu:not(.el-menu--collapse) {
  width: 200px;
}

:deep(.el-menu-item) {
  height: 48px;
  line-height: 48px;
  margin: 2px 8px;
  border-radius: 6px;
  font-size: 14px;
}

:deep(.el-menu-item:hover) {
  background-color: #EEF3F2 !important;
}

:deep(.el-menu-item.is-active) {
  background-color: #E8F2F1 !important;
  font-weight: 500;
}

:deep(.el-menu-item .el-icon) {
  width: 18px;
  margin-right: 8px;
  font-size: 17px;
}

/* 主内容 */
.doctor-main {
  background-color: #F5F7F6;
  padding: 20px;
  overflow-y: auto;
}

/* 页面切换动画 */
.page-fade-enter-active,
.page-fade-leave-active {
  transition: opacity 0.2s ease;
}

.page-fade-enter-from,
.page-fade-leave-to {
  opacity: 0;
}

/* 滚动条 */
.doctor-aside::-webkit-scrollbar,
.doctor-main::-webkit-scrollbar {
  width: 4px;
}

.doctor-aside::-webkit-scrollbar-thumb,
.doctor-main::-webkit-scrollbar-thumb {
  background-color: #D8E0DE;
  border-radius: 4px;
}
</style>
