<template>
  <div class="register-form">
    <el-form ref="registerForm" :model="registerForm" :rules="rules" label-position="top" size="large">
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="账号" prop="userid">
            <el-input v-model="registerForm.userid" placeholder="设置登录账号" :prefix-icon="User" clearable />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="昵称" prop="nickname">
            <el-input v-model="registerForm.nickname" placeholder="您的称呼" :prefix-icon="Avatar" clearable />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="密码" prop="password">
            <el-input type="password" v-model="registerForm.password" placeholder="设置密码" :prefix-icon="Lock" show-password />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="确认密码" prop="repeatPassword">
            <el-input type="password" v-model="registerForm.repeatPassword" placeholder="再次输入密码" :prefix-icon="Lock" show-password />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="性别" prop="sex">
            <el-radio-group v-model="registerForm.sex" class="sex-group">
              <el-radio value="0">男</el-radio>
              <el-radio value="1">女</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="注册角色" prop="role">
            <el-select v-model="registerForm.role" placeholder="选择角色" style="width: 100%;">
              <el-option label="医生" value="1"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="手机号（选填）" prop="phone">
        <el-input v-model="registerForm.phone" placeholder="用于接收通知" :prefix-icon="Phone" />
      </el-form-item>

      <el-form-item label="邮箱（选填）" prop="email">
        <el-input v-model="registerForm.email" placeholder="用于找回密码" :prefix-icon="Message" />
      </el-form-item>

      <el-form-item class="button-area">
        <el-button type="primary" @click="submitRegister" class="register-btn">注 册</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { User, Avatar, Lock, Phone, Message } from '@element-plus/icons-vue'

export default {
  name: 'RegisterForm',
  components: {
    User,
    Avatar,
    Lock,
    Phone,
    Message
  },
  data() {
    const validatePass = (rule, value, callback) => {
      if (value !== this.registerForm.password) {
        callback(new Error('两次输入的密码不一致'));
      } else {
        callback();
      }
    };
    
    return {
      registerForm: {
        userid: '',
        nickname: '',
        password: '',
        repeatPassword: '',
        sex: '',
        role: '1',
        phone: '',
        email: ''
      },
      rules: {
        userid: [
          { required: true, message: '请输入账号', trigger: 'blur' },
          { min: 3, max: 20, message: '账号长度为3-20个字符', trigger: 'blur' }
        ],
        nickname: [
          { required: true, message: '请输入昵称', trigger: 'blur' },
          { min: 2, max: 16, message: '昵称长度为2-16个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 30, message: '密码长度为6-30个字符', trigger: 'blur' }
        ],
        repeatPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          { validator: validatePass, trigger: 'blur' }
        ],
        sex: [{ required: true, message: '请选择性别', trigger: 'change' }],
        role: [{ required: true, message: '请选择角色', trigger: 'change' }],
        phone: [
          { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
        ],
        email: [
          { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
        ]
      }
    };
  },
  methods: {
    submitRegister() {
      this.$refs.registerForm.validate((valid) => {
        if (valid) {
          const filteredForm = Object.keys(this.registerForm)
            .filter(key => this.registerForm[key] !== '' && this.registerForm[key] != null)
            .reduce((obj, key) => {
              obj[key] = this.registerForm[key];
              return obj;
            }, {});
          sessionStorage.setItem('userForm', JSON.stringify(this.registerForm));
          this.$router.push('/writeinfo');
        } else {
          this.$message.warning('请检查表单填写是否完整');
        }
      });
    }
  }
};
</script>

<style scoped>
.register-form {
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

.sex-group {
  display: flex;
  gap: 24px;
  padding-top: 4px;
}

:deep(.sex-group .el-radio__label) {
  color: #5A6A68;
}

.button-area {
  margin-top: 24px;
  margin-bottom: 0;
}

.register-btn {
  width: 100%;
  height: 42px;
  font-size: 15px;
  border-radius: 6px;
  background-color: #5B8C8A;
  border-color: #5B8C8A;
  letter-spacing: 4px;
  transition: background-color 0.2s;
}

.register-btn:hover {
  background-color: #4A7A78;
  border-color: #4A7A78;
}

.register-btn:active {
  background-color: #3D6A68;
  border-color: #3D6A68;
}
</style>
