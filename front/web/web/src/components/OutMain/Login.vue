<template>
  <div class="login-container">
    <el-form ref="loginForm" :model="loginForm" label-width="100px" class="login-form">
      <el-form-item label="账号">
        <el-input v-model="loginForm.userid"></el-input>
      </el-form-item>
      <el-form-item label="密码">
        <el-input type="password" v-model="loginForm.password"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitLogin" class="login-button">登录</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { messageEmits } from 'element-plus';


export default {
  data() {
    return {
      loginForm: {
        userid: '',
        password: ''
      }
    };
  },
  methods: {
    submitLogin() {
      this.$axios.post('/user/login', this.loginForm)
        .then(res => {
         
          if(res.data.data.role=="学生"){
            this.$message.error("对不起，学生用户请移步至手机端操作")
          }else if(res.data.data.role=="管理员"){
            //弹窗提示登录成功
            this.$message.success("登录成功，正在为您跳转！")
            setTimeout(() => {
              sessionStorage.setItem("token",res.data.data.token)
              this.$router.push('/master/userinfo');
            }, 1000); // 延时1000毫秒
          }else if(res.data.data.role=="医生"){
            //弹窗提示登录成功
            this.$message.success("登录成功，正在为您跳转！")
            setTimeout(() => {
              sessionStorage.setItem("token",res.data.data.token)
              this.$router.push('/doctor/appointments');
            }, 1000); // 延时1000毫秒 
          }
        })
        .catch(error => {
          this.$message({
              message:'错误！',
              type:'error'
            })
          console.error('Error fetching protected data:', error);
        });
    }
  }
};
</script>


<style scoped>
.login-container {
  width: 300px;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f5f5;
}

.login-form {
  width: 300px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  background-color: #fff;
}

.login-button {
  width: 100%;
}
</style>