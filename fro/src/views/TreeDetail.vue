<template>
  <div class="tree-detail" v-loading="loading">
    <div class="container" v-if="tree">
      <div class="detail-header">
        <div class="tree-gallery">
          <div class="main-image">
            <img :src="tree.images || '/placeholder-tree.jpg'" alt="桃树">
          </div>
          <div class="panorama-btn" v-if="tree.panoramaUrl">
            <el-button type="primary" @click="viewPanorama">
              <el-icon><View /></el-icon>
              查看360°全景
            </el-button>
          </div>
        </div>
        
        <div class="tree-main-info card">
          <h1 class="title-font">{{ tree.treeName }}</h1>
          <div class="tree-no">编号：{{ tree.treeNo }}</div>
          
          <div class="info-grid">
            <div class="info-item">
              <span class="label">品种</span>
              <span class="value">{{ tree.variety }}</span>
            </div>
            <div class="info-item">
              <span class="label">树龄</span>
              <span class="value">{{ tree.treeAge }}年</span>
            </div>
            <div class="info-item">
              <span class="label">预计产量</span>
              <span class="value">{{ tree.estimatedYield }}kg</span>
            </div>
            <div class="info-item">
              <span class="label">状态</span>
              <span class="value" :class="tree.status">
                {{ tree.status === 'AVAILABLE' ? '可认领' : '已认领' }}
              </span>
            </div>
          </div>
          
          <div class="description">
            <h3>桃树介绍</h3>
            <p>{{ tree.description }}</p>
          </div>
          
          <div class="price-section">
            <div class="price">
              <span class="price-label">认领价格</span>
              <span class="price-value">¥{{ tree.pricePerYear }}</span>
              <span class="price-unit">/年</span>
            </div>
            <el-button 
              type="primary" 
              size="large" 
              :disabled="tree.status !== 'AVAILABLE'"
              @click="handleAdopt">
              {{ tree.status === 'AVAILABLE' ? '立即认领' : '已被认领' }}
            </el-button>
          </div>
        </div>
      </div>
      
      <div class="detail-tabs">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="生长日记" name="diary">
            <div class="diary-list">
              <div class="diary-item card" v-for="diary in diaries" :key="diary.id">
                <div class="diary-date">{{ diary.recordDate }}</div>
                <div class="diary-stage">{{ getStageText(diary.growthStage) }}</div>
                <h3>{{ diary.title }}</h3>
                <p>{{ diary.content }}</p>
                <div class="diary-images" v-if="diary.images">
                  <img v-for="(img, idx) in JSON.parse(diary.images)" :key="idx" :src="img" alt="生长照片">
                </div>
              </div>
              <el-empty v-if="diaries.length === 0" description="暂无生长日记" />
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="认领权益" name="rights">
            <div class="rights-content card">
              <div class="right-item">
                <el-icon class="icon"><CircleCheck /></el-icon>
                <div>
                  <h4>保底产量保障</h4>
                  <p>承诺最低产量{{ tree.estimatedYield }}kg，产量不足将按比例退款</p>
                </div>
              </div>
              <div class="right-item">
                <el-icon class="icon"><CircleCheck /></el-icon>
                <div>
                  <h4>免费包装配送</h4>
                  <p>成熟后免费精美包装，顺丰包邮配送到家</p>
                </div>
              </div>
              <div class="right-item">
                <el-icon class="icon"><CircleCheck /></el-icon>
                <div>
                  <h4>专属生长日记</h4>
                  <p>定期更新桃树生长状态，记录每个重要时刻</p>
                </div>
              </div>
              <div class="right-item">
                <el-icon class="icon"><CircleCheck /></el-icon>
                <div>
                  <h4>会员积分奖励</h4>
                  <p>认领即可获得积分，可兑换礼品或抵扣费用</p>
                </div>
              </div>
              <div class="right-item">
                <el-icon class="icon"><CircleCheck /></el-icon>
                <div>
                  <h4>线下参观体验</h4>
                  <p>可预约到果园实地参观，体验采摘乐趣</p>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getTreeDetail } from '@/api/tree'
import { getTreeDiaries } from '@/api/interaction'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const tree = ref(null)
const diaries = ref([])
const activeTab = ref('diary')

const loadTreeDetail = async () => {
  loading.value = true
  try {
    const res = await getTreeDetail(route.params.id)
    tree.value = res.data
    await loadDiaries()
  } catch (error) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const loadDiaries = async () => {
  try {
    const res = await getTreeDiaries(route.params.id)
    diaries.value = res.data
  } catch (error) {
    console.error('加载生长日记失败', error)
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

const viewPanorama = () => {
  window.open(tree.value.panoramaUrl, '_blank')
}

const handleAdopt = () => {
  if (!userStore.userInfo) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  router.push({
    path: '/my-tree',
    query: { adopt: tree.value.id }
  })
}

onMounted(() => {
  loadTreeDetail()
})
</script>

<style lang="scss" scoped>
.tree-detail {
  min-height: 80vh;
}

.detail-header {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 48px;
  margin-bottom: 48px;
}

.tree-gallery {
  .main-image {
    border-radius: 20px;
    overflow: hidden;
    box-shadow: var(--shadow-md);
    height: 500px;
    background: linear-gradient(135deg, var(--bg-light), var(--accent-color));
    
    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }
  
  .panorama-btn {
    margin-top: 16px;
    text-align: center;
  }
}

.tree-main-info {
  h1 {
    font-size: 36px;
    color: var(--primary-color);
    margin-bottom: 8px;
  }
  
  .tree-no {
    color: var(--text-light);
    margin-bottom: 24px;
  }
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-bottom: 24px;
  padding: 20px;
  background: var(--bg-light);
  border-radius: 12px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  
  .label {
    font-size: 14px;
    color: var(--text-light);
  }
  
  .value {
    font-size: 18px;
    font-weight: 600;
    color: var(--text-dark);
    
    &.AVAILABLE {
      color: #67c23a;
    }
    
    &.ADOPTED {
      color: #909399;
    }
  }
}

.description {
  margin-bottom: 24px;
  
  h3 {
    font-size: 18px;
    color: var(--text-dark);
    margin-bottom: 12px;
  }
  
  p {
    color: var(--text-light);
    line-height: 1.8;
  }
}

.price-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 24px;
  border-top: 2px solid var(--bg-light);
}

.price {
  display: flex;
  align-items: baseline;
  gap: 6px;
  
  .price-label {
    font-size: 14px;
    color: var(--text-light);
  }
  
  .price-value {
    font-size: 36px;
    font-weight: 700;
    color: var(--primary-color);
  }
  
  .price-unit {
    font-size: 16px;
    color: var(--text-light);
  }
}

.detail-tabs {
  margin-top: 48px;
}

.diary-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.diary-item {
  .diary-date {
    color: var(--text-light);
    font-size: 14px;
    margin-bottom: 4px;
  }
  
  .diary-stage {
    display: inline-block;
    padding: 4px 12px;
    background: var(--accent-color);
    color: var(--primary-color);
    border-radius: 12px;
    font-size: 14px;
    margin-bottom: 12px;
  }
  
  h3 {
    font-size: 18px;
    color: var(--text-dark);
    margin-bottom: 8px;
  }
  
  p {
    color: var(--text-light);
    line-height: 1.8;
    margin-bottom: 16px;
  }
}

.diary-images {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  
  img {
    width: 100%;
    height: 150px;
    object-fit: cover;
    border-radius: 8px;
  }
}

.rights-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.right-item {
  display: flex;
  gap: 16px;
  
  .icon {
    font-size: 24px;
    color: var(--primary-color);
    flex-shrink: 0;
  }
  
  h4 {
    font-size: 16px;
    color: var(--text-dark);
    margin-bottom: 4px;
  }
  
  p {
    color: var(--text-light);
    line-height: 1.6;
  }
}
</style>
