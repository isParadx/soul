<template>
  <div>
    <el-table :data="pagedTableData" stripe style="width: 100%">
    <el-table-column prop="id" label="序号" width="180" />
    <el-table-column prop="context" label="建议内容" width="180" />
    <el-table-column prop="time" label="日期" />
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
import { ElTable, ElTableColumn, ElPagination } from 'element-plus';

export default {
  components: {
    ElTable,
    ElTableColumn,
    ElPagination
  },
  data() {
    return {
      tableData: [],
      pagedTableData: [],
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
      this.$axios.get('/user/getUserInfo',{
        headers:{
          token:sessionStorage.getItem("token")
        }
      }).then((res) => {
        console.log(res)
        if(res.data.data.loginUser.role==2){
          //调用查询所有用户信息接口
              this.$axios.get('/advice/getAdvice').then((res) => {
                console.log(res);
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
    }
  }
}
</script>

<style scoped>
</style>