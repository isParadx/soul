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
        <el-table-column prop="appointmentTime" label="预约时间" width="280" />
        <el-table-column prop="status" label="咨询状态" width="180" />
        <el-table-column prop="feedback" label="反馈内容" width="180">
          <template #default="{row}">
            <el-button type="text" @click="showDialog(row)">查看内容</el-button>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{row}">
            <div style="display: flex; justify-content: space-around;">
              <el-button type="success" @click="updateData(row)">{{row.statusButton}}</el-button>
              <el-button type="danger" @click="deleteData(row.id)">取消预约</el-button>
            </div>
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
            placeholder="请输入反馈内容"
            :rows="30"
          ></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveFeedback">提交并结束</el-button>
      </span>
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
        keywords:null,
        docid:null,
        currentPage: 1,
        pageSize: 10,
        totalItems: 0,

        statusButton:'',
        dialogVisible: false, // 控制对话框显示
        currentFeedback: {}, // 当前反馈数据
      };
    },
    mounted() {
         //确认是管理员账户
         this.$axios.get('/user/getUserInfo',{
          headers:{
            token:sessionStorage.getItem("token")
          }
        }).then((res) => {
          if(res.data.data.loginUser.role==1){
            this.keywords=res.data.data.loginUser.userid
            this.docid=res.data.data.loginUser.userid
            //调用查询所有患者接口
            this.getTableData();
          }else{
            this.$message.error("对不起,您没有权限！")
            sessionStorage.clear();
            this.$router.push('/');  
          }
        }).catch((error) => {
          console.error('Error fetching protected data:', error);
        });
    },
    methods: {
      getTableData() {
        //调用查询所有用户信息接口
        this.$axios.get('/consult/checkOrder', {
              params: {
                keywords: this.keywords
              }
            }).then((res) => {
              this.tableData = res.data.data;
              this.totalItems = this.tableData.length;
              this.updatePagedTableData();
              this.tableData.forEach(item=>{
                item.statusButton="未开始";
              if(item.status=="未开始"){
                item.statusButton="开始问询";
              }else if(item.status=="进行中"){
                item.statusButton="结束问询";
              }else if(item.status=="已结束"){
                item.statusButton="已结束";
              }
            })

            }).catch((error) => {
              
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
        this.keywords=this.searchQuery
        this.getTableData();
        // 将非该医生的患者剔除
        const resDate=[];
        this.tableData.forEach(item=>{
          if(item.docId==this.docid){
            resDate.push(item)
          }
        })
        this.tableData=resDate;
        this.currentPage = 1; // 重置页码为第一页
        this.updatePagedTableData(); // 更新分页数据
      },

      //显示状态弹窗
      showDialog(row) {
        if(row.status=="未开始"){
          this.$message.error('咨询未开始，您不应该填写报告！');
        }else{
          this.dialogVisible = true;
          this.currentFeedback=row;
        }
       
    },
      updateData(row){
        if(row.status=='进行中'&&row.feedback==null){
          this.$message.error('您还未填写报告，无法结束项目');
        }else{
          if(row.status=='已结束'){
            this.$message.error('项目已经结束，请勿重新操作');
          }else{
            this.$axios.post('/consult/changeInfo', row)
            .then(() => {
              this.$message.success('咨询状态已经改变');
              this.getTableData(); // 重新获取数据
            })
            .catch((error) => {
              this.$message.error('错误，请检查服务状态');
            });
          }
          
        }
      },

      saveFeedback(){
        if(this.currentFeedback.status=="已结束"){
          this.$message.error('该咨询已经结束,请勿修改报告');
        }else{
          this.$axios.post('/consult/changeInfo', this.currentFeedback)
            .then(() => {
              this.$message.success('报告已提交');
              this.getTableData(); // 重新获取数据
            })
            .catch((error) => {
              this.$message.error('错误，请检查服务状态');
            });
        }
        this.dialogVisible = false;

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
            this.$message.error('删除失败');
          });
      }
    }
  }
  </script>
  
  <style scoped>
  </style>