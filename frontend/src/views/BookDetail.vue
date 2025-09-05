<template>
  <div class="app-container">
    <!-- 导航区域 -->
    <div class="navigation-section">
      <el-breadcrumb separator="/" class="breadcrumb">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/dashboard' }">图书管理</el-breadcrumb-item>
        <el-breadcrumb-item>图书详情</el-breadcrumb-item>
      </el-breadcrumb>

      <el-button 
        icon="el-icon-arrow-left" 
        @click="goBack" 
        class="back-btn"
        size="small"
      >
        返回
      </el-button>
    </div>

    <!-- 主要内容 -->
    <el-card v-loading="loading" class="book-card" :body-style="{ padding: '30px' }">
      <!-- 加载状态 -->
      <div v-if="loading" class="skeleton-container">
        <el-skeleton :rows="5" animated />
      </div>

      <!-- 错误状态 -->
      <el-alert
        v-else-if="error"
        :title="`加载失败: ${error}`"
        type="error"
        :closable="false"
        show-icon
        class="error-alert"
      >
        <el-button @click="fetchBookDetail" type="primary" size="small">重试</el-button>
      </el-alert>

      <!-- 图书详情内容 -->
      <div v-else-if="book" class="book-content">
        <!-- 头部信息 -->
        <div class="book-header">
          <h1 class="book-title">{{ book.title }}</h1>
          <div class="book-meta">
            <el-tag type="info" size="small" class="meta-tag">
              <i class="el-icon-user"></i> {{ book.author || '未知作者' }}
            </el-tag>
            <el-tag type="success" size="small" class="meta-tag">
              <i class="el-icon-collection-tag"></i> {{ book.categoryName || '未分类' }}
            </el-tag>
            <el-tag size="small" class="meta-tag">
              <i class="el-icon-time"></i> {{ formatDate(book.createdAt) }}
            </el-tag>
          </div>
        </div>

        <el-divider class="custom-divider"></el-divider>

        <!-- 主要内容 -->
        <div class="book-main">
          <!-- 左侧信息 -->
          <div class="book-info-side">
            <div class="book-cover-container">
              <div class="book-cover">
                <i class="el-icon-notebook-2"></i>
              </div>
            </div>
            
            <div class="stats-container">
              <div class="stat-item">
                <div class="stat-value">{{ book.likes || 0 }}</div>
                <div class="stat-label">点赞数</div>
              </div>
              
              <el-divider direction="vertical" class="stat-divider"></el-divider>
              
              <div class="stat-item">
                <div class="stat-value">{{ formatDate(book.createdAt, 'short') }}</div>
                <div class="stat-label">添加时间</div>
              </div>
            </div>

            <div class="action-buttons">
              <el-button 
                :type="book.isLiked ? 'danger' : 'primary'" 
                :icon="book.isLiked ? 'el-icon-star-on' : 'el-icon-star-off'"
                @click="toggleLike"
                class="action-btn like-btn"
                round
              >
                {{ book.isLiked ? '已点赞' : '点赞' }} ({{ book.likes || 0 }})
              </el-button>
            </div>
          </div>

          <!-- 右侧详情 -->
          <div class="book-details-side">
            <el-card shadow="never" class="info-card">
              <div slot="header" class="info-card-header">
                <span>图书信息</span>
              </div>
              <div class="info-grid">
                <div class="info-item">
                  <span class="info-label">图书ID:</span>
                  <span class="info-value">{{ book.id }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">作者:</span>
                  <span class="info-value">{{ book.author || '未知' }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">分类:</span>
                  <span class="info-value">{{ book.categoryName || '未分类' }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">分类ID:</span>
                  <span class="info-value">{{ book.categoryId || '无' }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">点赞数:</span>
                  <span class="info-value">{{ book.likes || 0 }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">添加时间:</span>
                  <span class="info-value">{{ formatDateTime(book.createdAt) }}</span>
                </div>
              </div>
            </el-card>

            <el-card shadow="never" class="description-card">
              <div slot="header" class="description-header">
                <span>图书描述</span>
              </div>
              <div class="description-content">
                <p>{{ book.description || '暂无描述信息' }}</p>
              </div>
            </el-card>
          </div>
        </div>

        <!-- 相关图书推荐 -->
        <div class="related-books" v-if="relatedBooks.length > 0">
          <el-divider class="custom-divider">
            <i class="el-icon-notebook-1"></i> 相关图书推荐
          </el-divider>
          
          <el-row :gutter="20">
            <el-col 
              v-for="relatedBook in relatedBooks" 
              :key="relatedBook.id" 
              :xs="24" :sm="12" :md="8"
            >
              <el-card 
                shadow="hover" 
                class="related-card"
                @click.native="goToBook(relatedBook.id)"
                :body-style="{ padding: '15px' }"
              >
                <div class="related-content">
                  <div class="related-cover">
                    <i class="el-icon-reading"></i>
                  </div>
                  <div class="related-info">
                    <div class="related-title">{{ relatedBook.title }}</div>
                    <div class="related-author">{{ relatedBook.author || '未知作者' }}</div>
                    <div class="related-likes">
                      <i class="el-icon-star-on"></i> {{ relatedBook.likes || 0 }}
                    </div>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>
      </div>

      <!-- 404状态 -->
      <div v-else class="not-found">
        <el-empty description="图书未找到">
          <span>抱歉，找不到ID为 {{ $route.params.id }} 的图书</span>
          <div style="margin-top: 20px;">
            <el-button @click="goBack" type="primary">返回图书列表</el-button>
          </div>
        </el-empty>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'BookDetail',
  props: {
    id: {
      type: [String, Number],
      required: true
    }
  },
  data() {
    return {
      book: null,
      relatedBooks: [],
      loading: true,
      error: null,
      likedBooks: new Set()
    }
  },
  computed: {
    apiBaseUrl() {
      return 'http://localhost:8080/api/books';
    }
  },
  watch: {
    id: {
      immediate: true,
      handler(newId) {
        if (newId) {
          this.fetchBookDetail();
          this.fetchRelatedBooks();
        }
      }
    }
  },
  mounted() {
    const savedLikes = localStorage.getItem('likedBooks');
    if (savedLikes) {
      this.likedBooks = new Set(JSON.parse(savedLikes));
    }
  },
  methods: {
    async fetchBookDetail() {
      this.loading = true;
      this.error = null;
      
      try {
        const response = await fetch(`${this.apiBaseUrl}/${this.id}`);
        
        if (!response.ok) {
          if (response.status === 404) {
            throw new Error('图书不存在');
          }
          throw new Error(`HTTP错误! 状态码: ${response.status}`);
        }
        
        const bookData = await response.json();
        this.book = {
          ...bookData,
          isLiked: this.likedBooks.has(bookData.id)
        };
        
      } catch (err) {
        this.error = err.message;
        console.error('获取图书详情失败:', err);
      } finally {
        this.loading = false;
      }
    },

    async fetchRelatedBooks() {
      try {
        const response = await fetch(this.apiBaseUrl);
        if (response.ok) {
          const allBooks = await response.json();
          this.relatedBooks = allBooks
            .filter(book => book.id !== parseInt(this.id))
            .slice(0, 3);
        }
      } catch (error) {
        console.error('获取相关图书失败:', error);
      }
    },

    async toggleLike() {
      if (!this.book) return;

      try {
        const bookId = this.book.id;
        const currentLikes = this.book.likes;
        const currentIsLiked = this.book.isLiked;
        
        const newIsLiked = !currentIsLiked;
        const newLikes = newIsLiked ? currentLikes + 1 : currentLikes - 1;
        
        // 乐观更新
        this.book.likes = newLikes;
        this.book.isLiked = newIsLiked;
        
        // 更新本地存储
        if (newIsLiked) {
          this.likedBooks.add(bookId);
        } else {
          this.likedBooks.delete(bookId);
        }
        this.saveLikedBooks();
        
        const apiUrl = newIsLiked ? 
          `${this.apiBaseUrl}/${bookId}/like` : 
          `${this.apiBaseUrl}/${bookId}/unlike`;
        
        const response = await fetch(apiUrl, { method: 'POST' });
        
        if (!response.ok) {
          // 回滚
          this.book.likes = currentLikes;
          this.book.isLiked = currentIsLiked;
          
          if (currentIsLiked) {
            this.likedBooks.add(bookId);
          } else {
            this.likedBooks.delete(bookId);
          }
          this.saveLikedBooks();
          
          throw new Error(newIsLiked ? '点赞失败' : '取消点赞失败');
        }
        
      } catch (error) {
        console.error('点赞操作失败:', error);
        this.$message.error(error.message);
      }
    },

    saveLikedBooks() {
      localStorage.setItem('likedBooks', JSON.stringify(Array.from(this.likedBooks)));
    },

    goToBook(bookId) {
      this.$router.push(`/book/detail/${bookId}`);
    },

    goBack() {
      this.$router.go(-1);
    },

    formatDate(dateString, format = 'long') {
      if (!dateString) return '未知日期';
      const date = new Date(dateString);
      
      if (format === 'short') {
        return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}`;
      }
      
      return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      });
    },

    formatDateTime(dateString) {
      if (!dateString) return '未知时间';
      const date = new Date(dateString);
      return date.toLocaleString('zh-CN');
    }
  }
}
</script>

<style scoped>
.app-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.navigation-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 15px;
}

.breadcrumb {
  flex-grow: 1;
}

.back-btn {
  flex-shrink: 0;
}

.book-card {
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.skeleton-container {
  padding: 30px;
}

.error-alert {
  margin: 20px;
}

.book-header {
  text-align: center;
  margin-bottom: 30px;
}

.book-title {
  font-size: 2.2rem;
  color: #303133;
  margin-bottom: 15px;
  font-weight: 600;
  line-height: 1.3;
}

.book-meta {
  display: flex;
  justify-content: center;
  gap: 10px;
  flex-wrap: wrap;
}

.meta-tag {
  display: flex;
  align-items: center;
  gap: 5px;
}

.custom-divider {
  margin: 30px 0;
}

.book-main {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 30px;
  margin: 30px 0;
}

.book-info-side {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 25px;
  padding: 20px;
  background: #f9fafc;
  border-radius: 10px;
}

.book-cover-container {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
}

.book-cover {
  width: 160px;
  height: 200px;
  background: linear-gradient(135deg, #409EFF 0%, #67C23A 100%);
  border-radius: 8px;
  display: flex;
  justify-content: center;
  align-items: center;
  color: white;
  font-size: 60px;
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.2);
}

.stats-container {
  display: flex;
  justify-content: space-around;
  align-items: center;
  width: 100%;
  padding: 15px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 1.5rem;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 0.9rem;
  color: #909399;
}

.stat-divider {
  height: 40px;
}

.action-buttons {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.action-btn {
  width: 100%;
  padding: 12px;
  font-weight: 500;
}

.like-btn {
  font-weight: bold;
}

.book-details-side {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.info-card, .description-card {
  border-radius: 10px;
  border: 1px solid #e6e8eb;
}

.info-card-header, .description-header {
  font-size: 1.2rem;
  font-weight: 600;
  color: #303133;
  padding: 16px 20px;
  border-bottom: 1px solid #e6e8eb;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  padding: 5px;
}

.info-item {
  display: flex;
  flex-direction: column;
  padding: 12px;
  background: #f9fafc;
  border-radius: 6px;
}

.info-label {
  font-size: 0.9rem;
  color: #606266;
  margin-bottom: 5px;
}

.info-value {
  font-size: 1rem;
  color: #303133;
  font-weight: 500;
}

.description-content {
  padding: 5px;
  line-height: 1.7;
  color: #606266;
}

.description-content p {
  margin: 0;
}

.related-books {
  margin-top: 40px;
}

.related-card {
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 10px;
}

.related-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
}

.related-content {
  display: flex;
  align-items: center;
  gap: 15px;
}

.related-cover {
  width: 50px;
  height: 60px;
  background: #409EFF;
  border-radius: 6px;
  display: flex;
  justify-content: center;
  align-items: center;
  color: white;
  font-size: 24px;
  flex-shrink: 0;
}

.related-info {
  flex: 1;
  overflow: hidden;
}

.related-title {
  font-weight: 600;
  color: #303133;
  margin-bottom: 5px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.related-author {
  color: #606266;
  font-size: 0.9rem;
  margin-bottom: 5px;
}

.related-likes {
  color: #909399;
  font-size: 0.85rem;
  display: flex;
  align-items: center;
  gap: 3px;
}

.not-found {
  text-align: center;
  padding: 60px 0;
}

/* 响应式设计 */
@media (max-width: 992px) {
  .book-main {
    grid-template-columns: 1fr;
    gap: 25px;
  }
  
  .book-info-side {
    order: 2;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .navigation-section {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .book-title {
    font-size: 1.8rem;
  }
  
  .stats-container {
    flex-direction: column;
    gap: 15px;
  }
  
  .stat-divider {
    display: none;
  }
  
  .related-content {
    flex-direction: column;
    text-align: center;
  }
}

@media (max-width: 480px) {
  .app-container {
    padding: 15px;
  }
  
  .book-card {
    border-radius: 8px;
  }
  
  .book-title {
    font-size: 1.5rem;
  }
  
  .book-cover {
    width: 120px;
    height: 160px;
    font-size: 40px;
  }
  
  .action-buttons {
    flex-direction: column;
  }
}
</style>