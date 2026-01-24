<template>
  <div class="container">
    <!-- 左侧视图 -->
    <div class="left-view">
      <el-form-item class="form-item">
        <el-label>昵 称：</el-label>
        <el-input v-model="user.nickname" placeholder="请输入昵称" />
      </el-form-item>

      <el-form-item class="form-item">
        <el-label>电 话：</el-label>
        <el-input v-model="user.phone" placeholder="请输入手机号" />
      </el-form-item>

      <el-form-item class="form-item">
        <el-label>性 别：</el-label>
        <el-select v-model="user.sex" placeholder="请选择性别">
          <el-option
            v-for="(gender, index) in genders"
            :key="index"
            :label="gender"
            :value="index"
          />
        </el-select>
      </el-form-item>

      <el-form-item class="form-item">
        <el-label>邮箱：</el-label>
        <el-input v-model="user.email" placeholder="请输入邮箱" />
      </el-form-item>

      <el-button class="save-btn" @click="saveInfo">确认修改</el-button>
    </div>

    <!-- 右侧视图 -->
    <div class="right-view">
      <el-form ref="doctorForm" :model="doctorForm" :rules="rules" label-width="120px">
        <el-form-item label="个人简介" prop="intruduce">
          <el-input
            type="textarea"
            v-model="doctorForm.intruduce"
            :autosize="{ minRows: 4, maxRows: 6}"
            placeholder="请输入个人简介"
          ></el-input>
        </el-form-item>
        <el-form-item label="擅长领域" prop="type">
          <div class="type-tip">医生标签之间用逗号隔开，一个标签不超过七个字</div>
          <el-input
            type="textarea"
            v-model="doctorForm.type"
            :autosize="{ minRows: 4, maxRows: 6}"
            placeholder="请输入擅长领域"
          ></el-input>
        </el-form-item>
        <el-form-item label="医师寄语" prop="say">
          <el-input
            type="textarea"
            v-model="doctorForm.say"
            :autosize="{ minRows: 4, maxRows: 6}"
            placeholder="请输入医师寄语"
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm" class="submit-button">确认修改</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue';
import axios from 'axios';

export default {
  data() {
    return {
      token: sessionStorage.getItem('token'),
      user: {
        userid: '',
        nickname: '',
        phone: '',
        sex: '',
        email: '',
      },
      genders: ['男', '女'],
      doctorForm: {
        id:'',
        intruduce: '',
        type: '',
        say: ''
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

  mounted() {
    this.getUserInfo();
    this.getDocInfo();
  },

  methods: {
    getUserInfo() {
      this.$axios.get('/user/getUserInfo', {
        headers: {
          token: sessionStorage.getItem("token")
        }
      })
      .then((res) => {
        this.user = res.data.data.loginUser;
      })
      .catch(() => { });
    },
    onGenderChange(value) {
      this.user.sex = this.genders[value];
    },
    saveInfo() {
      if (this.user.nickname && this.user.sex && this.user.phone && this.user.email) {
        this.$axios.post('/user/changeUserInfo', this.user, {
          headers: {
            token: sessionStorage.getItem("token")
          }
        })
        .then(() => {
          this.$message.success('修改成功！');
        })
        .catch(() => {
          this.$message.error('错误，保存失败');
        });
      } else {
        this.$message.error('错误，请检查所有字段');
      }
    },
    validateSpecialty(rule, value, callback) {
      if (!value) {
        return callback(new Error('擅长领域不能为空'));
      }
      const tags = value.split(',');
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
        } else {
          this.$message.success('表单验证失败！');
          return false;
        }
      });
    },
    submitData() {
      // 修改医生信息
      this.$axios.post('/doctor/changeDoc', this.doctorForm).then((res) => {
        this.$message.success('修改成功！');
      }).catch((error) => {
        this.$message.success('错误！');
      });
        
    },
    getDocInfo() {
      this.$axios.get('/doctor/getAllDocter', {
        params: {
          keywords: this.user.userid
        }
      }).then((res) => {
        this.doctorForm = res.data.data[0];
      }).catch((error) => {
        this.$message.success('错误！');
      });
    }
  }
};
</script>

<style scoped>
.container {
  display: flex;
  justify-content: space-between;
  padding: 20px;
  background-color:#ffffff;
  border-radius: 8px;
}

.left-view, .right-view {
  flex: 1;
  padding: 20px;
  background-color: transparent; /* 使背景色透明，统一背景 */
  border: none; /* 去掉边框 */
  border-radius: 8px;
  box-shadow: none; /* 去掉阴影 */
  display: flex;
  flex-direction: column;
}


.form-item, .el-form {
  width: 100%;
  display: flex;
  flex-direction: column;
  margin-bottom: 20px;
}

.el-label {
  margin-bottom: 5px;
  color: #333;
  font-weight: 500;
}

.el-input__inner, .el-textarea__inner {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #ccc;
  border-radius: 6px;
  font-size: 16px;
  transition: all 0.3s;
}

.save-btn, .submit-button {
  width: 100%;
  padding: 10px;
  margin-top: 20px;
  background: linear-gradient(135deg, #55aaff, #55ffff);
  color: white;
  border: none;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.save-btn:hover, .submit-button:hover {
  background: #66b1ff;
}

.form-title {
  text-align: center;
  font-size: 24px;
  color: #333;
  margin-bottom: 20px;
}

.type-tip {
  font-size: 12px;
  color: #666;
  margin-bottom: 5px;
}
</style>