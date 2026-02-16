<template>
  <div class="community">
    <div class="container">
      <div class="page-header">
        <h1 class="title-font">树主社区</h1>
        <p class="subtitle">分享您的桃树故事，交流种植心得</p>
        <el-button type="primary" @click="showPostDialog = true" v-if="userStore.userInfo">
          <el-icon><Edit /></el-icon>
          发布动态
        </el-button>
      </div>

      <div class="posts-list" v-loading="loading">
        <div class="post-card card" v-for="post in posts" :key="post.id">
          <div class="post-header">
            <div class="author-info">
              <el-avatar :src="post.avatar" :size="48">
                {{ post.nickname?.[0] || post.username?.[0] || '用' }}
              </el-avatar>
              <div class="author-details">
                <div class="author-name">{{ post.nickname || post.username || '匿名用户' }}</div>
                <div class="post-time">{{ formatTime(post.createTime) }}</div>
              </div>
            </div>
          </div>

          <div class="post-content">
            <h3 v-if="post.title">{{ post.title }}</h3>
            <!-- 富文本内容显示 -->
            <div class="rich-content" v-html="post.content"></div>
          </div>

          <div class="post-actions">
            <div 
              class="action-btn" 
              :class="{ 'liked': post.hasLiked }"
              @click="likePost(post)">
              <el-icon><Star /></el-icon>
              <span>{{ post.likeCount }}</span>
            </div>
            <div class="action-btn" @click="showComments(post)">
              <el-icon><ChatDotRound /></el-icon>
              <span>{{ post.commentCount }}</span>
            </div>
            <div class="action-btn">
              <el-icon><Share /></el-icon>
              <span>分享</span>
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
          @current-change="loadPosts"
        />
      </div>
    </div>

    <!-- 发布动态对话框 -->
    <el-dialog v-model="showPostDialog" title="发布动态" width="800px" :close-on-click-modal="false">
      <el-form :model="postForm" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="postForm.title" placeholder="给您的动态起个标题（可选）" />
        </el-form-item>
        <el-form-item label="内容">
          <!-- 富文本编辑器 -->
          <div style="border: 1px solid #ccc; width: 100%;">
            <Toolbar
              style="border-bottom: 1px solid #ccc"
              :editor="editorRef"
              :defaultConfig="toolbarConfig"
              mode="default"
            />
            <Editor
              style="height: 400px; overflow-y: hidden;"
              v-model="postForm.content"
              :defaultConfig="editorConfig"
              mode="default"
              @onCreated="handleCreated"
            />
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="closeDialog">取消</el-button>
        <el-button type="primary" @click="publishPost">发布</el-button>
      </template>
    </el-dialog>

    <!-- 评论对话框 -->
    <el-dialog v-model="showCommentDialog" title="评论" width="700px">
      <div class="comments-section">
        <div class="comment-list" v-loading="loadingComments">
          <div class="comment-item" v-for="comment in comments" :key="comment.id">
            <el-avatar :src="comment.avatar" :size="40">
              {{ comment.nickname?.[0] || comment.username?.[0] || '用' }}
            </el-avatar>
            <div class="comment-content">
              <div class="comment-header">
                <span class="comment-author">{{ comment.nickname || comment.username }}</span>
                <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
              </div>
              <!-- 回复提示 -->
              <div class="reply-to" v-if="comment.parentNickname">
                回复 <span class="reply-name">@{{ comment.parentNickname }}</span>
              </div>
              <div class="comment-text">{{ comment.content }}</div>
              <!-- 回复按钮 -->
              <el-button 
                text 
                size="small" 
                @click="replyToComment(comment)"
                v-if="userStore.userInfo">
                回复
              </el-button>
            </div>
          </div>
          <el-empty v-if="comments.length === 0" description="暂无评论" />
        </div>
        
        <div class="comment-input" v-if="userStore.userInfo">
          <!-- 回复提示 -->
          <div class="replying-to" v-if="replyingTo">
            <span>回复 @{{ replyingTo.nickname || replyingTo.username }}</span>
            <el-button text @click="cancelReply">取消</el-button>
          </div>
          
          <el-input
            v-model="commentContent"
            type="textarea"
            :rows="3"
            :placeholder="replyingTo ? `回复 @${replyingTo.nickname || replyingTo.username}` : '写下你的评论...'"
            maxlength="500"
            show-word-limit
          />
          
          <div class="comment-actions">
            <span></span>
            <el-button type="primary" @click="submitComment">
              发表评论
            </el-button>
          </div>
        </div>
        <div v-else class="login-tip">
          <el-button text @click="$router.push('/login')">登录后发表评论</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, shallowRef, onBeforeUnmount } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import '@wangeditor/editor/dist/css/style.css'

const userStore = useUserStore()
const loading = ref(false)
const posts = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const showPostDialog = ref(false)
const showCommentDialog = ref(false)
const loadingComments = ref(false)
const comments = ref([])
const commentContent = ref('')
const currentPost = ref(null)
const replyingTo = ref(null)
const postForm = reactive({
  title: '',
  content: ''
})

// 富文本编辑器
const editorRef = shallowRef()
const toolbarConfig = {
  excludeKeys: ['group-video'] // 排除视频功能
}
const editorConfig = {
  placeholder: '分享您的桃树故事...',
  MENU_CONF: {
    uploadImage: {
      // 自定义图片上传
      async customUpload(file, insertFn) {
        // 这里可以对接图片上传接口
        // 暂时使用base64
        const reader = new FileReader()
        reader.onload = (e) => {
          insertFn(e.target.result, file.name, e.target.result)
        }
        reader.readAsDataURL(file)
      }
    }
  }
}

const handleCreated = (editor) => {
  editorRef.value = editor
}

const loadPosts = async () => {
  loading.value = true
  try {
    const res = await request.get('/community/posts', {
      params: {
        pageNum: currentPage.value,
        pageSize: pageSize.value
      }
    })
    posts.value = res.data.records
    total.value = res.data.total
    
    // 加载点赞状态
    if (userStore.userInfo) {
      for (const post of posts.value) {
        const likeRes = await request.get('/community/like/status', {
          params: {
            postId: post.id,
            userId: userStore.userInfo.id
          }
        })
        post.hasLiked = likeRes.data
      }
    }
  } catch (error) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const publishPost = async () => {
  if (!postForm.content || postForm.content === '<p><br></p>') {
    ElMessage.warning('请输入内容')
    return
  }
  
  try {
    const data = {
      title: postForm.title,
      content: postForm.content,
      userId: userStore.userInfo.id
    }
    await request.post('/community/post', data)
    ElMessage.success('发布成功')
    closeDialog()
    // 延迟加载，避免卡死
    setTimeout(() => {
      loadPosts()
    }, 300)
  } catch (error) {
    ElMessage.error('发布失败')
  }
}

const closeDialog = () => {
  showPostDialog.value = false
  postForm.title = ''
  postForm.content = ''
  // 清空编辑器
  if (editorRef.value) {
    editorRef.value.clear()
  }
}

const likePost = async (post) => {
  if (!userStore.userInfo) {
    ElMessage.warning('请先登录')
    return
  }
  
  try {
    const res = await request.post(`/community/like/${post.id}`, null, {
      params: { userId: userStore.userInfo.id }
    })
    post.hasLiked = res.data.liked
    ElMessage.success(res.data.message)
    loadPosts()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const showComments = async (post) => {
  currentPost.value = post
  showCommentDialog.value = true
  replyingTo.value = null
  commentContent.value = ''
  await loadComments(post.id)
}

const replyToComment = (comment) => {
  replyingTo.value = comment
  commentContent.value = ''
}

const cancelReply = () => {
  replyingTo.value = null
}

const parseImages = (images) => {
  try {
    return JSON.parse(images)
  } catch {
    return []
  }
}

const previewImage = (url) => {
  window.open(url, '_blank')
}

const loadComments = async (postId) => {
  loadingComments.value = true
  try {
    const res = await request.get(`/comments/post/${postId}`)
    comments.value = res.data
  } catch (error) {
    ElMessage.error('加载评论失败')
  } finally {
    loadingComments.value = false
  }
}

const submitComment = async () => {
  if (!commentContent.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  
  try {
    await request.post('/comments/add', {
      postId: currentPost.value.id,
      userId: userStore.userInfo.id,
      content: commentContent.value,
      parentId: replyingTo.value ? replyingTo.value.id : null
    })
    ElMessage.success('评论成功')
    commentContent.value = ''
    replyingTo.value = null
    await loadComments(currentPost.value.id)
    // 更新帖子列表中的评论数
    loadPosts()
  } catch (error) {
    ElMessage.error('评论失败')
  }
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  return date.toLocaleDateString('zh-CN')
}

onMounted(() => {
  loadPosts()
})

onBeforeUnmount(() => {
  if (editorRef.value) {
    editorRef.value.destroy()
  }
})
</script>

<style lang="scss" scoped>
.community {
  min-height: 80vh;
}

.page-header {
  text-align: center;
  margin-bottom: 48px;
  position: relative;
  
  h1 {
    font-size: 48px;
    color: var(--primary-color);
    margin-bottom: 12px;
  }
  
  .subtitle {
    font-size: 18px;
    color: var(--text-light);
    margin-bottom: 24px;
  }
  
  .el-button {
    position: absolute;
    right: 0;
    top: 50%;
    transform: translateY(-50%);
  }
}

.posts-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
  margin-bottom: 48px;
  min-height: 400px;
}

.post-card {
  padding: 24px;
}

.post-header {
  margin-bottom: 20px;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-details {
  .author-name {
    font-weight: 600;
    color: var(--text-dark);
    margin-bottom: 4px;
  }
  
  .post-time {
    font-size: 14px;
    color: var(--text-light);
  }
}

.post-content {
  margin-bottom: 20px;
  
  h3 {
    font-size: 20px;
    color: var(--text-dark);
    margin-bottom: 12px;
  }
  
  .rich-content {
    color: var(--text-light);
    line-height: 1.8;
    
    // 富文本内容样式
    :deep(p) {
      margin: 12px 0;
      line-height: 1.8;
    }
    
    :deep(img) {
      max-width: 100%;
      height: auto;
      border-radius: 12px;
      margin: 16px 0;
      cursor: pointer;
      transition: transform 0.3s ease;
      
      &:hover {
        transform: scale(1.02);
      }
    }
    
    :deep(h1), :deep(h2), :deep(h3), :deep(h4), :deep(h5), :deep(h6) {
      color: var(--text-dark);
      margin: 16px 0 12px;
      font-weight: 600;
    }
    
    :deep(ul), :deep(ol) {
      margin: 12px 0;
      padding-left: 24px;
    }
    
    :deep(blockquote) {
      border-left: 4px solid var(--primary-color);
      padding-left: 16px;
      margin: 16px 0;
      color: var(--text-light);
      font-style: italic;
    }
    
    :deep(code) {
      background: var(--bg-light);
      padding: 2px 6px;
      border-radius: 4px;
      font-family: 'Courier New', monospace;
    }
    
    :deep(pre) {
      background: var(--bg-light);
      padding: 16px;
      border-radius: 8px;
      overflow-x: auto;
      margin: 16px 0;
      
      code {
        background: none;
        padding: 0;
      }
    }
    
    :deep(a) {
      color: var(--primary-color);
      text-decoration: none;
      
      &:hover {
        text-decoration: underline;
      }
    }
    
    :deep(table) {
      width: 100%;
      border-collapse: collapse;
      margin: 16px 0;
      
      th, td {
        border: 1px solid var(--bg-light);
        padding: 8px 12px;
        text-align: left;
      }
      
      th {
        background: var(--bg-light);
        font-weight: 600;
      }
    }
  }
}

.post-actions {
  display: flex;
  gap: 32px;
  padding-top: 16px;
  border-top: 1px solid var(--bg-light);
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--text-light);
  cursor: pointer;
  transition: color 0.3s ease;
  
  &:hover {
    color: var(--primary-color);
  }
  
  &.liked {
    color: var(--primary-color);
    
    .el-icon {
      animation: heartbeat 0.3s ease;
    }
  }
  
  .el-icon {
    font-size: 18px;
  }
}

@keyframes heartbeat {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.2); }
}

.pagination {
  display: flex;
  justify-content: center;
  padding: 40px 0;
}

.comments-section {
  max-height: 600px;
  display: flex;
  flex-direction: column;
}

.comment-list {
  flex: 1;
  overflow-y: auto;
  margin-bottom: 20px;
  max-height: 400px;
}

.comment-item {
  display: flex;
  gap: 12px;
  padding: 16px;
  border-bottom: 1px solid var(--bg-light);
  
  &:last-child {
    border-bottom: none;
  }
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.comment-author {
  font-weight: 600;
  color: var(--text-dark);
}

.comment-time {
  font-size: 12px;
  color: var(--text-light);
}

.comment-text {
  color: var(--text-light);
  line-height: 1.6;
  word-break: break-word;
  margin-bottom: 8px;
}

.reply-to {
  font-size: 14px;
  color: var(--text-light);
  margin-bottom: 6px;
  
  .reply-name {
    color: var(--primary-color);
    font-weight: 500;
  }
}

.replying-to {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: var(--bg-light);
  border-radius: 8px;
  margin-bottom: 12px;
  font-size: 14px;
  color: var(--text-dark);
}

.comment-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
}

.comment-input {
  padding-top: 16px;
  border-top: 1px solid var(--bg-light);
}

.login-tip {
  text-align: center;
  padding: 20px;
  color: var(--text-light);
}
</style>
