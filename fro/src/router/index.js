import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/views/Layout.vue'
import Home from '@/views/Home.vue'
import TreeList from '@/views/TreeList.vue'
import TreeDetail from '@/views/TreeDetail.vue'
import MyTree from '@/views/MyTree.vue'
import Community from '@/views/Community.vue'
import Login from '@/views/Login.vue'
import Register from '@/views/Register.vue'
import Profile from '@/views/Profile.vue'
import Admin from '@/views/Admin.vue'

const routes = [
  {
    path: '/',
    component: Layout,
    children: [
      { path: '', name: 'Home', component: Home },
      { path: 'trees', name: 'TreeList', component: TreeList },
      { path: 'tree/:id', name: 'TreeDetail', component: TreeDetail },
      { path: 'my-tree', name: 'MyTree', component: MyTree },
      { path: 'community', name: 'Community', component: Community },
      { path: 'profile', name: 'Profile', component: Profile },
      { path: 'admin', name: 'Admin', component: Admin }
    ]
  },
  { path: '/login', name: 'Login', component: Login },
  { path: '/register', name: 'Register', component: Register }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
