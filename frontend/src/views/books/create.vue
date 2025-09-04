<template>
  <div class="app-container">
    <div class="create-book-container">
      <!-- 页面标题 -->
      <div class="page-header">
        <div class="page-header-content">
          <i class="el-icon-plus"></i>
          <h1>添加新图书</h1>
          <p>填写图书信息，丰富图书馆藏书</p>
        </div>
      </div>

      <!-- 表单卡片 -->
      <el-card class="form-card" shadow="always">
        <div slot="header" class="card-header">
          <i class="el-icon-document"></i>
          <span>图书信息</span>
        </div>

        <el-form
          ref="bookForm"
          :model="bookForm"
          :rules="rules"
          label-width="120px"
          class="book-form"
          label-position="left"
        >
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="图书标题" prop="title">
                <el-input
                  v-model="bookForm.title"
                  placeholder="请输入图书标题"
                  prefix-icon="el-icon-reading"
                  clearable
                  maxlength="200"
                  show-word-limit
                />
              </el-form-item>
            </el-col>

            <el-col :span="12">
              <el-form-item label="作者" prop="author">
                <el-input
                  v-model="bookForm.author"
                  placeholder="请输入作者姓名"
                  prefix-icon="el-icon-user-solid"
                  clearable
                  maxlength="100"
                  show-word-limit
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="图书分类" prop="categoryId">
                <el-select
                  v-model="bookForm.categoryId"
                  placeholder="请选择图书分类"
                  filterable
                  style="width: 100%"
                  :loading="categoriesLoading"
                >
                  <el-option
                    v-for="category in categories"
                    :key="category.id"
                    :label="category.name"
                    :value="category.id"
                  >
                    <span style="float: left">{{ category.name }}</span>
                    <span style="float: right; color: #8492a6; font-size: 13px">ID: {{ category.id }}</span>
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="图书描述" prop="description">
                <el-input
                  v-model="bookForm.description"
                  type="textarea"
                  placeholder="请输入图书描述，包括内容简介、特色等"
                  :rows="6"
                  maxlength="1000"
                  show-word-limit
                  resize="vertical"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <!-- 操作按钮 -->
          <el-form-item class="form-actions">
            <el-button
              type="primary"
              @click="submitForm"
              :loading="submitLoading"
              icon="el-icon-check"
              size="medium"
            >
              {{ submitLoading ? '创建中...' : '创建图书' }}
            </el-button>
            <el-button
              @click="resetForm"
              icon="el-icon-refresh"
              size="medium"
            >
              重置表单
            </el-button>
            <el-button
              @click="goBack"
              icon="el-icon-back"
              size="medium"
              plain
            >
              返回列表
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 提示信息卡片 -->
      <el-card class="tips-card" shadow="hover">
        <div slot="header" class="card-header">
          <i class="el-icon-warning"></i>
          <span>填写提示</span>
        </div>
        <ul class="tips-list">
          <li><i class="el-icon-circle-check"></i>图书标题不能超过200个字符</li>
          <li><i class="el-icon-circle-check"></i>作者姓名不能超过100个字符</li>
          <li><i class="el-icon-circle-check"></i>请选择合适的图书分类</li>
          <li><i class="el-icon-circle-check"></i>图书描述可以包含内容简介、特色亮点等</li>
        </ul>
      </el-card>
    </div>
  </div>
</template>

<script>
import { createBook, getCategories } from '@/api/book'

export default {
  name: 'CreateBook',
  data() {
    return {
      bookForm: {
        title: '',
        author: '',
        description: '',
        categoryId: null
      },
      categories: [],
      categoriesLoading: false,
      submitLoading: false,
      rules: {
        title: [
          { required: true, message: '请输入图书标题', trigger: 'blur' },
          { max: 200, message: '标题长度不能超过200个字符', trigger: 'blur' }
        ],
        author: [
          { required: true, message: '请输入作者姓名', trigger: 'blur' },
          { max: 100, message: '作者姓名长度不能超过100个字符', trigger: 'blur' }
        ],
        categoryId: [
          { required: true, message: '请选择图书分类', trigger: 'change' }
        ],
        description: [
          { max: 1000, message: '描述长度不能超过1000个字符', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.loadCategories()
  },
  methods: {
    // 加载分类列表
    async loadCategories() {
      try {
        this.categoriesLoading = true
        // 尝试从API获取分类
        const response = await getCategories()
        this.categories = response.data || []

        // 如果API返回的数据为空，使用默认分类
        if (this.categories.length === 0) {
          this.useDefaultCategories()
        }
      } catch (error) {
        console.warn('从API加载分类失败，使用默认分类:', error.message)
        // API调用失败时使用默认分类
        this.useDefaultCategories()
      } finally {
        this.categoriesLoading = false
      }
    },

    // 使用默认分类数据
    useDefaultCategories() {
      this.categories = [
        { id: 1, name: 'Fiction' },
        { id: 2, name: 'Non-fiction' },
        { id: 3, name: 'Science' },
        { id: 4, name: 'History' },
        { id: 5, name: 'Technology' }
      ]
      this.$message.info('已加载默认分类数据')
    },

    // 提交表单
    submitForm() {
      this.$refs['bookForm'].validate(async (valid) => {
        if (valid) {
          try {
            this.submitLoading = true
            await createBook(this.bookForm)
            this.$message.success('图书创建成功！')
            this.$router.push('/books/list')
          } catch (error) {
            this.$message.error('创建失败: ' + (error.message || '服务器错误'))
          } finally {
            this.submitLoading = false
          }
        } else {
          this.$message.warning('请检查表单填写是否正确')
          return false
        }
      })
    },

    // 重置表单
    resetForm() {
      this.$refs['bookForm'].resetFields()
      this.$message.info('表单已重置')
    },

    // 返回上页
    goBack() {
      this.$router.go(-1)
    }
  }
}
</script>

<style scoped>
.create-book-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 24px;
  color: white;
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.3);
}

.page-header-content {
  text-align: center;
}

.page-header i {
  font-size: 48px;
  margin-bottom: 16px;
  opacity: 0.9;
}

.page-header h1 {
  font-size: 32px;
  font-weight: 600;
  margin: 0 0 8px 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.page-header p {
  font-size: 16px;
  margin: 0;
  opacity: 0.9;
}

.form-card {
  margin-bottom: 24px;
  border-radius: 12px;
  overflow: hidden;
}

.form-card >>> .el-card__header {
  background: #f8f9ff;
  border-bottom: 2px solid #e6e8ff;
}

.card-header {
  display: flex;
  align-items: center;
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
}

.card-header i {
  margin-right: 8px;
  font-size: 20px;
  color: #667eea;
}

.book-form {
  padding: 20px;
}

.book-form >>> .el-form-item__label {
  color: #2c3e50;
  font-weight: 500;
}

.book-form >>> .el-input__inner,
.book-form >>> .el-textarea__inner {
  border-radius: 8px;
  border: 2px solid #e6e8ff;
  transition: all 0.3s ease;
}

.book-form >>> .el-input__inner:focus,
.book-form >>> .el-textarea__inner:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
}

.book-form >>> .el-select {
  width: 100%;
}

.form-actions {
  text-align: center;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #e6e8ff;
}

.form-actions >>> .el-button {
  border-radius: 8px;
  padding: 12px 24px;
  font-weight: 500;
  margin: 0 8px;
}

.form-actions >>> .el-button--primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.form-actions >>> .el-button--primary:hover {
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
  transform: translateY(-2px);
}

.tips-card {
  border-radius: 12px;
  border-left: 4px solid #67c23a;
}

.tips-card >>> .el-card__header {
  background: #f0f9ff;
  border-bottom: 1px solid #e1f5fe;
}

.tips-list {
  margin: 0;
  padding: 0;
  list-style: none;
}

.tips-list li {
  display: flex;
  align-items: center;
  padding: 8px 0;
  color: #606266;
  font-size: 14px;
}

.tips-list li i {
  color: #67c23a;
  margin-right: 8px;
  font-size: 16px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .create-book-container {
    padding: 16px;
  }

  .page-header {
    padding: 20px;
  }

  .page-header h1 {
    font-size: 24px;
  }

  .page-header i {
    font-size: 36px;
  }

  .book-form {
    padding: 16px;
  }
}

/* 动画效果 */
.form-card,
.tips-card {
  animation: fadeInUp 0.6s ease-out;
}

.page-header {
  animation: fadeInDown 0.6s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
