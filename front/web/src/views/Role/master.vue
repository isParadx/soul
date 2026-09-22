<template>
  <el-container class="admin-layout">
    <!-- 顶部导航 -->
    <el-header class="admin-header">
      <div class="header-brand">
        <span class="brand-icon">◈</span>
        <span class="brand-text">心灵驿站 · 管理后台</span>
      </div>
      <div class="header-user">
        <span class="user-name">{{ username || '管理员' }}</span>
        <el-dropdown trigger="click" @command="handleCommand">
          <el-avatar :size="32" class="user-avatar">{{ username ? username.charAt(0) : '管' }}</el-avatar>
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

    <el-container class="admin-body">
      <!-- 侧边栏 -->
      <el-aside width="200px" class="admin-aside">
        <el-menu
          :default-active="activeMenu"
          unique-opened
          background-color="#2D3A3A"
          text-color="#B8C8C6"
          active-text-color="#FFFFFF"
          class="side-menu"
          router
        >
          <el-sub-menu index="1">
            <template #title>
              <el-icon><User /></el-icon>
              <span>用户账户</span>
            </template>
            <el-menu-item index="/master/userinfo">用户管理</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="2">
            <template #title>
              <el-icon><Calendar /></el-icon>
              <span>预约咨询</span>
            </template>
            <el-menu-item index="/master/doctorinfo">医生管理</el-menu-item>
            <el-menu-item index="/master/consultationinfo">预约管理</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="3">
            <template #title>
              <el-icon><ChatDotRound /></el-icon>
              <span>意见反馈</span>
            </template>
            <el-menu-item index="/master/advice">反馈列表</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-aside>

      <!-- 主内容区 -->
      <el-main class="admin-main">
        <router-view v-slot="{ Component }">
          <transition name="page-fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { User, Calendar, SwitchButton, ChatDotRound } from '@element-plus/icons-vue'

export default {
  name: 'AdminLayout',
  components: {
    User,
    Calendar,
    SwitchButton,
    ChatDotRound
  },
  data() {
    return {
      username: '',
      activeMenu: '/master/userinfo'
    }
  },
  created() {
    const userInfo = sessionStorage.getItem('userInfo');
    if (userInfo) {
      try {
        this.username = JSON.parse(userInfo).nickname || '管理员';
      } catch (e) {
        this.username = '管理员';
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
.admin-layout {
  height: 100vh;
  overflow: hidden;
}

/* 顶部 */
.admin-header {
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
  background-color: #5B8C8A;
  cursor: pointer;
  font-size: 14px;
}

/* 主体区域 */
.admin-body {
  height: calc(100vh - 56px);
}

/* 侧边栏 */
.admin-aside {
  background-color: #2D3A3A;
  overflow-y: auto;
}

.side-menu {
  border-right: none;
  height: 100%;
}

.side-menu:not(.el-menu--collapse) {
  width: 200px;
}

:deep(.el-sub-menu__title) {
  font-size: 14px;
  font-weight: 500;
}

:deep(.el-sub-menu__title:hover) {
  background-color: #3D4A4A !important;
}

:deep(.el-menu-item) {
  font-size: 13px;
  padding-left: 52px !important;
}

:deep(.el-menu-item:hover) {
  background-color: #3D4A4A !important;
}

:deep(.el-menu-item.is-active) {
  background-color: #5B8C8A !important;
  color: #FFFFFF;
}

/* 主内容 */
.admin-main {
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
.admin-aside::-webkit-scrollbar,
.admin-main::-webkit-scrollbar {
  width: 4px;
}

.admin-aside::-webkit-scrollbar-thumb {
  background-color: #4A5A58;
  border-radius: 4px;
}

.admin-main::-webkit-scrollbar-thumb {
  background-color: #D8E0DE;
  border-radius: 4px;
}
</style>
