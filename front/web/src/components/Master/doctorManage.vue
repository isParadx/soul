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
      <el-table-column prop="id" label="账号" width="180" />
      <el-table-column prop="name" label="姓名" width="180" />
      <el-table-column prop="intruduce" label="个人简介" width="400" />
      <el-table-column prop="type" label="擅长领域" width="180" />
      <el-table-column prop="say" label="寄语" width="300" />
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
   mounted() {
    // 页面挂载后加载Coze SDK
    this.loadCozeSDK();
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
          
          //只有当是管理员时调用查询所有医生信息接口
          this.$axios.get('/doctor/getAllDocter', { 
            params: {
              keywords: this.searchQuery 
            }
          }).then((res) => {
            this.tableData = res.data.data;
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
    }
  }
}
</script>

<style scoped>
</style>