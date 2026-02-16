<template>
  <div class="layout">
    <header class="header">
      <div class="container">
        <div class="header-content">
          <div class="logo title-font" @click="$router.push('/')">
            <span class="peach-icon">🍑</span>
            桃醉心生
          </div>
          <nav class="nav">
            <router-link to="/" class="nav-item">首页</router-link>
            <router-link to="/trees" class="nav-item">认领桃树</router-link>
            <router-link to="/my-tree" class="nav-item" v-if="userStore.userInfo">我的桃树</router-link>
            <router-link to="/community" class="nav-item">树主社区</router-link>
            <router-link to="/admin" class="nav-item admin-link" v-if="userStore.userInfo?.role === 'ADMIN'">
              <el-icon><Setting /></el-icon>
              管理后台
            </router-link>
          </nav>
          <div class="user-actions">
            <template v-if="userStore.userInfo">
              <div class="user-info" @click="$router.push('/profile')">
                <el-avatar :src="userStore.userInfo.avatar" :size="36">
                  {{ userStore.userInfo.nickname?.[0] }}
                </el-avatar>
                <span class="username">{{ userStore.userInfo.nickname }}</span>
              </div>
              <el-button @click="handleLogout" text>退出</el-button>
            </template>
            <template v-else>
              <el-button @click="$router.push('/login')" text>登录</el-button>
              <el-button type="primary" @click="$router.push('/register')">注册</el-button>
            </template>
          </div>
        </div>
      </div>
    </header>
    
    <main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
    
    <footer class="footer">
      <div class="container">
        <div class="footer-content">
          <div class="footer-section">
            <h3 class="title-font">桃醉心生</h3>
            <p>认领一颗小桃树，收获一份甜蜜</p>
          </div>
          <div class="footer-section">
            <h4>联系我们</h4>
            <p>电话：400-123-4567</p>
            <p>邮箱：contact@peachtree.com</p>
          </div>
          <div class="footer-section">
            <h4>关注我们</h4>
            <p>微信公众号：桃醉心生</p>
          </div>
        </div>
        <div class="copyright">
          © 2024 桃醉心生. All rights reserved.
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const router = useRouter()

const handleLogout = () => {
  userStore.logout()
  ElMessage.success('退出成功')
  router.push('/')
}
</script>

<style lang="scss" scoped>
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background: linear-gradient(135deg, #fff 0%, #fff5f8 100%);
  box-shadow: var(--shadow-sm);
  position: sticky;
  top: 0;
  z-index: 100;
  backdrop-filter: blur(10px);
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 0;
}

.logo {
  font-size: 28px;
  font-weight: 700;
  color: var(--primary-color);
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: transform 0.3s ease;
  
  &:hover {
    transform: scale(1.05);
  }
}

.peach-icon {
  font-size: 32px;
  animation: float 3s ease-in-out infinite;
}

.nav {
  display: flex;
  gap: 32px;
}

.nav-item {
  color: var(--text-dark);
  text-decoration: none;
  font-weight: 500;
  position: relative;
  transition: color 0.3s ease;
  display: flex;
  align-items: center;
  gap: 4px;
  
  &:hover {
    color: var(--primary-color);
  }
  
  &.router-link-active {
    color: var(--primary-color);
    
    &::after {
      content: '';
      position: absolute;
      bottom: -4px;
      left: 0;
      right: 0;
      height: 2px;
      background: var(--primary-color);
      border-radius: 2px;
    }
  }
  
  &.admin-link {
    color: var(--dark-pink);
    font-weight: 600;
    
    &:hover {
      color: var(--primary-color);
    }
  }
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 12px;
  border-radius: 20px;
  transition: background 0.3s ease;
  
  &:hover {
    background: var(--bg-light);
  }
}

.username {
  font-weight: 500;
  color: var(--text-dark);
}

.main-content {
  flex: 1;
  padding: 40px 0;
}

.footer {
  background: linear-gradient(135deg, var(--text-dark) 0%, #1a0d1b 100%);
  color: white;
  padding: 48px 0 24px;
  margin-top: 80px;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 48px;
  margin-bottom: 32px;
}

.footer-section {
  h3, h4 {
    margin-bottom: 16px;
    color: var(--secondary-color);
  }
  
  p {
    margin-bottom: 8px;
    opacity: 0.8;
  }
}

.copyright {
  text-align: center;
  padding-top: 24px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  opacity: 0.6;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
