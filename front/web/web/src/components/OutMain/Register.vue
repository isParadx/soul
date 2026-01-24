<template>
  <div class="register-container">
    <el-form ref="registerForm" :model="registerForm" label-width="100px" class="register-form">
      <el-form-item label="账号">
        <el-input v-model="registerForm.userid"></el-input>
      </el-form-item>
      <el-form-item label="昵称">
        <el-input v-model="registerForm.nickname"></el-input>
      </el-form-item>
      <el-form-item label="密码">
        <el-input type="password" v-model="registerForm.password"></el-input>
      </el-form-item>
      <el-form-item label="重复密码">
        <el-input type="password" v-model="registerForm.repeatPassword"></el-input>
      </el-form-item>
      <el-form-item label="性别">
        <el-radio-group v-model="registerForm.sex">
          <el-radio value="0">男</el-radio>
          <el-radio value="1">女</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="角色">
        <el-select v-model="registerForm.role">
          <el-option label="医生" value="1"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="手机" prop="phone">
        <el-input v-model="registerForm.phone" placeholder="选填"></el-input>
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="registerForm.email" placeholder="选填"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitRegister" class="register-button">注册</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
export default {
  data() {
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
        userid: [{ required: true, message: '请输入账号', trigger: 'blur' }],
        nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        repeatPassword: [
          { required: true, message: '请再次输入密码', trigger: 'blur' },
          { validator: (rule, value, callback) => {
            if (value !== this.registerForm.password) {
              callback(new Error('两次输入密码不一致'));
            } else {
              callback();
            }
          }, trigger: 'blur' }
        ],
        sex: [{ required: true, message: '请选择性别', trigger: 'change' }],
        role: [{ required: true, message: '请选择角色', trigger: 'change' }]
      }
    };
  },
  methods: {
    submitRegister() {
      this.$refs.registerForm.validate((valid) => {
        if (valid) {
          // 过滤空值和null值
          const filteredForm = Object.keys(this.registerForm)
            .filter(key => this.registerForm[key] !== '' && this.registerForm[key] != null)
            .reduce((obj, key) => {
              obj[key] = this.registerForm[key];
              return obj;
            }, {});
              //将数据暂时存入本地
              sessionStorage.setItem('userForm', JSON.stringify(this.registerForm));
              this.$router.push('/writeinfo');
        
        } else {
          this.$message.error('请检查表单填写是否完整');
        }
      });
    }
  }
};
</script>

<style scoped>
.register-container {
  width: 300px;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f5f5;
}

.register-form {
  width: 400px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  background-color: #fff;
}

.register-button {
  width: 100%;
}
</style>