<template>
  <div class="my-tree">
    <div class="container">
      <div class="page-header">
        <h1 class="title-font">我的桃树</h1>
        <p class="subtitle">见证您的桃树成长每一刻</p>
      </div>

      <div v-if="myOrders.length === 0" class="empty-state">
        <el-empty description="您还没有认领桃树">
          <el-button type="primary" @click="$router.push('/trees')">
            去认领
          </el-button>
        </el-empty>
      </div>

      <div v-else class="my-trees-list">
        <div class="tree-item card" v-for="order in myOrders" :key="order.id">
          <div class="tree-header">
            <div class="tree-basic-info">
              <h2>{{ order.peachTree?.treeName || '我的桃树' }}</h2>
              <div class="tree-meta">
                <span>认领时间：{{ formatDate(order.createTime) }}</span>
                <span>有效期至：{{ formatDate(order.endDate) }}</span>
              </div>
            </div>
            <div class="custom-plate" v-if="order.customNamePlate">
              <div class="plate-icon">🏷️</div>
              <div class="plate-name">{{ order.customNamePlate }}</div>
            </div>
          </div>

          <div class="tree-content">
            <div class="interaction-panel card">
              <h3>每日互动</h3>
              <div class="interaction-actions">
                <div class="action-item" @click="performAction(order.treeId, 'WATER')">
                  <div class="action-icon">💧</div>
                  <div class="action-name">浇水</div>
                  <div class="action-points">+5积分</div>
                </div>
                <div class="action-item" @click="performAction(order.treeId, 'FERTILIZE')">
                  <div class="action-icon">🌱</div>
                  <div class="action-name">施肥</div>
                  <div class="action-points">+10积分</div>
                </div>
                <div class="action-item" @click="performAction(order.treeId, 'SIGN_IN')">
                  <div class="action-icon">✅</div>
                  <div class="action-name">签到</div>
                  <div class="action-points">+3积分</div>
                </div>
              </div>
              <div class="points-info">
                当前积分：<span class="points-value">{{ userStore.userInfo?.points || 0 }}</span>
              </div>
            </div>

            <div class="growth-timeline">
              <h3>生长日记</h3>
              <el-timeline>
                <el-timeline-item
                  v-for="diary in treeDiaries[order.treeId] || []"
                  :key="diary.id"
                  :timestamp="diary.recordDate"
                  placement="top">
                  <div class="timeline-content">
                    <div class="timeline-stage">{{ getStageText(diary.growthStage) }}</div>
                    <h4>{{ diary.title }}</h4>
                    <p>{{ diary.content }}</p>
                  </div>
                </el-timeline-item>
              </el-timeline>
              <el-button text @click="viewMoreDiaries(order.treeId)">
                查看更多 <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
          </div>

          <div class="tree-footer">
            <el-button @click="manageAddress">管理收货地址</el-button>
            <el-button type="primary" @click="viewTreeDetail(order.treeId)">
              查看详情
            </el-button>
          </div>
        </div>
      </div>

      <!-- 收货地址管理对话框 -->
      <el-dialog v-model="addressDialogVisible" title="收货地址管理" width="600px">
        <div class="address-list">
          <div class="address-item" v-for="addr in addresses" :key="addr.id">
            <div class="address-content">
              <div class="address-header">
                <span class="receiver">{{ addr.receiverName }}</span>
                <span class="phone">{{ addr.phone }}</span>
                <el-tag v-if="addr.isDefault === 1" type="success" size="small">默认</el-tag>
              </div>
              <div class="address-detail">
                {{ addr.province }} {{ addr.city }} {{ addr.district }} {{ addr.detailAddress }}
              </div>
            </div>
            <div class="address-actions">
              <el-button text @click="editAddress(addr)">编辑</el-button>
              <el-button text type="danger" @click="deleteAddress(addr.id)">删除</el-button>
            </div>
          </div>
        </div>
        <el-button type="primary" @click="showAddressForm = true" style="width: 100%; margin-top: 16px;">
          添加新地址
        </el-button>
      </el-dialog>

      <!-- 添加/编辑地址表单 -->
      <el-dialog v-model="showAddressForm" title="地址信息" width="500px">
        <el-form :model="addressForm" label-width="100px">
          <el-form-item label="收货人">
            <el-input v-model="addressForm.receiverName" />
          </el-form-item>
          <el-form-item label="联系电话">
            <el-input v-model="addressForm.phone" />
          </el-form-item>
          <el-form-item label="省份">
            <el-input v-model="addressForm.province" />
          </el-form-item>
          <el-form-item label="城市">
            <el-input v-model="addressForm.city" />
          </el-form-item>
          <el-form-item label="区县">
            <el-input v-model="addressForm.district" />
          </el-form-item>
          <el-form-item label="详细地址">
            <el-input v-model="addressForm.detailAddress" type="textarea" />
          </el-form-item>
          <el-form-item label="默认地址">
            <el-switch v-model="addressForm.isDefault" :active-value="1" :inactive-value="0" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showAddressForm = false">取消</el-button>
          <el-button type="primary" @click="saveAddress">保存</el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getUserOrders } from '@/api/order'
import { getTreeDiaries, performAction as performActionApi } from '@/api/interaction'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const userStore = useUserStore()
const myOrders = ref([])
const treeDiaries = ref({})
const addressDialogVisible = ref(false)
const showAddressForm = ref(false)
const addresses = ref([])
const addressForm = reactive({
  id: null,
  receiverName: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  isDefault: 0
})

const loadMyOrders = async () => {
  if (!userStore.userInfo) {
    router.push('/login')
    return
  }
  
  try {
    const res = await getUserOrders(userStore.userInfo.id)
    myOrders.value = res.data
    
    // 加载每棵树的生长日记
    for (const order of myOrders.value) {
      if (order.treeId) {
        loadTreeDiaries(order.treeId)
      }
    }
  } catch (error) {
    ElMessage.error('加载失败')
  }
}

const loadTreeDiaries = async (treeId) => {
  try {
    const res = await getTreeDiaries(treeId)
    treeDiaries.value[treeId] = res.data.slice(0, 3) // 只显示最近3条
  } catch (error) {
    console.error('加载生长日记失败', error)
  }
}

const performAction = async (treeId, actionType) => {
  try {
    await performActionApi({
      userId: userStore.userInfo.id,
      treeId,
      actionType
    })
    ElMessage.success('操作成功，积分已增加！')
    // 刷新用户信息以更新积分
    const userRes = await request.get(`/auth/user/${userStore.userInfo.id}`)
    userStore.setUser(userRes.data)
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const getStageText = (stage) => {
  const map = {
    'FLOWERING': '🌸 开花期',
    'FRUITING': '🍑 结果期',
    'RIPENING': '✨ 成熟期'
  }
  return map[stage] || stage
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN')
}

const viewMoreDiaries = (treeId) => {
  router.push(`/tree/${treeId}`)
}

const viewTreeDetail = (treeId) => {
  router.push(`/tree/${treeId}`)
}

const manageAddress = async () => {
  addressDialogVisible.value = true
  try {
    const res = await request.get(`/address/user/${userStore.userInfo.id}`)
    addresses.value = res.data
  } catch (error) {
    ElMessage.error('加载地址失败')
  }
}

const editAddress = (addr) => {
  Object.assign(addressForm, addr)
  showAddressForm.value = true
}

const saveAddress = async () => {
  try {
    addressForm.userId = userStore.userInfo.id
    if (addressForm.id) {
      await request.put('/address/update', addressForm)
    } else {
      await request.post('/address/add', addressForm)
    }
    ElMessage.success('保存成功')
    showAddressForm.value = false
    manageAddress()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const deleteAddress = async (id) => {
  try {
    await request.delete(`/address/delete/${id}`)
    ElMessage.success('删除成功')
    manageAddress()
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

onMounted(() => {
  loadMyOrders()
})
</script>

<style lang="scss" scoped>
.my-tree {
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

.empty-state {
  padding: 80px 0;
}

.my-trees-list {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.tree-item {
  padding: 32px;
}

.tree-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 2px solid var(--bg-light);
  
  h2 {
    font-size: 28px;
    color: var(--text-dark);
    margin-bottom: 8px;
  }
  
  .tree-meta {
    display: flex;
    gap: 24px;
    font-size: 14px;
    color: var(--text-light);
  }
}

.custom-plate {
  background: linear-gradient(135deg, var(--primary-color), var(--dark-pink));
  color: white;
  padding: 16px 24px;
  border-radius: 12px;
  text-align: center;
  box-shadow: var(--shadow-md);
  
  .plate-icon {
    font-size: 32px;
    margin-bottom: 8px;
  }
  
  .plate-name {
    font-size: 18px;
    font-weight: 600;
  }
}

.tree-content {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 32px;
  margin-bottom: 24px;
}

.interaction-panel {
  background: var(--bg-light);
  
  h3 {
    font-size: 18px;
    color: var(--text-dark);
    margin-bottom: 20px;
  }
}

.interaction-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 20px;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: white;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateX(8px);
    box-shadow: var(--shadow-sm);
  }
  
  .action-icon {
    font-size: 32px;
  }
  
  .action-name {
    flex: 1;
    font-weight: 600;
    color: var(--text-dark);
  }
  
  .action-points {
    color: var(--primary-color);
    font-size: 14px;
  }
}

.points-info {
  text-align: center;
  padding: 16px;
  background: white;
  border-radius: 12px;
  font-size: 16px;
  color: var(--text-dark);
  
  .points-value {
    font-size: 24px;
    font-weight: 700;
    color: var(--primary-color);
    margin-left: 8px;
  }
}

.growth-timeline {
  h3 {
    font-size: 18px;
    color: var(--text-dark);
    margin-bottom: 20px;
  }
}

.timeline-content {
  .timeline-stage {
    display: inline-block;
    padding: 4px 12px;
    background: var(--accent-color);
    color: var(--primary-color);
    border-radius: 12px;
    font-size: 14px;
    margin-bottom: 8px;
  }
  
  h4 {
    font-size: 16px;
    color: var(--text-dark);
    margin-bottom: 8px;
  }
  
  p {
    color: var(--text-light);
    line-height: 1.6;
  }
}

.tree-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 24px;
  border-top: 1px solid var(--bg-light);
}

.address-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  max-height: 400px;
  overflow-y: auto;
}

.address-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: var(--bg-light);
  border-radius: 12px;
}

.address-content {
  flex: 1;
}

.address-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
  
  .receiver {
    font-weight: 600;
    color: var(--text-dark);
  }
  
  .phone {
    color: var(--text-light);
  }
}

.address-detail {
  color: var(--text-light);
  font-size: 14px;
}

.address-actions {
  display: flex;
  gap: 8px;
}
</style>
