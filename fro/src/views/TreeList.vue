<template>
  <div class="tree-list">
    <div class="container">
      <div class="page-header">
        <h1 class="title-font">认领桃树</h1>
        <p class="subtitle">选择您心仪的桃树，开启甜蜜之旅</p>
      </div>

      <div class="filters">
        <el-radio-group v-model="statusFilter" @change="loadTrees">
          <el-radio-button label="">全部</el-radio-button>
          <el-radio-button label="AVAILABLE">可认领</el-radio-button>
          <el-radio-button label="ADOPTED">已认领</el-radio-button>
        </el-radio-group>
      </div>

      <div class="trees-grid" v-loading="loading">
        <div class="tree-card card" v-for="tree in trees" :key="tree.id"
             @click="viewDetail(tree.id)">
          <div class="tree-image">
            <img :src="tree.images || '/placeholder-tree.jpg'" alt="桃树">
            <div class="tree-status" :class="tree.status">
              {{ tree.status === 'AVAILABLE' ? '可认领' : '已认领' }}
            </div>
          </div>
          <div class="tree-info">
            <h3>{{ tree.treeName }}</h3>
            <div class="tree-details">
              <div class="detail-item">
                <el-icon><Location /></el-icon>
                <span>编号：{{ tree.treeNo }}</span>
              </div>
              <div class="detail-item">
                <el-icon><Timer /></el-icon>
                <span>树龄：{{ tree.treeAge }}年</span>
              </div>
              <div class="detail-item">
                <el-icon><Apple /></el-icon>
                <span>品种：{{ tree.variety }}</span>
              </div>
              <div class="detail-item">
                <el-icon><TrendCharts /></el-icon>
                <span>预计产量：{{ tree.estimatedYield }}kg</span>
              </div>
            </div>
            <div class="tree-footer">
              <div class="price">
                <span class="price-label">认领价格</span>
                <span class="price-value">¥{{ tree.pricePerYear }}</span>
                <span class="price-unit">/年</span>
              </div>
              <el-button type="primary" :disabled="tree.status !== 'AVAILABLE'">
                {{ tree.status === 'AVAILABLE' ? '立即认领' : '已被认领' }}
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="loadTrees"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getTreeList } from '@/api/tree'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const trees = ref([])
const currentPage = ref(1)
const pageSize = ref(9)
const total = ref(0)
const statusFilter = ref('')

const loadTrees = async () => {
  loading.value = true
  try {
    const res = await getTreeList({
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      status: statusFilter.value
    })
    trees.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const viewDetail = (id) => {
  router.push(`/tree/${id}`)
}

onMounted(() => {
  loadTrees()
})
</script>

<style lang="scss" scoped>
.tree-list {
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

.filters {
  display: flex;
  justify-content: center;
  margin-bottom: 40px;
}

.trees-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 32px;
  margin-bottom: 48px;
  min-height: 400px;
}

.tree-card {
  cursor: pointer;
  overflow: hidden;
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-8px);
    box-shadow: var(--shadow-lg);
  }
}

.tree-image {
  position: relative;
  height: 240px;
  overflow: hidden;
  background: linear-gradient(135deg, var(--bg-light), var(--accent-color));
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s ease;
  }
  
  &:hover img {
    transform: scale(1.1);
  }
}

.tree-status {
  position: absolute;
  top: 16px;
  right: 16px;
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  background: rgba(255, 255, 255, 0.95);
  
  &.AVAILABLE {
    color: #67c23a;
  }
  
  &.ADOPTED {
    color: #909399;
  }
}

.tree-info {
  padding: 20px;
  
  h3 {
    font-size: 20px;
    color: var(--text-dark);
    margin-bottom: 16px;
    font-weight: 600;
  }
}

.tree-details {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin-bottom: 20px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: var(--text-light);
  
  .el-icon {
    color: var(--primary-color);
  }
}

.tree-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid var(--bg-light);
}

.price {
  display: flex;
  align-items: baseline;
  gap: 4px;
  
  .price-label {
    font-size: 12px;
    color: var(--text-light);
  }
  
  .price-value {
    font-size: 24px;
    font-weight: 700;
    color: var(--primary-color);
  }
  
  .price-unit {
    font-size: 14px;
    color: var(--text-light);
  }
}

.pagination {
  display: flex;
  justify-content: center;
  padding: 40px 0;
}
</style>
