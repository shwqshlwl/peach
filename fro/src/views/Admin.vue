<template>
  <div class="admin-panel">
    <div class="container">
      <div class="page-header">
        <h1 class="title-font">管理后台</h1>
        <p class="subtitle">系统管理与数据维护</p>
      </div>

      <el-tabs v-model="activeTab" class="admin-tabs">
        <!-- 桃树管理 -->
        <el-tab-pane label="桃树管理" name="trees">
          <div class="tab-header">
            <el-button type="primary" @click="showTreeDialog = true">
              <el-icon><Plus /></el-icon>
              添加桃树
            </el-button>
          </div>

          <el-table :data="trees" style="width: 100%">
            <el-table-column prop="treeNo" label="编号" width="120" />
            <el-table-column prop="treeName" label="名称" width="150" />
            <el-table-column prop="variety" label="品种" width="100" />
            <el-table-column prop="treeAge" label="树龄" width="80" />
            <el-table-column prop="estimatedYield" label="预计产量(kg)" width="120" />
            <el-table-column prop="pricePerYear" label="年价格" width="100" />
            <el-table-column prop="status" label="状态" width="120">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">
                  {{ getStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180" fixed="right">
              <template #default="{ row }">
                <el-button text @click="editTree(row)">编辑</el-button>
                <el-button text type="danger" @click="deleteTree(row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 用户管理 -->
        <el-tab-pane label="用户管理" name="users">
          <el-table :data="users" style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="username" label="用户名" width="150" />
            <el-table-column prop="nickname" label="昵称" width="150" />
            <el-table-column prop="phone" label="手机号" width="150" />
            <el-table-column prop="role" label="角色" width="100">
              <template #default="{ row }">
                <el-tag :type="row.role === 'ADMIN' ? 'danger' : ''">
                  {{ row.role === 'ADMIN' ? '管理员' : '用户' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="points" label="积分" width="100" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'danger'">
                  {{ row.status === 1 ? '正常' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button 
                  text 
                  :type="row.status === 1 ? 'danger' : 'success'"
                  @click="toggleUserStatus(row)">
                  {{ row.status === 1 ? '禁用' : '启用' }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-model:current-page="userPage"
            v-model:page-size="userPageSize"
            :total="userTotal"
            layout="prev, pager, next"
            @current-change="loadUsers"
            style="margin-top: 20px; justify-content: center;"
          />
        </el-tab-pane>

        <!-- 社区管理 -->
        <el-tab-pane label="社区管理" name="posts">
          <el-table :data="posts" style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="title" label="标题" width="200" />
            <el-table-column prop="username" label="发布者" width="150" />
            <el-table-column prop="likeCount" label="点赞数" width="100" />
            <el-table-column prop="commentCount" label="评论数" width="100" />
            <el-table-column prop="createTime" label="发布时间" width="180" />
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="{ row }">
                <el-button text type="danger" @click="deletePost(row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>

      <!-- 添加/编辑桃树对话框 -->
      <el-dialog 
        v-model="showTreeDialog" 
        :title="treeForm.id ? '编辑桃树' : '添加桃树'" 
        width="600px">
        <el-form :model="treeForm" label-width="120px">
          <el-form-item label="桃树编号">
            <el-input v-model="treeForm.treeNo" />
          </el-form-item>
          <el-form-item label="桃树名称">
            <el-input v-model="treeForm.treeName" />
          </el-form-item>
          <el-form-item label="品种">
            <el-input v-model="treeForm.variety" />
          </el-form-item>
          <el-form-item label="树龄（年）">
            <el-input-number v-model="treeForm.treeAge" :min="1" />
          </el-form-item>
          <el-form-item label="预计产量(kg)">
            <el-input-number v-model="treeForm.estimatedYield" :min="0" :precision="2" />
          </el-form-item>
          <el-form-item label="年认领价格">
            <el-input-number v-model="treeForm.pricePerYear" :min="0" :precision="2" />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="treeForm.status">
              <el-option label="可认领" value="AVAILABLE" />
              <el-option label="已认领" value="ADOPTED" />
              <el-option label="维护中" value="MAINTENANCE" />
            </el-select>
          </el-form-item>
          <el-form-item label="描述">
            <el-input v-model="treeForm.description" type="textarea" :rows="4" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showTreeDialog = false">取消</el-button>
          <el-button type="primary" @click="saveTree">保存</el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()
const activeTab = ref('trees')
const trees = ref([])
const users = ref([])
const posts = ref([])
const showTreeDialog = ref(false)
const userPage = ref(1)
const userPageSize = ref(10)
const userTotal = ref(0)

const treeForm = reactive({
  id: null,
  treeNo: '',
  treeName: '',
  variety: '',
  treeAge: 1,
  estimatedYield: 0,
  pricePerYear: 0,
  status: 'AVAILABLE',
  description: ''
})

// 检查管理员权限
onMounted(() => {
  if (!userStore.userInfo || userStore.userInfo.role !== 'ADMIN') {
    ElMessage.error('无权访问')
    router.push('/')
    return
  }
  loadTrees()
  loadUsers()
  loadPosts()
})

const loadTrees = async () => {
  try {
    const res = await request.get('/peach-trees/list')
    trees.value = res.data
  } catch (error) {
    ElMessage.error('加载失败')
  }
}

const loadUsers = async () => {
  try {
    const res = await request.get('/admin/users', {
      params: {
        pageNum: userPage.value,
        pageSize: userPageSize.value
      }
    })
    users.value = res.data.records
    userTotal.value = res.data.total
  } catch (error) {
    ElMessage.error('加载失败')
  }
}

const loadPosts = async () => {
  try {
    const res = await request.get('/community/posts', {
      params: { pageNum: 1, pageSize: 100 }
    })
    posts.value = res.data.records
  } catch (error) {
    ElMessage.error('加载失败')
  }
}

const editTree = (tree) => {
  Object.assign(treeForm, tree)
  showTreeDialog.value = true
}

const saveTree = async () => {
  try {
    if (treeForm.id) {
      await request.put('/admin/tree/update', treeForm)
    } else {
      await request.post('/admin/tree/add', treeForm)
    }
    ElMessage.success('保存成功')
    showTreeDialog.value = false
    loadTrees()
    resetTreeForm()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const deleteTree = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这棵桃树吗？', '提示', {
      type: 'warning'
    })
    await request.delete(`/admin/tree/delete/${id}`)
    ElMessage.success('删除成功')
    loadTrees()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const toggleUserStatus = async (user) => {
  try {
    const newStatus = user.status === 1 ? 0 : 1
    await request.put(`/admin/user/status/${user.id}`, null, {
      params: { status: newStatus }
    })
    ElMessage.success('操作成功')
    loadUsers()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const deletePost = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这条帖子吗？', '提示', {
      type: 'warning'
    })
    await request.delete(`/admin/post/delete/${id}`)
    ElMessage.success('删除成功')
    loadPosts()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const resetTreeForm = () => {
  Object.assign(treeForm, {
    id: null,
    treeNo: '',
    treeName: '',
    variety: '',
    treeAge: 1,
    estimatedYield: 0,
    pricePerYear: 0,
    status: 'AVAILABLE',
    description: ''
  })
}

const getStatusType = (status) => {
  const map = {
    'AVAILABLE': 'success',
    'ADOPTED': 'warning',
    'MAINTENANCE': 'info'
  }
  return map[status] || ''
}

const getStatusText = (status) => {
  const map = {
    'AVAILABLE': '可认领',
    'ADOPTED': '已认领',
    'MAINTENANCE': '维护中'
  }
  return map[status] || status
}
</script>

<style lang="scss" scoped>
.admin-panel {
  min-height: 80vh;
}

.page-header {
  text-align: center;
  margin-bottom: 48px;
  
  h1 {
    font-size: 48px;
    color: var(--primary-color);
    margin-bottom: 12px;
  }
  
  .subtitle {
    font-size: 18px;
    color: var(--text-light);
  }
}

.admin-tabs {
  background: white;
  padding: 24px;
  border-radius: 16px;
  box-shadow: var(--shadow-sm);
}

.tab-header {
  margin-bottom: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
