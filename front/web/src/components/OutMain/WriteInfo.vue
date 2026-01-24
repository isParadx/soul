<template>
  <div>
    <div class="doctor-info-form">
      <div class="form-title">为了方便学生更好的选择您，您需要完善以下内容</div>
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
          <el-button type="primary" @click="submitForm" class="submit-button">提交</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
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
      const tags = value.split(',');
      console.log(tags)
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
          console.log('表单验证失败');
          return false;
        }
      });
    },
    submitData() {
      //调用添加医生信息接口
      this.$axios.post('/doctor/addDoctorInfo', {
        id:this.userForm.userid,
        intruduce:this.doctorForm.intruduce,
        say:this.doctorForm.say,
        name:this.userForm.nickname,
        type:this.doctorForm.type
      })
        .then(res => {
          //调用注册用户接口
          if(res.data.code==200){
            this.$axios.post('/user/regist', this.userForm).then(r => {
              if(r.data.code==200){
                this.$message.success("注册成功，跳转至登录！")
                sessionStorage.clear();
                this.$router.push('/');   
              }
            })
          .catch(error => {
              console.error('Error fetching protected data:', error);
          });
          }
        })
    }
  }
};
</script>

<style scoped>
.doctor-info-form {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.form-title {
  text-align: center;
  font-size: 24px;
  color: #333;
  margin-bottom: 20px;
}

.el-form {
  display: flex;
  flex-direction: column;
}

.el-form-item {
  margin-bottom: 20px;
}

.el-input, .el-textarea {
  width: 100%;
}

.el-button {
  align-self: center;
  width: 100%;
  margin-top: 10px;
}

.specialty-tip {
  font-size: 12px;
  color: #666;
  margin-bottom: 5px;
}

.submit-button {
  background-color: #409EFF;
  border-color: #409EFF;
}

.submit-button:hover {
  background-color: #66b1ff;
  border-color: #66b1ff;
}
</style>