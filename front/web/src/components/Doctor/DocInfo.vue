<template>
  <div class="doc-info-page">
    <!-- 顶部个人信息头图 -->
    <div class="profile-hero">
      <div class="hero-bg"></div>
      <div class="hero-body">
        <div class="avatar-wrap" @click="triggerAvatarUpload">
          <el-avatar :size="72" :src="avatarUrl" class="hero-avatar">{{ avatarText }}</el-avatar>
          <div class="avatar-mask">更换</div>
          <input ref="avatarInput" type="file" accept="image/*" class="hidden-input" @change="handleAvatarChange" />
        </div>
        <div class="hero-info">
          <div class="hero-name">
            {{ form.nickname || '未设置昵称' }}
            <el-tag size="small" type="success" effect="plain">医生</el-tag>
          </div>
          <div class="hero-account">账号：{{ account }}</div>
        </div>
      </div>
    </div>

    <el-form
      ref="docInfoForm"
      :model="form"
      :rules="rules"
      label-position="top"
      class="doc-info-card"
    >
      <!-- 基本信息 -->
      <div class="section">
        <div class="section-title"><span class="bar"></span>基本信息</div>
        <el-row :gutter="24">
          <el-col :xs="24" :md="12">
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="form.nickname" placeholder="请输入昵称" />
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
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" maxlength="11" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
      </div>

      <!-- 执业信息 -->
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
            v-model="customTag"
            placeholder="输入自定义标签后回车添加"
            maxlength="7"
            @keyup.enter="addCustomTag"
          >
            <template #append>
              <el-button @click="addCustomTag">添加</el-button>
            </template>
          </el-input>
          <div class="field-tip">点击系统推荐标签快速添加，也可自定义（最多 {{ MAX_TAGS }} 个，单个不超过 7 字）</div>

          <div class="tag-group-title">系统推荐</div>
          <div class="tag-group">
            <span
              v-for="tag in defaultTags"
              :key="tag"
              class="tag-chip"
              :class="{ 'is-active': selectedTags.includes(tag) }"
              @click="toggleTag(tag)"
            >{{ tag }}</span>
          </div>

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
      MAX_TAGS: 6,
      saving: false,
      uploading: false,
      account: '',
      img: '',
      customTag: '',
      defaultTags: [],
      selectedTags: [],
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
          { required: true, message: '请输入昵称', trigger: 'blur' },
          { pattern: /^[\u4e00-\u9fa5a-zA-Z0-9_]{2,20}$/, message: '昵称为2-20位，支持中英文、数字、下划线', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
        ],
        email: [
          { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
        ],
        intruduce: [
          { required: true, message: '请输入个人简介', trigger: 'blur' }
        ],
        type: [
          {
            validator: (rule, value, callback) => {
              if (!this.selectedTags.length) {
                return callback(new Error('请至少选择一个擅长领域标签'));
              }
              callback();
            },
            trigger: 'change'
          }
        ]
      }
    };
  },
  computed: {
    avatarUrl() {
      return this.img ? this.resolveUrl(this.img) : '';
    },
    avatarText() {
      return this.form.nickname ? this.form.nickname.charAt(0) : '医';
    }
  },
  mounted() {
    this.loadAll();
    this.loadDefaultTags();
  },
  methods: {
    resolveUrl(path) {
      if (!path) return '';
      if (/^https?:\/\//.test(path)) return path;
      return `http://localhost:8080${path}`;
    },
    async loadDefaultTags() {
      try {
        const res = await this.$axios.get('/tag/list');
        this.defaultTags = (res.data.data || []).map(t => t.name);
      } catch (e) {
        this.defaultTags = ['情绪管理', '人际关系', '学业压力', '焦虑抑郁', '恋爱心理', '亲子关系', '职业规划', '睡眠问题', '自我成长'];
      }
    },
    async loadAll() {
      try {
        // 1. 取当前登录用户（拿到账号后才能查询对应医生档案）
        const userRes = await this.$axios.get('/user/getUserInfo');
        const loginUser = (userRes.data.data && userRes.data.data.loginUser) || {};
        this.account = loginUser.userid;
        this.img = loginUser.img || '';
        this.form.userid = loginUser.userid;
        this.form.nickname = loginUser.nickname;
        this.form.phone = loginUser.phone;
        this.form.sex = loginUser.sex === null || loginUser.sex === undefined ? 0 : loginUser.sex;
        this.form.email = loginUser.email || '';
        this.form.id = loginUser.userid;

        // 2. 按账号精确查询医生档案
        const docRes = await this.$axios.get('/doctor/getAllDocter', {
          params: { keywords: this.account }
        });
        const docList = docRes.data.data;
        if (docList && docList.length > 0) {
          const doc = docList.find(d => String(d.id) === String(this.account)) || docList[0];
          this.form.id = doc.id;
          this.form.intruduce = doc.intruduce || '';
          this.form.say = doc.say || '';
          this.selectedTags = doc.type ? doc.type.split(/[,，]/).map(t => t.trim()).filter(Boolean) : [];
        }
      } catch (error) {
        this.$message.error('加载个人信息失败，请刷新重试');
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
      this.$refs.docInfoForm.validateField('type');
    },
    addCustomTag() {
      const tag = (this.customTag || '').trim();
      if (!tag) return;
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
      this.$refs.docInfoForm.validateField('type');
    },
    removeTag(tag) {
      this.selectedTags = this.selectedTags.filter(t => t !== tag);
    },
    triggerAvatarUpload() {
      this.$refs.avatarInput && this.$refs.avatarInput.click();
    },
    async handleAvatarChange(e) {
      const file = e.target.files && e.target.files[0];
      if (!file) return;
      if (!/^image\/(jpeg|png|gif|webp)$/.test(file.type)) {
        this.$message.warning('仅支持 JPG / PNG / GIF / WebP 格式');
        e.target.value = '';
        return;
      }
      if (file.size > 5 * 1024 * 1024) {
        this.$message.warning('图片大小不能超过 5MB');
        e.target.value = '';
        return;
      }
      const formData = new FormData();
      formData.append('file', file);
      try {
        const res = await this.$axios.post('/user/uploadAvatar', formData, {
          headers: { 'Content-Type': 'multipart/form-data' }
        });
        if (res.data.code === 200) {
          const d = res.data.data || {};
          this.img = d.imgUrl || d.url || this.img;
          this.$message.success('头像已更新');
        } else {
          this.$message.error(res.data.message || '头像上传失败');
        }
      } catch (error) {
        const msg = error.response && error.response.data && error.response.data.message;
        this.$message.error(msg || '头像上传失败');
      } finally {
        e.target.value = '';
      }
    },
    saveAll() {
      this.$refs.docInfoForm.validate(async (valid) => {
        if (!valid) return;
        this.saving = true;
        try {
          // 1. 保存基本信息
          const userRes = await this.$axios.post('/user/changeUserInfo', {
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
            id: this.form.id || this.account,
            intruduce: this.form.intruduce,
            type: this.selectedTags.join(','),
            say: this.form.say,
            name: this.form.nickname
          });
          if (docRes.data.code !== 200) {
            this.$message.error(docRes.data.message || '执业信息保存失败');
            return;
          }
          // 同步会话缓存中的昵称
          const info = sessionStorage.getItem('userInfo');
          if (info) {
            try {
              const parsed = JSON.parse(info);
              parsed.nickname = this.form.nickname;
              sessionStorage.setItem('userInfo', JSON.stringify(parsed));
            } catch (e) { /* ignore */ }
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

/* 头图区域 */
.profile-hero {
  position: relative;
  max-width: 960px;
  margin: 0 auto 16px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.hero-bg {
  height: 96px;
  background: linear-gradient(120deg, #5B8C8A 0%, #7FB3B0 60%, #A8CFCC 100%);
}

.hero-body {
  background: #fff;
  padding: 0 32px 20px;
  display: flex;
  align-items: flex-end;
  gap: 20px;
}

.avatar-wrap {
  position: relative;
  margin-top: -36px;
  cursor: pointer;
  border-radius: 50%;
  overflow: hidden;
  border: 4px solid #fff;
  width: 80px;
  height: 80px;
}

.hero-avatar {
  background: #5B8C8A;
  font-size: 26px;
}

.avatar-mask {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 24px;
  line-height: 24px;
  text-align: center;
  font-size: 11px;
  color: #fff;
  background: rgba(0, 0, 0, 0.45);
  opacity: 0;
  transition: opacity 0.2s;
}

.avatar-wrap:hover .avatar-mask {
  opacity: 1;
}

.hidden-input {
  display: none;
}

.hero-info {
  padding-bottom: 6px;
}

.hero-name {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

.hero-account {
  margin-top: 6px;
  font-size: 13px;
  color: #909399;
  letter-spacing: 0.5px;
}

/* 表单卡片 */
.doc-info-card {
  max-width: 960px;
  margin: 0 auto;
  background: #fff;
  border-radius: 12px;
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
  background: #5B8C8A;
  margin-right: 8px;
}

.field-tip {
  font-size: 12px;
  color: #909399;
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

.selected-group {
  min-height: 32px;
  align-items: center;
}

.empty-tip {
  font-size: 12px;
  color: #B0BCBA;
}

.save-btn {
  width: 100%;
  height: 42px;
  font-size: 15px;
  border-radius: 8px;
  margin-top: 8px;
  background-color: #5B8C8A;
  border-color: #5B8C8A;
}

.save-btn:hover {
  background-color: #4A7A78;
  border-color: #4A7A78;
}
</style>
