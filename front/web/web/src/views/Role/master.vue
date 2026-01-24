<template>
  <el-container class="tac">
    <el-header class="header">
      <el-col :span="20">
        <el-icon><House /></el-icon>
        <span class="title">学生心理咨询预约管理系统</span>
      </el-col>
      <el-col :span="4" class="user-info">
        <el-dropdown>
          <span style="color: white;">
            Hi,admin
            <el-icon class="el-icon--right">
              <arrow-down />
            </el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click=" handleCommand">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-col>
    </el-header>
  
    <el-container>
      <el-aside width="200px" class="aside">
        <el-menu
            unique-opened:true
            active-text-color="#ffd04b"
            background-color="#545c64"
            class="el-menu-vertical-demo"
            router
            text-color="#fff"
        >
            
          <el-sub-menu index="1">
                <template #title>
                    <el-icon><user /></el-icon>
                    <span>用户账户管理</span>
                </template>
                <el-menu-item index="/master/userinfo">用户账户管理</el-menu-item>
            </el-sub-menu>
            <el-sub-menu index="2">
                <template #title>
                    <el-icon><calendar /></el-icon>
                    <span>预约咨询管理</span>
                </template>
                <el-menu-item index="/master/doctorinfo">医生详情管理</el-menu-item>
                <el-menu-item index="/master/consultationinfo">学生预约管理</el-menu-item>
            </el-sub-menu>
            <el-sub-menu index="3">
                <template #title>
                    <el-icon><message /></el-icon>
                    <span>意见反馈</span>
                </template>
                <el-menu-item index="/master/advice">意见反馈列表</el-menu-item>
            </el-sub-menu>
        </el-menu>
      </el-aside>
      <el-main class="main">
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import {
  ArrowDown,
  House,
  User,
  Calendar,
  Message
} from '@element-plus/icons-vue'

export default{
  methods:{
    handleCommand(){
      this.$message({
            message:'账号已退出，请重新登录',
            type:'success'
            })
      sessionStorage.clear();
      this.$router.push('/'); 
       //弹窗提示登录成功
     
    }
  }
}
</script>
<style scoped>
html, body {
  height: 100%;
  margin: 0;
  padding: 0;
  font-family: 'Arial', sans-serif; /* 设置默认字体 */
}
#app {
  height: 100%;
}
.header {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color:#545c64; 
  padding: 10px 20px; 
  color: white; 
}

.title {
  margin: 0 20px; 
  font-size: 18px; 
}
.aside {
  background-color: #545c64;
  color: #5b1111;
  height: 92vh;
}

.main {
  padding: 20px;
}

.el-menu-vertical-demo:not(.el-menu--collapse) {
  width: 200px;
  height: 100%;
  min-height: 400px;
}

.el-aside {
  color: #fff;
}

.el-menu-item {
  color: #fff;
}

.el-menu-item.is-active {
  background-color: #ffd04b;
  color: #333744;
}

.el-submenu__title {
  background-color: #3e4254;
}

.el-menu-item i, .el-submenu__title i {
  margin-right: 10px;
  color: #fff;
}
</style>