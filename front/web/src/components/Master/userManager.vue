<template>
  <div>
    <!-- 添加搜索框 -->
    <el-input
      placeholder="请输入您要搜索的内容"
      v-model="searchQuery"
      clearable
      style="margin-bottom: 20px;width: 20vw;"
      @keyup.enter="searchData"
    >
      <template #append>
        <el-button @click="searchData">搜索</el-button>
      </template>
    </el-input>
    <el-table :data="pagedTableData" stripe style="width: 100%">
      <el-table-column prop="userid" label="账号" width="180" />
      <el-table-column prop="nickname" label="昵称/姓名" width="180" />
      <el-table-column prop="sex" label="性别" width="180" />
      <el-table-column prop="phone" label="联系电话" width="180" />
      <el-table-column prop="email" label="邮箱" width="180" />
      <el-table-column prop="role" label="角色" width="180" />
      <el-table-column label="操作" width="210">
        <template #default="{row}">
          <el-row type="flex" justify="space-around">
            <el-col :span="12">
              <el-button type="success" @click="reSetPwd(row.userid)">重置密码</el-button>
            </el-col>
            <el-col :span="12">
              <el-button type="danger" @click="deleteData(row.userid,row.role)">注销账户</el-button>
            </el-col>
          </el-row>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="currentPage"
      :page-sizes="[10, 15, 20]"
      :page-size="pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="totalItems">
    </el-pagination>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { ElTable, ElTableColumn, ElPagination, ElInput, ElButton } from 'element-plus';

export default {
  components: {
    ElTable,
    ElTableColumn,
    ElPagination,
    ElInput,
    ElButton
  },
  data() {
    return {
      tableData: [],
      pagedTableData: [],
      searchQuery: null, 
      currentPage: 1,
      pageSize: 10,
      totalItems: 0,
    };
  },
  created() {
    this.getTableData();
  },
  methods: {
    getTableData() {
      //确认是管理员账户
      this.$axios.get('/user/getUserInfo',{
        headers:{
          token:sessionStorage.getItem("token")
        }
      }).then((res) => {
        if(res.data.data.loginUser.role==2){
          //调用查询所有用户信息接口
          this.$axios.get('/user/getAllUserInfo', { 
            params: {
              keywords: this.searchQuery 
            }
          }).then((res) => {
            const resData=res.data.data
            resData.forEach(item => {
              if(item.role==0){
                  item.role='学生'
              }else if(item.role==1){
                  item.role='医生'
              }else if(item.role==2){
                  item.role='管理员'
              }

              if(item.sex==0){
                    item.sex='男'
                }else if(item.sex==1){
                    item.sex='女'
                }
            });
            this.tableData =resData
            this.totalItems = this.tableData.length;
            this.updatePagedTableData();
          }).catch((error) => {
            console.error('Error fetching protected data:', error);
          });

        }else{
          this.$message.error("对不起,您没有权限！")
          sessionStorage.clear();
          this.$router.push('/');  
        }
      }).catch((error) => {
        console.error('Error fetching protected data:', error);
      });
    },
    handleSizeChange(newSize) {
      this.pageSize = newSize;
      this.currentPage = 1;
      this.updatePagedTableData();
    },
    handleCurrentChange(newPage) {
      this.currentPage = newPage;
      this.updatePagedTableData();
    },
    updatePagedTableData() {
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      this.pagedTableData = this.tableData.slice(start, end);
    },
    searchData() {
      this.getTableData();
      this.currentPage = 1; // 重置页码为第一页
      this.updatePagedTableData(); // 更新分页数据
    },
    deleteData(userid,role) {
         if(role=="医生"){
            this.deleteDoc(userid);
            this.deleteUser(userid)
            this.getTableData();
          }else if(role=="管理员"){
            this.$message.error('管理员账户不可注销');
          }else{
            this.deleteUser(userid);
            this.getTableData();
          };
    },
    deleteDoc(id){
      this.$axios.post('/doctor/delDocter', id,{
        headers: {
          'Content-Type': 'application/json'
        }
      })
        .then(() => {
          this.$message.success('删除医生成功');
        })
        .catch((error) => {
          this.$message.error('删除失败');
        });
    },

    deleteUser(id){
      this.$axios.post('/user/delUser',id, {
              headers: {
                'Content-Type': 'application/json'
              }
            }).then(() => {
              this.$message.success('注销成功！');
              })
              .catch((error) => {
                this.$message.error('删除失败');
              });

            this.getTableData(); 
    },

    reSetPwd(id){//ResetPassword
        console.log(id);
        this.$axios.post('/user/ResetPassword',id, {
          headers: {
              'Content-Type': 'application/json'
          }
      }).then(() => {
          this.$message.success('重置成功,新密码:123');
          this.getTableData(); // 重新获取数据
          if(role=='管理员'){
            sessionStorage.clear();
            this.$router.push('/');
            this.$message.error('管理员账户已被修改，重新登录');   
          }
      })
      .catch((error) => {
          console.log(error)
          this.$message.error('重置失败');
      });
    }
  }
}
</script>

<style scoped>
.el-button {
  margin-right: 10px;
}
</style>