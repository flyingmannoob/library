<template>
    <div class="book-list-container">
      <header>
        <h1>我的阅读清单</h1>
        <p class="subtitle">持续阅读，持续成长</p>
      </header>
      
      <div class="content">
        <div class="main-content">
          <h2>图书列表</h2>
          
          <div class="search-box">
            <input 
              v-model="searchQuery" 
              placeholder="搜索图书或作者..." 
              class="search-input"
            >
            <button @click="clearSearch" class="clear-btn">清除</button>
          </div>
          
          <div v-if="loading" class="loading">加载中...</div>
          
          <div v-if="error" class="error">
            <p>加载图书时出错: {{ error }}</p>
            <button @click="fetchBooks" class="retry-btn">重试</button>
          </div>
          
          <div v-else>
            <ul class="book-list">
              <li v-for="book in paginatedBooks" :key="book.id" class="book-item">
                <div class="book-icon">📚</div>
                <div class="book-info">
                  <div class="book-title">{{ book.title }}</div>
                  <div class="book-author">{{ book.author }} · {{ formatDate(book.createdAt) }}</div>
                  <div class="book-category">{{ book.categoryName }}</div>
                </div>
                <div class="book-likes">
                  {{ book.likes }} ❤️
                </div>
              </li>
            </ul>
            
            <!-- 分页控件 -->
            <div class="pagination" v-if="totalPages > 1">
              <button 
                @click="prevPage" 
                :disabled="currentPage === 1"
                class="pagination-btn"
              >
                ← 上一页
              </button>
              
              <span class="page-info">
                第 {{ currentPage }} 页 / 共 {{ totalPages }} 页
              </span>
              
              <button 
                @click="nextPage" 
                :disabled="currentPage === totalPages"
                class="pagination-btn"
              >
                下一页 →
              </button>
            </div>
          </div>
          
          <div v-if="!loading && !error" class="stats">
            <div class="stat-item">
              <div class="stat-value">{{ filteredBooks.length }}</div>
              <div class="stat-label">图书总数</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ latestDate }}</div>
              <div class="stat-label">最新添加</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ authorCount }}</div>
              <div class="stat-label">作者数量</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ totalLikes }}</div>
              <div class="stat-label">总点赞数</div>
            </div>
          </div>
        </div>
        
        <!-- 右侧ECharts图表区域 -->
        <div class="chart-container">
          <div class="chart-header">
            <h3>图书数据可视化</h3>
            <div class="chart-tabs">
              <button 
                :class="['chart-tab', activeChart === 'category' ? 'active' : '']"
                @click="activeChart = 'category'"
              >
                分类分布
              </button>
              <button 
                :class="['chart-tab', activeChart === 'likes' ? 'active' : '']"
                @click="activeChart = 'likes'"
              >
                点赞分布
              </button>
              <button 
                :class="['chart-tab', activeChart === 'date' ? 'active' : '']"
                @click="activeChart = 'date'"
              >
                添加趋势
              </button>
            </div>
          </div>
          <div id="bookChart" class="chart"></div>
        </div>
      </div>
      
      <footer>
        <p>© 2023 我的图书管理系统 | 共 {{ books.length }} 本书籍</p>
      </footer>
    </div>
  </template>
  
  <script>
  import * as echarts from 'echarts';
  
  export default {
    name: 'BookList',
    data() {
      return {
        books: [],
        loading: true,
        error: null,
        searchQuery: '',
        currentPage: 1,
        pageSize: 5, // 修改：每页显示5本书
        activeChart: 'category', // 默认显示分类图表
        chart: null // ECharts实例
      };
    },
    computed: {
      filteredBooks() {
        if (!this.searchQuery) {
          return this.books;
        }
        const query = this.searchQuery.toLowerCase();
        return this.books.filter(book => 
          book.title.toLowerCase().includes(query) ||
          book.author.toLowerCase().includes(query) ||
          (book.categoryName && book.categoryName.toLowerCase().includes(query))
        );
      },
      totalPages() {
        return Math.ceil(this.filteredBooks.length / this.pageSize);
      },
      paginatedBooks() {
        const start = (this.currentPage - 1) * this.pageSize;
        const end = start + this.pageSize;
        return this.filteredBooks.slice(start, end);
      },
      latestDate() {
        if (this.books.length === 0) return '无';
        const dates = this.books.map(book => new Date(book.createdAt));
        const latest = new Date(Math.max.apply(null, dates));
        // 修改：将年月显示在一行
        return `${latest.getFullYear()}-${(latest.getMonth() + 1).toString().padStart(2, '0')}`;
      },
      authorCount() {
        return new Set(this.books.map(book => book.author)).size;
      },
      totalLikes() {
        return this.books.reduce((sum, book) => sum + (book.likes || 0), 0);
      },
      // 按分类统计
      categoryData() {
        const categoryMap = {};
        this.books.forEach(book => {
          const category = book.categoryName || '未分类';
          if (!categoryMap[category]) {
            categoryMap[category] = 0;
          }
          categoryMap[category]++;
        });
        
        return Object.keys(categoryMap).map(category => ({
          name: category,
          value: categoryMap[category]
        }));
      },
      // 按点赞数统计
      likesData() {
        const likesMap = {
          '0-10': 0,
          '11-50': 0,
          '51-100': 0,
          '100+': 0
        };
        
        this.books.forEach(book => {
          const likes = book.likes || 0;
          if (likes <= 10) {
            likesMap['0-10']++;
          } else if (likes <= 50) {
            likesMap['11-50']++;
          } else if (likes <= 100) {
            likesMap['51-100']++;
          } else {
            likesMap['100+']++;
          }
        });
        
        return Object.keys(likesMap).map(likes => ({
          name: likes,
          value: likesMap[likes]
        }));
      },
      // 按添加日期统计
      dateData() {
        const dateMap = {};
        this.books.forEach(book => {
          if (book.createdAt) {
            const date = new Date(book.createdAt);
            const yearMonth = `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}`;
            
            if (!dateMap[yearMonth]) {
              dateMap[yearMonth] = 0;
            }
            dateMap[yearMonth]++;
          }
        });
        
        // 转换为数组并按日期排序
        return Object.keys(dateMap)
          .sort()
          .map(date => ({
            date: date,
            count: dateMap[date]
          }));
      }
    },
    watch: {
      searchQuery() {
        this.currentPage = 1; // 搜索时回到第一页
      },
      // 监听图表类型变化
      activeChart() {
        this.renderChart();
      },
      // 监听书籍数据变化
      books() {
        this.renderChart();
      }
    },
    methods: {
      async fetchBooks() {
        this.loading = true;
        this.error = null;
        this.currentPage = 1; // 重置到第一页
        
        try {
          const response = await fetch('http://localhost:8081/api/books');
          if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
          }
          this.books = await response.json();
        } catch (err) {
          this.error = err.message || '加载图书失败';
          console.error(err);
        } finally {
          this.loading = false;
        }
      },
      formatDate(dateString) {
        if (!dateString) return '未知日期';
        const date = new Date(dateString);
        return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}`;
      },
      clearSearch() {
        this.searchQuery = '';
        this.currentPage = 1; // 搜索清除后回到第一页
      },
      nextPage() {
        if (this.currentPage < this.totalPages) {
          this.currentPage++;
        }
      },
      prevPage() {
        if (this.currentPage > 1) {
          this.currentPage--;
        }
      },
      // 初始化图表
      initChart() {
        const chartDom = document.getElementById('bookChart');
        if (!chartDom) return;
        
        this.chart = echarts.init(chartDom);
        this.renderChart();
        
        // 监听窗口变化，调整图表大小
        window.addEventListener('resize', this.resizeChart);
      },
      // 调整图表大小
      resizeChart() {
        if (this.chart) {
          this.chart.resize();
        }
      },
      // 渲染图表
      renderChart() {
        if (!this.chart || this.books.length === 0) return;
        
        let option = {};
        
        switch (this.activeChart) {
          case 'category':
            option = {
              title: {
                text: '图书分类分布',
                left: 'center'
              },
              tooltip: {
                trigger: 'item',
                formatter: '{a} <br/>{b}: {c} ({d}%)'
              },
              legend: {
                orient: 'vertical',
                left: 'left',
                data: this.categoryData.map(item => item.name)
              },
              series: [
                {
                  name: '图书数量',
                  type: 'pie',
                  radius: '70%',
                  data: this.categoryData,
                  emphasis: {
                    itemStyle: {
                      shadowBlur: 10,
                      shadowOffsetX: 0,
                      shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                  }
                }
              ]
            };
            break;
            
          case 'likes':
            option = {
              title: {
                text: '图书点赞分布',
                left: 'center'
              },
              tooltip: {
                trigger: 'axis',
                axisPointer: {
                  type: 'shadow'
                }
              },
              xAxis: {
                type: 'category',
                data: this.likesData.map(item => item.name)
              },
              yAxis: {
                type: 'value',
                name: '图书数量'
              },
              series: [
                {
                  name: '图书数量',
                  type: 'bar',
                  data: this.likesData.map(item => item.value),
                  itemStyle: {
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                      { offset: 0, color: '#83bff6' },
                      { offset: 0.5, color: '#188df0' },
                      { offset: 1, color: '#188df0' }
                    ])
                  }
                }
              ]
            };
            break;
            
          case 'date':
            option = {
              title: {
                text: '图书添加趋势',
                left: 'center'
              },
              tooltip: {
                trigger: 'axis'
              },
              xAxis: {
                type: 'category',
                data: this.dateData.map(item => item.date),
                name: '日期'
              },
              yAxis: {
                type: 'value',
                name: '图书数量'
              },
              series: [
                {
                  name: '添加数量',
                  type: 'line',
                  data: this.dateData.map(item => item.count),
                  smooth: true,
                  symbol: 'circle',
                  symbolSize: 8,
                  itemStyle: {
                    color: '#5470c6'
                  },
                  lineStyle: {
                    width: 3
                  }
                }
              ]
            };
            break;
        }
        
        this.chart.setOption(option);
      }
    },
    mounted() {
      this.fetchBooks();
      this.$nextTick(() => {
        this.initChart();
      });
    },
    beforeDestroy() {
      if (this.chart) {
        this.chart.dispose();
        this.chart = null;
      }
      window.removeEventListener('resize', this.resizeChart);
    }
  };
  </script>
  
  <style scoped>
  .book-list-container {
    max-width: 1200px;
    margin: 0 auto;
    background: white;
    border-radius: 15px;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
    overflow: hidden;
  }
  
  header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    padding: 30px 20px;
    text-align: center;
  }
  
  h1 {
    font-size: 2.5rem;
    margin-bottom: 10px;
    font-weight: 600;
  }
  
  .subtitle {
    font-weight: 300;
    opacity: 0.9;
    font-size: 1.1rem;
  }
  
  .content {
    display: flex;
    padding: 30px;
    gap: 30px;
  }
  
  .main-content {
    flex: 1;
    min-width: 0;
  }
  
  .chart-container {
    width: 400px;
    background: #f8f9fa;
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  }
  
  .chart-header {
    margin-bottom: 20px;
  }
  
  .chart-header h3 {
    margin-bottom: 15px;
    color: #2c3e50;
    text-align: center;
  }
  
  .chart-tabs {
    display: flex;
    justify-content: center;
    gap: 10px;
    margin-bottom: 15px;
  }
  
  .chart-tab {
    padding: 8px 15px;
    background: #e9ecef;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    font-size: 14px;
    transition: all 0.3s;
  }
  
  .chart-tab:hover {
    background: #dee2e6;
  }
  
  .chart-tab.active {
    background: #667eea;
    color: white;
  }
  
  .chart {
    height: 350px;
    width: 100%;
  }
  
  .search-box {
    display: flex;
    gap: 10px;
    margin: 20px 0;
  }
  
  .search-input {
    flex: 1;
    padding: 12px 16px;
    border: 2px solid #e1e5e9;
    border-radius: 8px;
    font-size: 16px;
    outline: none;
    transition: border-color 0.3s;
  }
  
  .search-input:focus {
    border-color: #667eea;
  }
  
  .clear-btn {
    padding: 12px 20px;
    background: #6c757d;
    color: white;
    border: none;
    border-radius: 8px;
    cursor: pointer;
    font-weight: 500;
  }
  
  .clear-btn:hover {
    background: #5a6268;
  }
  
  .book-list {
    list-style: none;
    margin-top: 20px;
  }
  
  .book-item {
    background: #f8f9fa;
    margin-bottom: 15px;
    padding: 20px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    transition: all 0.3s ease;
    border-left: 4px solid #667eea;
  }
  
  .book-item:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
    background: #ffffff;
  }
  
  .book-icon {
    margin-right: 20px;
    font-size: 2rem;
  }
  
  .book-info {
    flex: 1;
  }
  
  .book-title {
    font-weight: 600;
    font-size: 1.2rem;
    color: #2c3e50;
    margin-bottom: 5px;
  }
  
  .book-author {
    color: #6c757d;
    font-size: 0.95rem;
    margin-bottom: 3px;
  }
  
  .book-category {
    color: #667eea;
    font-size: 0.85rem;
    font-weight: 500;
    background: rgba(102, 126, 234, 0.1);
    padding: 4px 8px;
    border-radius: 4px;
    display: inline-block;
  }
  
  .book-likes {
    font-size: 1.1rem;
    font-weight: 600;
    color: #e74c3c;
    background: rgba(231, 76, 60, 0.1);
    padding: 8px 12px;
    border-radius: 20px;
  }
  
  .stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr); /* 确保总是4列 */
  gap: 15px;
  margin-top: 40px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  padding: 25px;
  border-radius: 12px;
  align-items: center; /* 垂直居中 */
}

.stat-item {
  text-align: center;
  padding: 10px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: 100px; /* 确保所有项目高度一致 */
}

.stat-value {
  font-size: 2.2rem;
  font-weight: 700;
  color: #667eea;
  margin-bottom: 5px;
  line-height: 1.2;
}

.stat-label {
  color: #6c757d;
  font-size: 0.9rem;
  font-weight: 500;
  line-height: 1.2;
}

/* 响应式调整 */
@media (max-width: 992px) {
  .stats {
    grid-template-columns: repeat(2, 1fr); /* 中等屏幕改为2列 */
  }
}

@media (max-width: 576px) {
  .stats {
    grid-template-columns: 1fr; /* 小屏幕改为1列 */
  }
  
  .stat-item {
    min-height: auto;
    padding: 10px;
  }
  
  .stat-value {
    font-size: 1.8rem;
  }
}
  
  .loading {
    text-align: center;
    padding: 40px;
    color: #6c757d;
    font-size: 1.1rem;
  }
  
  .error {
    text-align: center;
    padding: 30px;
    color: #dc3545;
    background: #f8d7da;
    border-radius: 12px;
    margin: 20px 0;
  }
  
  .retry-btn {
    margin-top: 15px;
    padding: 10px 20px;
    background: #dc3545;
    color: white;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    font-weight: 500;
  }
  
  .retry-btn:hover {
    background: #c82333;
  }
  
  footer {
    text-align: center;
    padding: 25px;
    color: #6c757d;
    font-size: 0.9rem;
    border-top: 1px solid #e9ecef;
    background: #f8f9fa;
  }
  
  /* 分页样式 */
  .pagination {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 20px;
    margin-top: 40px;
    padding: 25px;
    background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
    border-radius: 15px;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  }
  
  .pagination-btn {
    padding: 12px 25px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    border: none;
    border-radius: 8px;
    cursor: pointer;
    font-weight: 600;
    font-size: 14px;
    transition: all 0.3s ease;
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  }
  
  .pagination-btn:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
  }
  
  .pagination-btn:active:not(:disabled) {
    transform: translateY(0);
  }
  
  .pagination-btn:disabled {
    background: linear-gradient(135deg, #cbd5e0 0%, #a0aec0 100%);
    cursor: not-allowed;
    box-shadow: none;
    transform: none;
  }
  
  .page-info {
    color: #4a5568;
    font-weight: 600;
    font-size: 16px;
    padding: 10px 20px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    min-width: 150px;
    text-align: center;
  }
  
  @media (max-width: 992px) {
    .content {
      flex-direction: column;
    }
    
    .chart-container {
      width: 100%;
    }
  }
  
  @media (max-width: 768px) {
    .book-item {
      flex-direction: column;
      align-items: flex-start;
      gap: 15px;
    }
    
    .book-icon {
      margin-right: 0;
      margin-bottom: 10px;
    }
    
    .book-likes {
      align-self: flex-end;
    }
    
    .stats {
      grid-template-columns: 1fr 1fr;
    }
    
    .search-box {
      flex-direction: column;
    }
    
    .pagination {
      flex-direction: column;
      gap: 15px;
    }
  }
  
  @media (max-width: 480px) {
    .stats {
      grid-template-columns: 1fr;
    }
    
    header {
      padding: 20px 15px;
    }
    
    h1 {
      font-size: 2rem;
    }
    
    .content {
      padding: 20px;
    }
  }
  </style>