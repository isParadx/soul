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
      <el-table-column prop="id" label="序号" width="70" />
      <el-table-column prop="stuId" label="学生账号" width="180" />
      <el-table-column prop="docId" label="医生账号" width="180" />
      <el-table-column prop="appointmentTime" label="预约时间" width="180" />
      <el-table-column prop="status" label="咨询状态" width="180" />
      <el-table-column prop="feedback" label="反馈内容" width="180">
          <template #default="{row}">
            <el-button type="text" @click="showDialog(row)">查看内容</el-button>
          </template>
        </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="{row}">
          <el-button type="danger" @click="deleteData(row.id)">删除</el-button>
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



  <!-- 反馈内容对话框 -->
  <el-dialog title="反馈内容" v-model="dialogVisible" width="30%">
      <el-form>
        <el-form-item label="反馈内容">
          <el-input
            type="textarea"
            v-model="currentFeedback.feedback"
            :disabled="true"
            :rows="30"
          ></el-input>
        </el-form-item>
      </el-form>
    </el-dialog>



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


      dialogVisible: false, // 控制对话框显示
      currentFeedback: {}, // 当前反馈数据
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
        console.log(res)
        if(res.data.data.loginUser.role==2){
          //调用查询所有用户信息接口

          this.$axios.get('/consult/checkOrder', {
            params: {
              keywords: this.searchQuery
            }
          }).then((res) => {
            this.tableData = res.data.data;
            this.totalItems = this.tableData.length;
            this.updatePagedTableData();
          }).catch((error) => {
            
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

    showDialog(row) {
        this.dialogVisible = true;
        this.currentFeedback=row;
    },

    deleteData(id) {
      this.$axios.post('/consult/delOrder',id, {
        headers: {
          'Content-Type': 'application/json'
        }
      })
        .then(() => {
          this.$message.success('删除成功');
          this.getTableData(); // 重新获取数据
        })
        .catch((error) => {
          console.error('Error deleting data:', error);
          this.$message.error('删除失败');
        });
    }
  }
}
</script>

<style scoped>
</style>