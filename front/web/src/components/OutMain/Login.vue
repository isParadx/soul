<template>
  <div class="login-form">
    <el-form ref="loginForm" :model="loginForm" :rules="loginRules" label-position="top" size="large">
      <el-form-item label="账号 / 手机号 / 邮箱" prop="account">
        <el-input 
          v-model="loginForm.account" 
          placeholder="请输入账号、手机号或邮箱"
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

      <div class="option-row">
        <el-checkbox v-model="rememberMe">记住账号</el-checkbox>
        <span class="forget-link" @click="onForget">忘记密码？</span>
      </div>
      
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
      <p>医生 / 管理员请使用电脑端登录，学生请使用手机 App</p>
    </div>
  </div>
</template>

<script>
import { markRaw } from 'vue';
import { User, Lock } from '@element-plus/icons-vue'

export default {
  name: 'LoginForm',
  components: {
    User,
    Lock
  },
  data() {
    return {
      // 图标组件需通过实例属性暴露，模板中 :prefix-icon 才能访问到
      User: markRaw(User),
      Lock: markRaw(Lock),
      loading: false,
      rememberMe: true,
      loginForm: {
        account: '',
        password: ''
      },
      loginRules: {
        account: [
          { required: true, message: '请输入账号、手机号或邮箱', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度为6-20位', trigger: 'blur' }
        ]
      }
    };
  },
  created() {
    // 优先使用刚注册分配的账号，其次使用"记住账号"
    const assigned = sessionStorage.getItem('assignedAccount');
    const remembered = localStorage.getItem('rememberAccount');
    if (assigned) {
      this.loginForm.account = assigned;
      sessionStorage.removeItem('assignedAccount');
      if (this.$message) this.$message.success('账号已自动填入，请输入密码登录');
    } else if (remembered) {
      this.loginForm.account = remembered;
    } else {
      this.rememberMe = false;
    }
  },
  methods: {
    // 后端ResultCodeEnum返回的是英文码，这里映射为友好提示
    friendlyMessage(code, fallback) {
      const map = {
        usernameError: '账号不存在，请检查账号 / 手机号 / 邮箱',
        passwordError: '密码错误，请重新输入',
        notLogin: '登录状态已过期，请重新登录',
        userNameUsed: '该账号已被注册'
      };
      return map[code] || fallback || '登录失败，请稍后重试';
    },
    onForget() {
      this.$message.info('请联系管理员重置密码，或登录后在个人中心修改密码');
    },
    submitLogin() {
      this.$refs.loginForm.validate((valid) => {
        if (!valid) return;
        this.loading = true;
        const account = this.loginForm.account.trim();
        this.$axios.post('/user/login', {
          account,
          password: this.loginForm.password
        })
          .then(res => {
            const data = res.data.data;
            if (!data) {
              this.$message.error('登录失败，请稍后重试');
              return;
            }
            if (data.role === "管理员" || data.role === "医生") {
              // 记住账号
              if (this.rememberMe) {
                localStorage.setItem('rememberAccount', account);
              } else {
                localStorage.removeItem('rememberAccount');
              }
              sessionStorage.setItem("token", data.token);
              sessionStorage.setItem("userInfo", JSON.stringify({
                role: data.role,
                userId: data.userId,
                nickname: data.nickname
              }));
              this.$message.success("登录成功");
              const target = data.role === "管理员" ? '/master/userinfo' : '/doctor/appointments';
              setTimeout(() => this.$router.push(target), 500);
            }
          })
          .catch(error => {
            const status = error.response?.status;
            const code = error.response?.data?.message;
            const msg = this.friendlyMessage(code, error.response?.data?.message || error.message);
            if (status === 403) {
              // 设备访问限制
              this.$message.warning(msg);
            } else {
              this.$message.error(msg);
            }
          })
          .finally(() => {
            this.loading = false;
          });
      });
    }
  }
};
</script>

<style scoped>
.login-form {
  width: 100%;
}

.option-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: -6px 0 6px;
}

.forget-link {
  font-size: 13px;
  color: #5B8C8A;
  cursor: pointer;
}

.forget-link:hover {
  color: #3D6A68;
  text-decoration: underline;
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
