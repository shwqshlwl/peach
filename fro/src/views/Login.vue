<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-left">
        <div class="brand">
          <div class="brand-icon">🍑</div>
          <h1 class="title-font">桃醉心生</h1>
          <p>认领一颗小桃树，收获一份甜蜜</p>
        </div>
      </div>
      
      <div class="login-right">
        <div class="login-form-wrapper">
          <h2>欢迎回来</h2>
          <p class="form-subtitle">登录您的账户</p>
          
          <el-form :model="loginForm" class="login-form">
            <el-form-item>
              <el-input 
                v-model="loginForm.username" 
                placeholder="用户名"
                size="large"
                prefix-icon="User">
              </el-input>
            </el-form-item>
            
            <el-form-item>
              <el-input 
                v-model="loginForm.password" 
                type="password"
                placeholder="密码"
                size="large"
                prefix-icon="Lock"
                @keyup.enter="handleLogin">
              </el-input>
            </el-form-item>
            
            <el-button 
              type="primary" 
              size="large" 
              :loading="loading"
              @click="handleLogin"
              class="login-btn">
              登录
            </el-button>
          </el-form>
          
          <div class="form-footer">
            <span>还没有账户？</span>
            <el-button text @click="$router.push('/register')">
              立即注册
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { login } from '@/api/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const handleLogin = async () => {
  if (!loginForm.username || !loginForm.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  
  loading.value = true
  try {
    const res = await login(loginForm)
    userStore.setUser(res.data)
    userStore.setToken('token-' + res.data.id)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) {
    ElMessage.error('登录失败，请检查用户名和密码')
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #fff5f8 0%, #ffe8f0 50%, #ffd4e5 100%);
  padding: 20px;
}

.login-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  max-width: 1000px;
  width: 100%;
  background: white;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: var(--shadow-lg);
}

.login-left {
  background: linear-gradient(135deg, var(--primary-color), var(--dark-pink));
  color: white;
  padding: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.brand {
  text-align: center;
  
  .brand-icon {
    font-size: 80px;
    margin-bottom: 24px;
    animation: float 3s ease-in-out infinite;
  }
  
  h1 {
    font-size: 42px;
    margin-bottom: 16px;
  }
  
  p {
    font-size: 18px;
    opacity: 0.9;
  }
}

.login-right {
  padding: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-form-wrapper {
  width: 100%;
  max-width: 360px;
  
  h2 {
    font-size: 32px;
    color: var(--text-dark);
    margin-bottom: 8px;
  }
  
  .form-subtitle {
    color: var(--text-light);
    margin-bottom: 32px;
  }
}

.login-form {
  .el-form-item {
    margin-bottom: 24px;
  }
  
  .login-btn {
    width: 100%;
    margin-top: 8px;
  }
}

.form-footer {
  text-align: center;
  margin-top: 24px;
  color: var(--text-light);
  
  span {
    margin-right: 8px;
  }
}
</style>
