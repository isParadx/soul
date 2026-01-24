<template>
    <el-container style="height: 98.5vh;"> <!-- 设置容器高度为视口高度 -->
      <el-header class="header">
      <el-col :span="20">
        <el-icon><House /></el-icon>
        <span class="title">学生心理咨询预约管理系统</span>
      </el-col>
      <el-col :span="4" class="user-info">
        <el-dropdown>
          <span style="color: black;">
            Hi,doctor!
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
        <el-aside  style="background-color: #eef1f6;width:200px;">
          <el-menu
            default-active="1"
            class="el-menu-vertical-demo"
            @select="handleSelect"
            style="border-right: 0;"
            router
          >
            <el-menu-item index="/doctor/appointments">
              <i class="el-icon-date"></i>
              预约管理
            </el-menu-item>
            <el-menu-item index="/doctor/docinfo">
              <i class="el-icon-user"></i>
              个人信息
            </el-menu-item>
          </el-menu>
        </el-aside>
        <el-main style="padding: 20px; background-color: #f1f1f1;">
          <router-view></router-view>
        </el-main>
      </el-container>
      <el-container>
        <CozeChat />
      </el-container>
    </el-container>
  </template>
  
  <script>
  import { ref } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import CozeChat from '../../components/CozeChat/CozeChat.vue';
  
  const route = useRoute();
  const router = useRouter();
  const handleSelect = (index, indexPath) => {
    router.push(index);
  };
  export default{
  components: {
    CozeChat
  },
  methods:{
    handleCommand(){
      this.$message({
            message:'账号已退出，请重新登录',
            type:'success'
            })
      sessionStorage.clear();
      this.$router.push('/');   
    }
  }
}
  </script>

  <style scoped>
.header {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color:#f7f8f9; 
  padding: 10px 20px; 
  color: rgb(4, 0, 0); 
}

.title {
  margin: 0 20px; 
  font-size: 18px; 
}
  
  .el-aside {
    color: #333;
    background-color: #eef1f6; /* 侧边栏背景颜色 */
  }
  
  .el-menu-vertical-demo:not(.el-menu--collapse) {
    width: 200px;
    min-height: 400px;
    height: 90vh;
  }
  
  /* 调整el-main的样式 */
  .el-main {
    padding: 20px;
    background-color: #f1f1f1; /* 主内容区域背景颜色 */
  }
  </style>