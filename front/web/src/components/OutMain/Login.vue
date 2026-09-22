<template>
  <div class="login-form">
    <el-form ref="loginForm" :model="loginForm" :rules="loginRules" label-position="top" size="large">
      <el-form-item label="账号" prop="userid">
        <el-input 
          v-model="loginForm.userid" 
          placeholder="请输入您的账号"
          :prefix-icon="User"
          clearable
        />
      </el-form-item>
      
      <el-form-item label="密码" prop="password">
        <el-input 
          type="password" 
          v-model="loginForm.password" 
          placeholder="请输入您的密码"
          :prefix-icon="Lock"
          show-password
          @keyup.enter="submitLogin"
        />
      </el-form-item>
      
      <el-form-item class="button-area">
        <el-button 
          type="primary" 
          @click="submitLogin" 
          class="login-btn"
          :loading="loading"
        >
          {{ loading ? '登录中...' : '登 录' }}
        </el-button>
      </el-form-item>
    </el-form>

    <div class="login-hint">
      <p>学生用户请使用手机端登录</p>
    </div>
  </div>
</template>

<script>
import { User, Lock } from '@element-plus/icons-vue'

export default {
  name: 'LoginForm',
  components: {
    User,
    Lock
  },
  data() {
    return {
      loading: false,
      loginForm: {
        userid: '',
        password: ''
      },
      loginRules: {
        userid: [
          { required: true, message: '请输入账号', trigger: 'blur' },
          { min: 3, max: 20, message: '账号长度为3-20个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 30, message: '密码长度为6-30个字符', trigger: 'blur' }
        ]
      }
    };
  },
  methods: {
    submitLogin() {
      this.$refs.loginForm.validate((valid) => {
        if (valid) {
          this.loading = true;
          this.$axios.post('/user/login', this.loginForm)
            .then(res => {
              const data = res.data.data;
              if (data.role === "学生") {
                this.$message.warning("学生用户请移步至手机端操作");
              } else if (data.role === "管理员") {
                this.$message.success("登录成功");
                localStorage.setItem('token', data.token);
                sessionStorage.setItem("token", data.token);
                setTimeout(() => {
                  this.$router.push('/master/userinfo');
                }, 800);
              } else if (data.role === "医生") {
                this.$message.success("登录成功");
                localStorage.setItem('token', data.token);
                sessionStorage.setItem("token", data.token);
                setTimeout(() => {
                  this.$router.push('/doctor/appointments');
                }, 800);
              }
            })
            .catch(error => {
              const msg = error.response?.data?.message || error.message || '登录失败';
              this.$error(msg);
            })
            .finally(() => {
              this.loading = false;
            });
        }
      });
    }
  }
};
</script>

<style scoped>
.login-form {
  width: 100%;
}

:deep(.el-form-item__label) {
  font-size: 13px;
  color: #5A6A68;
  font-weight: 500;
  padding-bottom: 6px;
}

:deep(.el-input__wrapper) {
  border-radius: 6px;
  box-shadow: 0 0 0 1px #D8E0DE inset;
  transition: box-shadow 0.2s;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #B8C8C6 inset;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #5B8C8A inset;
}

:deep(.el-input__inner)::placeholder {
  color: #B0BCBA;
}

.button-area {
  margin-top: 28px;
  margin-bottom: 0;
}

.login-btn {
  width: 100%;
  height: 42px;
  font-size: 15px;
  border-radius: 6px;
  background-color: #5B8C8A;
  border-color: #5B8C8A;
  letter-spacing: 4px;
  transition: background-color 0.2s;
}

.login-btn:hover {
  background-color: #4A7A78;
  border-color: #4A7A78;
}

.login-btn:active {
  background-color: #3D6A68;
  border-color: #3D6A68;
}

.login-hint {
  text-align: center;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px dashed #EEF1F0;
}

.login-hint p {
  font-size: 12px;
  color: #B0BCBA;
  margin: 0;
}
</style>
