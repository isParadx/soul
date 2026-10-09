<template>
  <div class="doc-info-page">
    <el-form
      ref="docInfoForm"
      :model="form"
      :rules="rules"
      label-position="top"
      class="doc-info-card"
    >
      <!-- 基本信息区 -->
      <div class="section">
        <div class="section-title"><span class="bar"></span>基本信息</div>
        <el-row :gutter="24">
          <el-col :xs="24" :md="12">
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="form.nickname" placeholder="请输入昵称" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="12">
            <el-form-item label="电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="12">
            <el-form-item label="性别" prop="sex">
              <el-select v-model="form.sex" placeholder="请选择性别" style="width: 100%;">
                <el-option label="男" :value="0" />
                <el-option label="女" :value="1" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
      </div>

      <!-- 执业信息区 -->
      <div class="section">
        <div class="section-title"><span class="bar"></span>执业信息</div>
        <el-form-item label="个人简介" prop="intruduce">
          <el-input
            type="textarea"
            v-model="form.intruduce"
            :autosize="{ minRows: 3, maxRows: 6 }"
            maxlength="200"
            show-word-limit
            placeholder="请输入个人简介"
          />
        </el-form-item>
        <el-form-item label="擅长领域" prop="type">
          <el-input
            type="textarea"
            v-model="form.type"
            :autosize="{ minRows: 2, maxRows: 5 }"
            maxlength="60"
            placeholder="请输入擅长领域"
          />
          <div class="field-tip">医生标签之间用逗号隔开，一个标签不超过七个字</div>
        </el-form-item>
        <el-form-item label="医师寄语（选填）" prop="say">
          <el-input
            type="textarea"
            v-model="form.say"
            :autosize="{ minRows: 2, maxRows: 5 }"
            maxlength="100"
            show-word-limit
            placeholder="请输入医师寄语"
          />
        </el-form-item>
      </div>

      <el-button
        type="primary"
        class="save-btn"
        :loading="saving"
        @click="saveAll"
      >{{ saving ? '正在保存...' : '保存修改' }}</el-button>
    </el-form>
  </div>
</template>

<script>
export default {
  name: 'DocInfo',
  data() {
    return {
      saving: false,
      userid: '',
      // 基本信息与执业信息合并为一个表单模型
      form: {
        userid: '',
        nickname: '',
        phone: '',
        sex: 0,
        email: '',
        id: '',
        intruduce: '',
        type: '',
        say: ''
      },
      rules: {
        nickname: [
          { required: true, message: '请输入昵称', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' }
        ],
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
  mounted() {
    this.loadAll();
  },
  methods: {
    async loadAll() {
      try {
        // 1. 先取当前登录用户（拿到 userid 后才能查询对应医生档案）
        const userRes = await this.$axios.get('/user/getUserInfo');
        const loginUser = (userRes.data.data && userRes.data.data.loginUser) || {};
        this.userid = loginUser.userid;
        this.form.userid = loginUser.userid;
        this.form.nickname = loginUser.nickname;
        this.form.phone = loginUser.phone;
        this.form.sex = loginUser.sex || 0;
        this.form.email = loginUser.email;

        // 2. 再按 userid 精确查询医生档案（避免竞态查出别人的数据）
        const docRes = await this.$axios.get('/doctor/getAllDocter', {
          params: { keywords: this.userid }
        });
        const docList = docRes.data.data;
        if (docList && docList.length > 0) {
          const doc = docList.find(d => String(d.id) === String(this.userid)) || docList[0];
          this.form.id = doc.id;
          this.form.intruduce = doc.intruduce || '';
          this.form.type = doc.type || '';
          this.form.say = doc.say || '';
        }
      } catch (error) {
        this.$message.error('加载个人信息失败，请刷新重试');
      }
    },
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
    saveAll() {
      this.$refs.docInfoForm.validate(async (valid) => {
        if (!valid) return;
        this.saving = true;
        try {
          // 1. 保存基本信息
          const userRes = await this.$axios.post('/user/changeUserInfo', {
            userid: this.form.userid,
            nickname: this.form.nickname,
            phone: this.form.phone,
            sex: this.form.sex,
            email: this.form.email
          });
          if (userRes.data.code !== 200) {
            this.$message.error(userRes.data.message || '基本信息保存失败');
            return;
          }
          // 2. 保存执业信息
          const docRes = await this.$axios.post('/doctor/changeDoc', {
            id: this.form.id || this.userid,
            intruduce: this.form.intruduce,
            type: this.form.type.split(/[,，]/).map(t => t.trim()).join(','),
            say: this.form.say,
            name: this.form.nickname
          });
          if (docRes.data.code !== 200) {
            this.$message.error(docRes.data.message || '执业信息保存失败');
            return;
          }
          this.$message.success('修改成功！');
        } catch (error) {
          const msg = error.response && error.response.data && error.response.data.message;
          this.$message.error(msg || '保存失败，请稍后重试');
        } finally {
          this.saving = false;
        }
      });
    }
  }
};
</script>

<style scoped>
.doc-info-page {
  padding: 4px;
}

.doc-info-card {
  max-width: 960px;
  margin: 0 auto;
  background: #fff;
  border-radius: 10px;
  padding: 28px 32px 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  box-sizing: border-box;
}

.section {
  margin-bottom: 8px;
}

.section-title {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 8px 0 18px;
}

.section-title .bar {
  display: inline-block;
  width: 4px;
  height: 16px;
  border-radius: 2px;
  background: #409eff;
  margin-right: 8px;
}

.field-tip {
  font-size: 12px;
  color: #909399;
  line-height: 1.6;
  margin-top: 4px;
}

.save-btn {
  width: 100%;
  height: 42px;
  font-size: 15px;
  border-radius: 8px;
  margin-top: 8px;
}
</style>
