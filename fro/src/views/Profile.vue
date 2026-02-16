<template>
  <div class="profile">
    <div class="container">
      <div class="profile-header card">
        <div class="avatar-section">
          <el-avatar :src="userStore.userInfo?.avatar" :size="120">
            {{ userStore.userInfo?.nickname?.[0] }}
          </el-avatar>
          <el-button text size="small">更换头像</el-button>
        </div>
        
        <div class="user-info-section">
          <h2>{{ userStore.userInfo?.nickname }}</h2>
          <div class="user-meta">
            <div class="meta-item">
              <span class="label">用户名：</span>
              <span class="value">{{ userStore.userInfo?.username }}</span>
            </div>
            <div class="meta-item">
              <span class="label">手机号：</span>
              <span class="value">{{ userStore.userInfo?.phone }}</span>
            </div>
            <div class="meta-item">
              <span class="label">会员等级：</span>
              <span class="value">Lv.{{ userStore.userInfo?.memberLevel }}</span>
            </div>
          </div>
        </div>
        
        <div class="points-section">
          <div class="points-card">
            <div class="points-icon">⭐</div>
            <div class="points-value">{{ userStore.userInfo?.points || 0 }}</div>
            <div class="points-label">我的积分</div>
          </div>
        </div>
      </div>

      <div class="profile-content">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="我的订单" name="orders">
            <div class="orders-list">
              <div class="order-card card" v-for="order in orders" :key="order.id">
                <div class="order-header">
                  <span class="order-no">订单号：{{ order.orderNo }}</span>
                  <el-tag :type="getOrderStatusType(order.paymentStatus)">
                    {{ getOrderStatusText(order.paymentStatus) }}
                  </el-tag>
                </div>
                <div class="order-content">
                  <div class="order-info">
                    <h4>{{ order.peachTree?.treeName }}</h4>
                    <p>认领时间：{{ formatDate(order.createTime) }}</p>
                    <p>有效期至：{{ formatDate(order.endDate) }}</p>
                  </div>
                  <div class="order-amount">
                    <span class="amount-label">金额</span>
                    <span class="amount-value">¥{{ order.amount }}</span>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="积分记录" name="points">
            <div class="points-history">
              <el-empty description="暂无积分记录" />
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="账户设置" name="settings">
            <div class="settings-form card">
              <el-form :model="settingsForm" label-width="100px">
                <el-form-item label="昵称">
                  <el-input v-model="settingsForm.nickname" />
                </el-form-item>
                <el-form-item label="邮箱">
                  <el-input v-model="settingsForm.email" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="updateProfile">保存修改</el-button>
                </el-form-item>
              </el-form>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getUserOrders } from '@/api/order'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const userStore = useUserStore()
const activeTab = ref('orders')
const orders = ref([])

const settingsForm = reactive({
  nickname: '',
  email: ''
})

const loadOrders = async () => {
  if (!userStore.userInfo) return
  
  try {
    const res = await getUserOrders(userStore.userInfo.id)
    orders.value = res.data
  } catch (error) {
    console.error('加载订单失败', error)
  }
}

const getOrderStatusType = (status) => {
  const map = {
    'PENDING': 'warning',
    'PAID': 'success',
    'REFUNDED': 'info'
  }
  return map[status] || 'info'
}

const getOrderStatusText = (status) => {
  const map = {
    'PENDING': '待支付',
    'PAID': '已支付',
    'REFUNDED': '已退款'
  }
  return map[status] || status
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN')
}

const updateProfile = async () => {
  try {
    await request.put('/auth/user/update', {
      id: userStore.userInfo.id,
      ...settingsForm
    })
    ElMessage.success('更新成功')
  } catch (error) {
    ElMessage.error('更新失败')
  }
}

onMounted(() => {
  if (userStore.userInfo) {
    settingsForm.nickname = userStore.userInfo.nickname
    settingsForm.email = userStore.userInfo.email
    loadOrders()
  }
})
</script>

<style lang="scss" scoped>
.profile {
  min-height: 80vh;
}

.profile-header {
  display: grid;
  grid-template-columns: auto 1fr auto;
  gap: 32px;
  align-items: center;
  margin-bottom: 32px;
  padding: 32px;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.user-info-section {
  h2 {
    font-size: 28px;
    color: var(--text-dark);
    margin-bottom: 16px;
  }
}

.user-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.meta-item {
  .label {
    color: var(--text-light);
    margin-right: 8px;
  }
  
  .value {
    color: var(--text-dark);
    font-weight: 500;
  }
}

.points-section {
  .points-card {
    background: linear-gradient(135deg, var(--primary-color), var(--dark-pink));
    color: white;
    padding: 24px;
    border-radius: 16px;
    text-align: center;
    min-width: 150px;
    
    .points-icon {
      font-size: 36px;
      margin-bottom: 8px;
    }
    
    .points-value {
      font-size: 32px;
      font-weight: 700;
      margin-bottom: 4px;
    }
    
    .points-label {
      font-size: 14px;
      opacity: 0.9;
    }
  }
}

.profile-content {
  background: white;
  border-radius: 20px;
  padding: 24px;
  box-shadow: var(--shadow-sm);
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-card {
  padding: 20px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--bg-light);
  
  .order-no {
    color: var(--text-light);
    font-size: 14px;
  }
}

.order-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-info {
  h4 {
    font-size: 18px;
    color: var(--text-dark);
    margin-bottom: 8px;
  }
  
  p {
    color: var(--text-light);
    font-size: 14px;
    margin-bottom: 4px;
  }
}

.order-amount {
  text-align: right;
  
  .amount-label {
    display: block;
    font-size: 14px;
    color: var(--text-light);
    margin-bottom: 4px;
  }
  
  .amount-value {
    font-size: 24px;
    font-weight: 700;
    color: var(--primary-color);
  }
}

.settings-form {
  max-width: 600px;
  padding: 32px;
}
</style>
