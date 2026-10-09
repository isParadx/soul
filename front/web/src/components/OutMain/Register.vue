<template>
  <div class="register-form">
    <!-- 第一步：填写注册信息 -->
    <template v-if="step === 'form'">
      <el-form ref="registerForm" :model="registerForm" :rules="rules" label-position="top" size="large">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="registerForm.nickname" placeholder="您的称呼" :prefix-icon="Avatar" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="sex">
              <el-radio-group v-model="registerForm.sex" class="sex-group">
                <el-radio value="0">男</el-radio>
                <el-radio value="1">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item prop="phone">
          <template #label>
            手机号
            <span class="label-tip">（必填，用于登录与联系）</span>
          </template>
          <el-input
            v-model="registerForm.phone"
            placeholder="请输入11位手机号"
            :prefix-icon="Phone"
            maxlength="11"
            clearable
          />
        </el-form-item>

        <el-form-item prop="email">
          <template #label>
            邮箱
            <span class="label-tip">（选填，可用于登录）</span>
          </template>
          <el-input
            v-model="registerForm.email"
            placeholder="请输入邮箱"
            :prefix-icon="Message"
            clearable
          />
        </el-form-item>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="密码" prop="password">
              <el-input type="password" v-model="registerForm.password" placeholder="6-20位，含字母和数字" :prefix-icon="Lock" show-password />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="确认密码" prop="repeatPassword">
              <el-input type="password" v-model="registerForm.repeatPassword" placeholder="再次输入密码" :prefix-icon="Lock" show-password />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item class="button-area">
          <el-button type="primary" @click="submitRegister" class="register-btn" :loading="submitting">
            {{ submitting ? '注册中...' : '注 册' }}
          </el-button>
        </el-form-item>

        <div class="role-hint">当前注册角色：<b>医生</b>（学生请使用手机 App 注册）</div>
      </el-form>
    </template>

    <!-- 第二步：展示系统分配的账号 -->
    <template v-else>
      <div class="success-panel">
        <el-icon class="success-icon"><CircleCheckFilled /></el-icon>
        <div class="success-title">注册成功</div>
        <div class="success-sub">系统已为您分配账号，请牢记（用于登录）</div>

        <div class="account-box">
          <span class="account-value">{{ assignedAccount }}</span>
          <el-button link type="primary" @click="copyAccount">
            <el-icon><CopyDocument /></el-icon>复制
          </el-button>
        </div>

        <div class="next-tip">下一步：完善执业信息（个人简介、擅长领域等），方便学生选择您</div>

        <el-button type="primary" class="register-btn" @click="goWriteInfo">完善执业信息</el-button>
        <el-button link class="skip-link" @click="goLogin">稍后完善，先去登录</el-button>
      </div>
    </template>
  </div>
</template>

<script>
import { User, Avatar, Lock, Phone, Message, CircleCheckFilled, CopyDocument } from '@element-plus/icons-vue'

export default {
  name: 'RegisterForm',
  components: {
    User,
    Avatar,
    Lock,
    Phone,
    Message,
    CircleCheckFilled,
    CopyDocument
  },
  data() {
    const validatePass = (rule, value, callback) => {
      if (value !== this.registerForm.password) {
        callback(new Error('两次输入的密码不一致'));
      } else {
        callback();
      }
    };
    // 与后端ValidationUtil.isValidPassword保持一致：6-20位，必须同时包含字母和数字
    const validatePassword = (rule, value, callback) => {
      if (!value) {
        return callback(new Error('请输入密码'));
      }
      if (value.length < 6 || value.length > 20) {
        return callback(new Error('密码长度为6-20位'));
      }
      if (!/[A-Za-z]/.test(value) || !/\d/.test(value)) {
        return callback(new Error('密码需同时包含字母和数字'));
      }
      callback();
    };

    // 手机号/邮箱唯一性异步校验（失焦即时提示，提交时同样会拦截）
    const validatePhoneUnique = (rule, value, callback) => {
      if (!value || !/^1[3-9]\d{9}$/.test(value)) return callback();
      this.$axios.get('/user/checkUnique', { params: { field: 'phone', value } })
        .then(res => {
          const available = res.data.data && res.data.data.available;
          callback(available === false ? new Error('该手机号已被注册') : undefined);
        })
        .catch(() => callback());
    };
    const validateEmailUnique = (rule, value, callback) => {
      if (!value) return callback();
      if (!/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(value)) return callback();
      this.$axios.get('/user/checkUnique', { params: { field: 'email', value } })
        .then(res => {
          const available = res.data.data && res.data.data.available;
          callback(available === false ? new Error('该邮箱已被注册') : undefined);
        })
        .catch(() => callback());
    };

    return {
      step: 'form',
      submitting: false,
      assignedAccount: '',
      registerForm: {
        nickname: '',
        password: '',
        repeatPassword: '',
        sex: '',
        role: 1,          // Web 端固定注册医生
        phone: '',
        email: ''
      },
      rules: {
        nickname: [
          { required: true, message: '请输入昵称', trigger: 'blur' },
          { pattern: /^[\u4e00-\u9fa5a-zA-Z0-9_]{2,20}$/, message: '昵称为2-20位，支持中英文、数字、下划线', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { validator: validatePassword, trigger: 'blur' }
        ],
        repeatPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          { validator: validatePass, trigger: 'blur' }
        ],
        sex: [{ required: true, message: '请选择性别', trigger: 'change' }],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' },
          { validator: validatePhoneUnique, trigger: 'blur' }
        ],
        email: [
          { type: 'email', message: '邮箱格式不正确', trigger: 'blur' },
          { validator: validateEmailUnique, trigger: 'blur' }
        ]
      }
    };
  },
  methods: {
    submitRegister() {
      this.$refs.registerForm.validate(async (valid, invalidFields) => {
        if (!valid) {
          const firstField = invalidFields && Object.keys(invalidFields)[0];
          const firstMsg = firstField ? invalidFields[firstField][0].message : null;
          this.$message.warning(firstMsg || '请检查表单填写是否完整');
          return;
        }
        this.submitting = true;
        try {
          const payload = {
            nickname: this.registerForm.nickname,
            password: this.registerForm.password,
            sex: Number(this.registerForm.sex),
            role: 1,
            phone: this.registerForm.phone,
            email: this.registerForm.email || null
          };
          const res = await this.$axios.post('/user/regist', payload);
          if (res.data.code !== 200) {
            this.$message.error(res.data.message || '注册失败，请检查填写内容');
            return;
          }
          const assigned = res.data.data && res.data.data.userid;
          this.assignedAccount = String(assigned);
          sessionStorage.setItem('assignedAccount', this.assignedAccount);
          sessionStorage.setItem('registerNickname', this.registerForm.nickname);
          this.step = 'done';
        } catch (error) {
          const msg = error.response && error.response.data && error.response.data.message;
          this.$message.error(msg || '注册失败，请稍后重试');
        } finally {
          this.submitting = false;
        }
      });
    },
    async copyAccount() {
      try {
        await navigator.clipboard.writeText(this.assignedAccount);
        this.$message.success('账号已复制');
      } catch (e) {
        this.$message.info('请手动记录账号：' + this.assignedAccount);
      }
    },
    goWriteInfo() {
      this.$router.push('/writeinfo');
    },
    goLogin() {
      this.$router.push('/login');
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

.label-tip {
  font-size: 12px;
  color: #9AA8A6;
  font-weight: 400;
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

.role-hint {
  margin-top: 14px;
  text-align: center;
  font-size: 12px;
  color: #9AA8A6;
}

.success-panel {
  text-align: center;
  padding: 12px 8px 4px;
}

.success-icon {
  font-size: 56px;
  color: #5B8C8A;
}

.success-title {
  font-size: 20px;
  color: #33413F;
  font-weight: 600;
  margin: 12px 0 6px;
}

.success-sub {
  font-size: 13px;
  color: #7A8A88;
  margin-bottom: 18px;
}

.account-box {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  background: #F2F7F6;
  border: 1px dashed #A9C4C2;
  border-radius: 8px;
  padding: 14px;
  margin-bottom: 16px;
}

.account-value {
  font-size: 22px;
  font-weight: 700;
  color: #33413F;
  letter-spacing: 2px;
}

.next-tip {
  font-size: 12px;
  color: #9AA8A6;
  line-height: 1.7;
  margin-bottom: 18px;
}

.skip-link {
  margin-top: 10px;
  color: #9AA8A6;
}
</style>
