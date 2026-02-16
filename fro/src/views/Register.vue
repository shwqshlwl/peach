<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-left">
        <div class="brand">
          <div class="brand-icon">🍑</div>
          <h1 class="title-font">桃醉心生</h1>
          <p>认领一颗小桃树，收获一份甜蜜</p>
        </div>
      </div>
      
      <div class="register-right">
        <div class="register-form-wrapper">
          <h2>创建账户</h2>
          <p class="form-subtitle">开启您的桃树之旅</p>
          
          <el-form :model="registerForm" class="register-form">
            <el-form-item>
              <el-input 
                v-model="registerForm.username" 
                placeholder="用户名"
                size="large"
                prefix-icon="User">
              </el-input>
            </el-form-item>
            
            <el-form-item>
              <el-input 
                v-model="registerForm.nickname" 
                placeholder="昵称"
                size="large"
                prefix-icon="Avatar">
              </el-input>
            </el-form-item>
            
            <el-form-item>
              <el-input 
                v-model="registerForm.phone" 
                placeholder="手机号"
                size="large"
                prefix-icon="Phone">
              </el-input>
            </el-form-item>
            
            <el-form-item>
              <el-input 
                v-model="registerForm.password" 
                type="password"
                placeholder="密码"
                size="large"
                prefix-icon="Lock">
              </el-input>
            </el-form-item>
            
            <el-form-item>
              <el-input 
                v-model="confirmPassword" 
                type="password"
                placeholder="确认密码"
                size="large"
                prefix-icon="Lock"
                @keyup.enter="handleRegister">
              </el-input>
            </el-form-item>
            
            <el-button 
              type="primary" 
              size="large" 
              :loading="loading"
              @click="handleRegister"
              class="register-btn">
              注册
            </el-button>
          </el-form>
          
          <div class="form-footer">
            <span>已有账户？</span>
            <el-button text @click="$router.push('/login')">
              立即登录
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
import { register } from '@/api/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const confirmPassword = ref('')

const registerForm = reactive({
  username: '',
  nickname: '',
  phone: '',
  password: ''
})

const handleRegister = async () => {
  if (!registerForm.username || !registerForm.password || !registerForm.nickname) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  if (registerForm.password !== confirmPassword.value) {
    ElMessage.warning('两次密码输入不一致')
    return
  }
  
  loading.value = true
  try {
    await register(registerForm)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    ElMessage.error('注册失败，用户名可能已存在')
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #fff5f8 0%, #ffe8f0 50%, #ffd4e5 100%);
  padding: 20px;
}

.register-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  max-width: 1000px;
  width: 100%;
  background: white;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: var(--shadow-lg);
}

.register-left {
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

.register-right {
  padding: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.register-form-wrapper {
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

.register-form {
  .el-form-item {
    margin-bottom: 20px;
  }
  
  .register-btn {
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
