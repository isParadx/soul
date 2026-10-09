<template>
  <div class="write-info-page">
    <div class="info-card">
      <div class="card-header">
        <div class="header-icon">🩺</div>
        <h2 class="card-title">完善医生执业信息</h2>
        <p class="card-subtitle">为了方便学生更好地选择您，请完善以下内容</p>
      </div>

      <el-form
        ref="doctorForm"
        :model="doctorForm"
        :rules="rules"
        label-position="top"
        class="info-form"
      >
        <el-form-item label="个人简介" prop="intruduce">
          <el-input
            type="textarea"
            v-model="doctorForm.intruduce"
            :autosize="{ minRows: 4, maxRows: 6 }"
            maxlength="200"
            show-word-limit
            placeholder="请输入个人简介，如：从业年限、咨询风格、资质证书等"
          ></el-input>
        </el-form-item>

        <el-form-item label="擅长领域" prop="type">
          <el-input
            type="textarea"
            v-model="doctorForm.type"
            :autosize="{ minRows: 3, maxRows: 5 }"
            maxlength="60"
            placeholder="请输入擅长领域"
          ></el-input>
          <div class="type-tip">多个标签之间用逗号隔开，单个标签不超过七个字，如：情绪管理,人际交往,学业压力</div>
        </el-form-item>

        <el-form-item label="医师寄语（选填）" prop="say">
          <el-input
            type="textarea"
            v-model="doctorForm.say"
            :autosize="{ minRows: 3, maxRows: 5 }"
            maxlength="100"
            show-word-limit
            placeholder="写一句想对学生说的话吧"
          ></el-input>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            class="submit-button"
            :loading="submitting"
            @click="submitForm"
          >{{ submitting ? '正在提交...' : '完成注册' }}</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  name: 'WriteInfo',
  data() {
    return {
      submitting: false,
      doctorForm: {
        intruduce: '',
        type: '',
        say: ''
      },
      userForm: {
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
        intruduce: [
          { required: true, message: '请输入个人简介', trigger: 'blur' }
        ],
        type: [
          { required: true, message: '请输入擅长领域', trigger: 'blur' },
          { validator: this.validateSpecialty, trigger: 'blur' }
        ]
      }
    };
  },
  created() {
    const storedUserForm = sessionStorage.getItem('userForm');
    if (storedUserForm) {
      this.userForm = JSON.parse(storedUserForm);
    }
  },
  methods: {
    validateSpecialty(rule, value, callback) {
      if (!value) {
        return callback(new Error('擅长领域不能为空'));
      }
      const tags = value.split(/[,，]/);
      const isInvalid = tags.some(tag => tag.length > 7 || !tag.trim());
      if (isInvalid) {
        return callback(new Error('每个标签不超过七个字且不能为空'));
      }
      return callback();
    },
    submitForm() {
      this.$refs.doctorForm.validate((valid) => {
        if (valid) {
          this.submitData();
        }
        return valid;
      });
    },
    // 组装注册请求体：剔除前端专用字段，统一数值类型
    buildRegistPayload() {
      return {
        userid: this.userForm.userid,
        nickname: this.userForm.nickname,
        password: this.userForm.password,
        sex: this.userForm.sex === '' ? null : Number(this.userForm.sex),
        role: Number(this.userForm.role) || 1,
        phone: this.userForm.phone,
        email: this.userForm.email
      };
    },
    async submitData() {
      this.submitting = true;
      try {
        // 1. 先注册用户账号（账号已存在等错误在此步明确提示）
        const registRes = await this.$axios.post('/user/regist', this.buildRegistPayload());
        if (registRes.data.code !== 200) {
          this.$message.error(registRes.data.message || '注册失败，请检查填写内容');
          return;
        }

        // 2. 再补充医生档案（支持中英文逗号分隔的标签）
        const docRes = await this.$axios.post('/doctor/addDoctorInfo', {
          id: this.userForm.userid,
          intruduce: this.doctorForm.intruduce,
          say: this.doctorForm.say,
          name: this.userForm.nickname,
          type: this.doctorForm.type.split(/[,，]/).map(t => t.trim()).join(',')
        });
        if (docRes.data.code !== 200) {
          this.$message.warning('账号注册成功，但医生信息保存失败：' + (docRes.data.message || '未知原因') + '。可登录后在"个人信息"中补充');
        } else {
          this.$message.success('注册成功，请登录！');
        }
        sessionStorage.clear();
        this.$router.push('/');
      } catch (error) {
        const msg = error.response && error.response.data && error.response.data.message;
        this.$message.error(msg || '注册失败，请稍后重试');
      } finally {
        this.submitting = false;
      }
    }
  }
};
</script>

<style scoped>
.write-info-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 16px;
  background: linear-gradient(160deg, #e8f1fd 0%, #f5f7fa 60%, #e6f7f1 100%);
  box-sizing: border-box;
}

.info-card {
  width: 100%;
  max-width: 560px;
  background: #fff;
  border-radius: 12px;
  padding: 36px 40px 28px;
  box-shadow: 0 8px 30px rgba(64, 128, 255, 0.12);
  box-sizing: border-box;
}

.card-header {
  text-align: center;
  margin-bottom: 24px;
}

.header-icon {
  font-size: 40px;
  margin-bottom: 8px;
}

.card-title {
  font-size: 22px;
  color: #303133;
  margin: 0 0 6px;
}

.card-subtitle {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.type-tip {
  font-size: 12px;
  color: #909399;
  line-height: 1.6;
  margin-top: 4px;
}

.submit-button {
  width: 100%;
  height: 42px;
  font-size: 15px;
  border-radius: 8px;
}
</style>
