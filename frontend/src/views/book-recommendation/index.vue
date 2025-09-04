<template>
  <div class="book-recommendation-container">
    <div class="chat-header">
      <h2>📚 图书推荐助手</h2>
      <p>告诉我您的阅读偏好，我将为您推荐合适的书籍</p>
    </div>
    
    <div class="chat-messages">
      <!-- 欢迎消息 -->
      <div v-if="messages.length === 0" class="welcome-message">
        <div class="message-bubble assistant">
          <p style="font-size: 16px; font-weight: bold; margin: 0 0 5px 0;">您好！我是您的图书推荐助手。</p>
          <p style="margin: 0;">请问您最近喜欢阅读哪类书籍？（例如：小说、历史、科学等）</p>
        </div>
      </div>
      
      <!-- 聊天消息列表 -->
      <div v-for="(message, index) in messages" :key="index" class="message-wrapper">
        <div v-if="message.role === 'user'" class="message-bubble user">
          <p style="margin: 0; color: #303133;">{{ message.content }}</p>
        </div>
        <div v-else-if="message.role === 'assistant'" class="message-bubble assistant">
          <div v-if="!message.isRecommendation" class="question-text">{{ message.content }}</div>
          <div v-else class="recommendation-content">
            <h4 style="text-align: center;">📚 为您推荐的图书</h4>
            <div class="recommendation-result">
              <p style="margin-top: 0;"><strong>推荐结果：</strong></p>
              <div class="recommendation-text">{{ message.content }}</div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-message">
        <div class="message-bubble assistant">
          <div class="loading-container">
            <i class="el-icon-loading" style="font-size: 24px; color: #409EFF;"></i>
            <p style="font-size: 14px; font-weight: normal; margin-top: 8px;">正在为您生成个性化推荐，请稍候...</p>
          </div>
        </div>
      </div>
    </div>
    
    <div class="chat-input-area">
      <el-input
        v-model="currentInput"
        type="textarea"
        :rows="3"
        placeholder="请输入您的回答..."
        :disabled="loading"
        @keyup.enter.native="handleKeyPress"
        style="border-radius: 8px;"
      ></el-input>
      <div class="input-hints">
        <p class="hint-text" style="text-align: right;">提示：按 Ctrl+Enter 或 Shift+Enter 发送消息</p>
      </div>
      <div class="input-actions">
        <el-button 
          type="primary" 
          @click="sendMessage"
          :disabled="!currentInput.trim() || loading"
          style="margin-top: 10px; border-radius: 20px;"
        >
          {{ loading ? '发送中...' : '发送' }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { recommendBooks } from '@/api/book'

export default {
  name: 'BookRecommendation',
  data() {
    return {
      currentInput: '',
      messages: [],
      loading: false,
      currentStep: 0,
      userResponses: [],
      questions: [
        { content: '您好！我是您的图书推荐助手。请问您最近喜欢阅读哪类书籍？（例如：小说、历史、科学等）' },
        { content: '您希望从阅读中获得什么？（例如：知识、放松、灵感等）' },
        { content: '您是否有偏好的作者或作品？' },
        { content: '您更喜欢哪种阅读方式？（例如：纸质书、电子书、有声书等）' }
      ]
    }
  },
  mounted() {
    // 初始化页面时自动显示第一个问题
    if (this.messages.length === 0) {
      this.addAssistantMessage(this.questions[0].content)
    }
  },
  methods: {
    handleKeyPress(e) {
      // 按下Ctrl+Enter或Shift+Enter发送消息，普通Enter不发送（避免换行时误触）
      if ((e.ctrlKey || e.shiftKey) && e.key === 'Enter') {
        this.sendMessage()
      }
    },
    
    sendMessage() {
      if (!this.currentInput.trim() || this.loading) {
        return
      }
      
      // 添加用户消息
      const userMessage = this.currentInput.trim()
      this.addUserMessage(userMessage)
      this.userResponses.push({
        question: this.questions[this.currentStep].content,
        answer: userMessage
      })
      
      // 清空输入框
      this.currentInput = ''
      
      // 检查是否需要获取推荐结果
      if (this.currentStep < this.questions.length - 1) {
        // 还有问题要问
        this.currentStep++
        setTimeout(() => {
          this.addAssistantMessage(this.questions[this.currentStep].content)
        }, 500)
      } else {
        // 开始获取推荐结果
        this.getBookRecommendations()
      }
    },
    
    addUserMessage(content) {
      this.messages.push({
        role: 'user',
        content: content
      })
      this.scrollToBottom()
    },
    
    addAssistantMessage(content, isRecommendation = false) {
      this.messages.push({
        role: 'assistant',
        content: content,
        isRecommendation: isRecommendation
      })
      this.scrollToBottom()
    },
    
    scrollToBottom() {
      // 在下一个渲染周期滚动到底部
      this.$nextTick(() => {
        const chatContainer = this.$el.querySelector('.chat-messages')
        if (chatContainer) {
          chatContainer.scrollTop = chatContainer.scrollHeight
        }
      })
    },
    
    async getBookRecommendations() {
      try {
        this.loading = true
        
        // 构建用户问答文本
        let userText = '用户问答信息：\n'
        this.userResponses.forEach((item, index) => {
          userText += `${index + 1}. ${item.question}\n   用户回答：${item.answer}\n`
        })
        
        const response = await recommendBooks({ userInfo: userText })
        this.addAssistantMessage(response.data || '暂无推荐结果', true)
      } catch (error) {
        console.error('获取推荐失败:', error)
        this.addAssistantMessage('获取推荐失败，请稍后再试', true)
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.book-recommendation-container {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 50px);
  background: linear-gradient(to bottom, #f5f7fa, #e4e7ed);
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  margin: 10px;
}

.chat-header {
  padding: 20px;
  background-color: #fff;
  border-bottom: 1px solid #e4e7ed;
  text-align: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  border-radius: 0 0 10px 10px;
  margin: 10px 10px 0 10px;
}

.chat-header h2 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 24px;
}

.chat-header p {
  margin: 0;
  color: #606266;
  font-size: 14px;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 15px;
  background: linear-gradient(to bottom, #f0f2f5, #e1e5ea);
  border-bottom: 1px solid #dcdfe6;
  border-radius: 10px;
  margin: 0 10px 10px 10px;
}

.welcome-message {
  text-align: center;
  margin-top: 20px;
}

.message-wrapper {
  display: flex;
}

.message-wrapper:nth-child(odd) {
  justify-content: flex-start;
}

.message-wrapper:nth-child(even) {
  justify-content: flex-end;
}

.message-bubble {
  max-width: 80%;
  padding: 12px 16px;
  border-radius: 10px;
  word-wrap: break-word;
  line-height: 1.6;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.user {
  background-color: #ffffff;
  border: 1px solid #e4e7ed;
  align-self: flex-end;
  border-bottom-right-radius: 15px;
  border-top-right-radius: 15px;
  border-top-left-radius: 15px;
}

.assistant {
  background-color: #409eff;
  color: white;
  align-self: flex-start;
  border-bottom-left-radius: 15px;
  border-top-left-radius: 15px;
  border-top-right-radius: 15px;
}

.loading-message {
  justify-content: flex-start;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  text-align: center;
}

.loading-container p {
  margin-top: 10px;
  margin-bottom: 0;
}

.question-text {
  margin: 0;
  line-height: 1.6;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
}

.recommendation-content h4 {
  margin-top: 0;
  margin-bottom: 10px;
  font-size: 18px;
  font-weight: bold;
  color: #303133;
  border-bottom: 1px solid rgba(255, 255, 255, 0.3);
  padding-bottom: 5px;
}

.recommendation-result {
  background-color: rgba(255, 255, 255, 0.1);
  padding: 10px;
  border-radius: 8px;
  max-height: 400px;
  overflow-y: auto;
}

.recommendation-text {
  white-space: pre-wrap;
  font-size: 14px;
  line-height: 1.6;
  color: #303133;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
}

.chat-input-area {
  padding: 20px;
  background-color: #fff;
  border-top: 1px solid #e4e7ed;
  box-shadow: 0 -2px 4px rgba(0, 0, 0, 0.05);
  border-radius: 10px 10px 0 0;
  margin: 0 10px 10px 10px;
}

.input-hints {
  margin-top: 5px;
  margin-bottom: 5px;
}

.hint-text {
  font-size: 12px;
  color: #909399;
  margin: 0;
  font-style: italic;
}

.input-actions {
  display: flex;
  justify-content: flex-end;
}
</style>