<template>
  <div class="write-info-page">
    <div class="info-card">
      <div class="card-header">
        <img class="header-logo" :src="logoImg" alt="心灵驿站" />
        <h2 class="card-title">完善执业信息</h2>
        <p class="card-subtitle">
          您的账号 <b class="account">{{ assignedAccount || '—' }}</b> 已分配成功，完善以下信息后即可登录使用
        </p>
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
          <div class="type-tip">点击下方标签快速添加，点击「＋」可自定义（最多 {{ MAX_TAGS }} 个，单个不超过 7 字）</div>

          <!-- 系统默认标签 -->
          <div class="tag-group-title">系统推荐</div>
          <div class="tag-group">
            <span
              v-for="tag in defaultTags"
              :key="tag"
              class="tag-chip"
              :class="{ 'is-active': selectedTags.includes(tag) }"
              @click="toggleTag(tag)"
            >{{ tag }}</span>
            <span
              class="tag-chip tag-add"
              title="添加自定义标签"
              @click="openTagDialog"
            >＋</span>
          </div>

          <!-- 已选标签 -->
          <div class="tag-group-title">已选择 <span class="count">({{ selectedTags.length }}/{{ MAX_TAGS }})</span></div>
          <div class="tag-group selected-group">
            <el-tag
              v-for="tag in selectedTags"
              :key="tag"
              closable
              type="success"
              effect="light"
              @close="removeTag(tag)"
            >{{ tag }}</el-tag>
            <span v-if="!selectedTags.length" class="empty-tip">尚未选择标签</span>
          </div>
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

    <!-- 自定义标签弹窗 -->
    <el-dialog
      v-model="tagDialogVisible"
      title="添加自定义标签"
      width="360px"
      :append-to-body="true"
    >
      <el-input
        ref="tagInput"
        v-model="customTag"
        maxlength="7"
        show-word-limit
        placeholder="请输入标签内容（最多 7 个字）"
        @keyup.enter="confirmAddTag"
      />
      <template #footer>
        <el-button @click="tagDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAddTag">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import logoImg from '@/assets/img/logo.png';

export default {
  name: 'WriteInfo',
  data() {
    return {
      logoImg,
      MAX_TAGS: 6,
      submitting: false,
      assignedAccount: '',
      nickname: '',
      customTag: '',
      tagDialogVisible: false,
      defaultTags: [],
      selectedTags: [],
      doctorForm: {
        intruduce: '',
        say: ''
      },
      rules: {
        intruduce: [
          { required: true, message: '请输入个人简介', trigger: 'blur' }
        ],
        type: [
          {
            validator: (rule, value, callback) => {
              if (!this.selectedTags.length) {
                return callback(new Error('请至少选择或添加一个擅长领域标签'));
              }
              callback();
            },
            trigger: 'change'
          }
        ]
      }
    };
  },
  created() {
    this.assignedAccount = sessionStorage.getItem('assignedAccount') || '';
    this.nickname = sessionStorage.getItem('registerNickname') || '';
    this.loadDefaultTags();
  },
  methods: {
    async loadDefaultTags() {
      try {
        const res = await this.$axios.get('/tag/list');
        this.defaultTags = (res.data.data || []).map(t => t.name);
      } catch (e) {
        this.defaultTags = ['情绪管理', '人际关系', '学业压力', '焦虑抑郁', '恋爱心理', '亲子关系', '职业规划', '睡眠问题', '自我成长'];
      }
    },
    toggleTag(tag) {
      if (this.selectedTags.includes(tag)) {
        this.removeTag(tag);
        return;
      }
      if (this.selectedTags.length >= this.MAX_TAGS) {
        this.$message.warning(`最多选择 ${this.MAX_TAGS} 个标签`);
        return;
      }
      this.selectedTags.push(tag);
      this.$refs.doctorForm.validateField('type');
    },
    openTagDialog() {
      if (this.selectedTags.length >= this.MAX_TAGS) {
        this.$message.warning(`最多选择 ${this.MAX_TAGS} 个标签`);
        return;
      }
      this.customTag = '';
      this.tagDialogVisible = true;
      this.$nextTick(() => {
        this.$refs.tagInput && this.$refs.tagInput.focus();
      });
    },
    confirmAddTag() {
      const tag = (this.customTag || '').trim();
      if (!tag) {
        this.$message.warning('请输入标签内容');
        return;
      }
      if (tag.length > 7) {
        this.$message.warning('单个标签不超过七个字');
        return;
      }
      if (this.selectedTags.includes(tag)) {
        this.$message.info('该标签已添加');
        this.customTag = '';
        return;
      }
      if (this.selectedTags.length >= this.MAX_TAGS) {
        this.$message.warning(`最多选择 ${this.MAX_TAGS} 个标签`);
        return;
      }
      this.selectedTags.push(tag);
      this.customTag = '';
      this.tagDialogVisible = false;
      this.$refs.doctorForm.validateField('type');
    },
    removeTag(tag) {
      this.selectedTags = this.selectedTags.filter(t => t !== tag);
    },
    submitForm() {
      this.$refs.doctorForm.validate((valid) => {
        if (valid) {
          this.submitData();
        }
      });
    },
    async submitData() {
      this.submitting = true;
      try {
        const res = await this.$axios.post('/doctor/addDoctorInfo', {
          id: this.assignedAccount,
          name: this.nickname,
          intruduce: this.doctorForm.intruduce,
          type: this.selectedTags.join(','),
          say: this.doctorForm.say
        });
        if (res.data.code !== 200) {
          this.$message.error(res.data.message || '保存失败，请稍后重试');
          return;
        }
        this.$message.success('注册完成，请使用您的账号登录！');
        sessionStorage.removeItem('registerNickname');
        setTimeout(() => this.$router.push('/login'), 600);
      } catch (error) {
        const msg = error.response && error.response.data && error.response.data.message;
        this.$message.error(msg || '保存失败，请稍后重试');
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
  background: linear-gradient(160deg, #e8f6f4 0%, #f5f7fa 60%, #eaf2f8 100%);
  box-sizing: border-box;
}

.info-card {
  width: 100%;
  max-width: 600px;
  background: #fff;
  border-radius: 12px;
  padding: 36px 40px 28px;
  box-shadow: 0 8px 30px rgba(91, 140, 138, 0.14);
  box-sizing: border-box;
}

.card-header {
  text-align: center;
  margin-bottom: 24px;
}

.header-logo {
  display: block;
  width: 64px;
  height: 64px;
  object-fit: contain;
  margin: 0 auto 8px;
  border-radius: 14px;
}

.card-title {
  font-size: 22px;
  color: #33413F;
  margin: 0 0 8px;
}

.card-subtitle {
  font-size: 13px;
  color: #7A8A88;
  margin: 0;
  line-height: 1.7;
}

.account {
  color: #5B8C8A;
  font-size: 15px;
  letter-spacing: 1px;
}

.type-tip {
  font-size: 12px;
  color: #9AA8A6;
  line-height: 1.6;
  margin-top: 4px;
}

.tag-group-title {
  font-size: 13px;
  color: #5A6A68;
  margin: 14px 0 8px;
}

.tag-group-title .count {
  color: #9AA8A6;
  font-weight: 400;
}

.tag-group {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-chip {
  display: inline-block;
  padding: 5px 12px;
  border-radius: 14px;
  font-size: 13px;
  color: #5A6A68;
  background: #F2F7F6;
  border: 1px solid #D8E6E4;
  cursor: pointer;
  user-select: none;
  transition: all 0.2s;
}

.tag-chip:hover {
  border-color: #5B8C8A;
  color: #3D6A68;
}

.tag-chip.is-active {
  background: #5B8C8A;
  border-color: #5B8C8A;
  color: #fff;
}

.tag-chip.tag-add {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 52px;
  padding: 5px 14px;
  border-style: dashed;
  border-color: #B9CFCC;
  color: #5B8C8A;
  font-weight: 600;
}

.tag-chip.tag-add:hover {
  border-color: #5B8C8A;
  background: #EAF3F2;
}

.selected-group {
  min-height: 32px;
  align-items: center;
}

.empty-tip {
  font-size: 12px;
  color: #B0BCBA;
}

.submit-button {
  width: 100%;
  height: 42px;
  font-size: 15px;
  border-radius: 8px;
  background-color: #5B8C8A;
  border-color: #5B8C8A;
}

.submit-button:hover {
  background-color: #4A7A78;
  border-color: #4A7A78;
}

:deep(.el-form-item__label) {
  color: #5A6A68;
  font-size: 13px;
  font-weight: 500;
}
</style>
