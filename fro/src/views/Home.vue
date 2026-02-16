<template>
  <div class="home">
    <section class="hero">
      <div class="container">
        <div class="hero-content fade-in-up">
          <h1 class="hero-title title-font">
            桃醉心生
            <span class="subtitle">认领一颗小桃树，收获一份甜蜜</span>
          </h1>
          <p class="hero-desc">
            在这里，您可以拥有属于自己的桃树，见证它从开花到结果的每一个瞬间
          </p>
          <div class="hero-actions">
            <button class="btn-primary" @click="$router.push('/trees')">
              立即认领
            </button>
            <button class="btn-secondary" @click="scrollToFeatures">
              了解更多
            </button>
          </div>
        </div>
        <div class="hero-image float">
          <div class="peach-tree-illustration">🌸🍑🌳</div>
        </div>
      </div>
    </section>

    <section class="features" ref="featuresRef">
      <div class="container">
        <h2 class="section-title title-font">为什么选择我们</h2>
        <div class="features-grid">
          <div class="feature-card card" v-for="(feature, index) in features" :key="index"
               :style="{ animationDelay: `${index * 0.1}s` }">
            <div class="feature-icon">{{ feature.icon }}</div>
            <h3>{{ feature.title }}</h3>
            <p>{{ feature.desc }}</p>
          </div>
        </div>
      </div>
    </section>

    <section class="process">
      <div class="container">
        <h2 class="section-title title-font">认领流程</h2>
        <div class="process-steps">
          <div class="step" v-for="(step, index) in steps" :key="index">
            <div class="step-number">{{ index + 1 }}</div>
            <div class="step-content">
              <h3>{{ step.title }}</h3>
              <p>{{ step.desc }}</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="cta">
      <div class="container">
        <div class="cta-content">
          <h2 class="title-font">开启您的桃树之旅</h2>
          <p>现在认领，享受早鸟优惠</p>
          <button class="btn-primary" @click="$router.push('/trees')">
            开始认领
          </button>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const featuresRef = ref(null)

const features = [
  { icon: '🌳', title: '实时监控', desc: '通过高清摄像头，随时查看您的桃树生长状态' },
  { icon: '📝', title: '生长日记', desc: '专业果农定期更新，记录桃树成长的每一刻' },
  { icon: '🎁', title: '保底产量', desc: '承诺最低产量，让您的投资有保障' },
  { icon: '🚚', title: '免费配送', desc: '成熟后免费配送到家，新鲜直达' },
  { icon: '💎', title: '会员特权', desc: '积分兑换、线下活动等多重会员福利' },
  { icon: '👥', title: '专属社区', desc: '加入树主社区，分享种植乐趣' }
]

const steps = [
  { title: '选择桃树', desc: '浏览果园，选择心仪的桃树' },
  { title: '完成认领', desc: '支付认领费用，获得专属权益' },
  { title: '见证成长', desc: '通过生长日记，关注桃树成长' },
  { title: '收获果实', desc: '桃子成熟后，免费配送到家' }
]

const scrollToFeatures = () => {
  featuresRef.value?.scrollIntoView({ behavior: 'smooth' })
}
</script>

<style lang="scss" scoped>
.hero {
  background: linear-gradient(135deg, #fff5f8 0%, #ffe8f0 50%, #ffd4e5 100%);
  padding: 80px 0;
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '🌸';
    position: absolute;
    font-size: 200px;
    opacity: 0.1;
    top: -50px;
    right: -50px;
    animation: float 6s ease-in-out infinite;
  }
  
  .container {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 60px;
    align-items: center;
  }
}

.hero-content {
  z-index: 1;
}

.hero-title {
  font-size: 56px;
  color: var(--primary-color);
  margin-bottom: 16px;
  line-height: 1.2;
  
  .subtitle {
    display: block;
    font-size: 24px;
    color: var(--text-light);
    margin-top: 12px;
  }
}

.hero-desc {
  font-size: 18px;
  color: var(--text-light);
  margin-bottom: 32px;
  line-height: 1.8;
}

.hero-actions {
  display: flex;
  gap: 16px;
}

.btn-secondary {
  background: white;
  color: var(--primary-color);
  border: 2px solid var(--primary-color);
  padding: 12px 32px;
  border-radius: 50px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    background: var(--primary-color);
    color: white;
    transform: translateY(-2px);
  }
}

.hero-image {
  display: flex;
  justify-content: center;
  align-items: center;
}

.peach-tree-illustration {
  font-size: 120px;
  text-align: center;
  line-height: 1.5;
}

.section-title {
  text-align: center;
  font-size: 42px;
  color: var(--primary-color);
  margin-bottom: 48px;
}

.features {
  padding: 80px 0;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 32px;
}

.feature-card {
  text-align: center;
  animation: fadeInUp 0.6s ease-out both;
  
  .feature-icon {
    font-size: 64px;
    margin-bottom: 16px;
  }
  
  h3 {
    font-size: 20px;
    color: var(--text-dark);
    margin-bottom: 12px;
  }
  
  p {
    color: var(--text-light);
    line-height: 1.6;
  }
}

.process {
  padding: 80px 0;
  background: var(--bg-white);
}

.process-steps {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 32px;
}

.step {
  text-align: center;
  position: relative;
  
  &:not(:last-child)::after {
    content: '→';
    position: absolute;
    right: -32px;
    top: 30px;
    font-size: 24px;
    color: var(--secondary-color);
  }
}

.step-number {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, var(--primary-color), var(--dark-pink));
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 700;
  margin: 0 auto 16px;
  box-shadow: var(--shadow-md);
}

.step-content {
  h3 {
    font-size: 18px;
    color: var(--text-dark);
    margin-bottom: 8px;
  }
  
  p {
    color: var(--text-light);
    font-size: 14px;
  }
}

.cta {
  padding: 80px 0;
  background: linear-gradient(135deg, var(--primary-color), var(--dark-pink));
  color: white;
}

.cta-content {
  text-align: center;
  
  h2 {
    font-size: 42px;
    margin-bottom: 16px;
  }
  
  p {
    font-size: 20px;
    margin-bottom: 32px;
    opacity: 0.9;
  }
  
  .btn-primary {
    background: white;
    color: var(--primary-color);
    
    &:hover {
      transform: translateY(-2px) scale(1.05);
    }
  }
}
</style>
