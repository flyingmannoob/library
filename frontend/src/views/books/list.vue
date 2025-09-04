<template>
  <div class="books-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">
            <i class="el-icon-reading"></i>
            图书管理
          </h1>
          <p class="page-subtitle">管理图书馆藏书信息和分类</p>
        </div>
        <div class="action-section">
          <el-button
            type="primary"
            size="medium"
            icon="el-icon-plus"
            class="add-button"
            @click="handleCreate"
          >
            添加图书
          </el-button>
        </div>
      </div>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <div class="search-container">
        <div class="search-card">
          <div class="search-header">
            <h3 class="search-title">
              <i class="el-icon-search"></i>
              搜索筛选
            </h3>
          </div>
          <div class="search-form">
            <div class="search-row">
              <div class="search-item">
                <label class="search-label">图书标题</label>
                <el-input
                  v-model="listQuery.title"
                  placeholder="请输入图书标题"
                  prefix-icon="el-icon-document"
                  clearable
                  class="search-input"
                  @keyup.enter.native="handleFilter"
                  @clear="handleFilter"
                />
              </div>
              <div class="search-item">
                <label class="search-label">作者姓名</label>
                <el-input
                  v-model="listQuery.author"
                  placeholder="请输入作者姓名"
                  prefix-icon="el-icon-user"
                  clearable
                  class="search-input"
                  @keyup.enter.native="handleFilter"
                  @clear="handleFilter"
                />
              </div>
              <div class="search-item">
                <div class="search-buttons">
                  <el-button
                    v-waves
                    type="primary"
                    icon="el-icon-search"
                    class="search-btn"
                    @click="handleFilter"
                  >
                    搜索
                  </el-button>
                  <el-button
                    icon="el-icon-refresh"
                    class="reset-btn"
                    @click="resetSearch"
                  >
                    重置
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-container">
      <div class="stats-card">
        <div class="stats-icon books-icon">
          <i class="el-icon-reading"></i>
        </div>
        <div class="stats-content">
          <div class="stats-number">{{ totalBooks }}</div>
          <div class="stats-label">总图书数</div>
        </div>
      </div>
      <div class="stats-card">
        <div class="stats-icon categories-icon">
          <i class="el-icon-collection-tag"></i>
        </div>
        <div class="stats-content">
          <div class="stats-number">{{ totalCategories }}</div>
          <div class="stats-label">图书分类</div>
        </div>
      </div>
      <div class="stats-card">
        <div class="stats-icon authors-icon">
          <i class="el-icon-user"></i>
        </div>
        <div class="stats-content">
          <div class="stats-number">{{ totalAuthors }}</div>
          <div class="stats-label">作者数量</div>
        </div>
      </div>
    </div>

    <!-- 图书表格卡片 -->
    <div class="table-card">
      <div class="card-header">
        <div class="header-left">
          <h3 class="card-title">
            <i class="el-icon-menu"></i>
            图书列表
          </h3>
          <div class="total-info">
            共 {{ total }} 本图书
          </div>
        </div>
      </div>

      <div class="table-container">
        <el-table
          :key="tableKey"
          v-loading="listLoading"
          :data="list"
          fit
          highlight-current-row
          class="modern-table"
          empty-text="暂无图书数据"
          element-loading-text="加载中..."
          element-loading-spinner="el-icon-loading"
        >
          <el-table-column
            label="ID"
            prop="id"
            sortable
            align="center"
            width="80"
            class-name="id-column"
          >
            <template slot-scope="scope">
              <span class="id-badge">#{{ scope.row.id }}</span>
            </template>
          </el-table-column>

          <el-table-column
            label="图书信息"
            min-width="250"
          >
            <template slot-scope="scope">
              <div class="book-info">
                <div class="book-cover">
                  <i class="el-icon-reading book-icon"></i>
                </div>
                <div class="book-details">
                  <div class="book-title" @click="handleUpdate(scope.row)">
                    {{ scope.row.title }}
                  </div>
                  <div class="book-author">
                    <i class="el-icon-user-solid"></i>
                    {{ scope.row.author }}
                  </div>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column
            label="分类"
            width="120"
            align="center"
          >
            <template slot-scope="scope">
              <el-tag
                :type="getCategoryType(scope.row.categoryId)"
                effect="plain"
                class="category-tag"
              >
                {{ getCategoryName(scope.row.categoryId) }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column
            label="图书描述"
            min-width="200"
            show-overflow-tooltip
          >
            <template slot-scope="scope">
              <div class="description-content">
                <span v-if="scope.row.description" class="description-text">
                  {{ scope.row.description }}
                </span>
                <span v-else class="no-description">
                  <i class="el-icon-info"></i>
                  暂无描述
                </span>
              </div>
            </template>
          </el-table-column>

          <el-table-column
            label="操作"
            align="center"
            width="180"
            class-name="action-column"
          >
            <template slot-scope="scope">
              <div class="action-buttons">
                <el-button
                  type="primary"
                  size="small"
                  icon="el-icon-edit"
                  circle
                  class="action-btn edit-btn"
                  @click="handleUpdate(scope.row)"
                  v-el-tooltip.top="'编辑图书'"
                />
                <el-button
                  type="danger"
                  size="small"
                  icon="el-icon-delete"
                  circle
                  class="action-btn delete-btn"
                  @click="handleDelete(scope.row)"
                  v-el-tooltip.top="'删除图书'"
                />
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页 -->
      <div class="pagination-container">
        <pagination
          v-show="total > 0"
          :total="total"
          :page.sync="listQuery.page"
          :limit.sync="listQuery.limit"
          @pagination="getList"
          class="modern-pagination"
        />
      </div>
    </div>

    <!-- 图书编辑对话框 -->
    <el-dialog
      :title="textMap[dialogStatus]"
      :visible.sync="dialogFormVisible"
      width="600px"
      class="modern-dialog"
      :close-on-click-modal="false"
    >
      <div class="dialog-content">
        <el-form
          ref="dataForm"
          :rules="rules"
          :model="temp"
          label-position="top"
          class="modern-form"
        >
          <div class="form-grid">
            <div class="form-item">
              <el-form-item
                label="图书标题"
                prop="title"
              >
                <el-input
                  v-model="temp.title"
                  placeholder="请输入图书标题"
                  prefix-icon="el-icon-document"
                  class="form-input"
                />
              </el-form-item>
            </div>

            <div class="form-item">
              <el-form-item
                label="作者姓名"
                prop="author"
              >
                <el-input
                  v-model="temp.author"
                  placeholder="请输入作者姓名"
                  prefix-icon="el-icon-user"
                  class="form-input"
                />
              </el-form-item>
            </div>
          </div>

          <div class="form-item">
            <el-form-item
              label="图书分类"
              prop="categoryId"
            >
              <el-select
                v-model="temp.categoryId"
                placeholder="请选择图书分类"
                style="width: 100%;"
                class="form-input"
              >
                <el-option
                  v-for="category in categoryOptions"
                  :key="category.value"
                  :label="category.label"
                  :value="category.value"
                />
              </el-select>
            </el-form-item>
          </div>

          <div class="form-item">
            <el-form-item label="图书描述">
              <el-input
                v-model="temp.description"
                type="textarea"
                placeholder="请输入图书描述（可选）"
                :autosize="{ minRows: 3, maxRows: 6 }"
                class="form-textarea"
              />
            </el-form-item>
          </div>
        </el-form>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button
          @click="dialogFormVisible = false"
          class="cancel-btn"
        >
          取消
        </el-button>
        <el-button
          type="primary"
          @click="dialogStatus === 'create' ? createData() : updateData()"
          class="confirm-btn"
        >
          <i class="el-icon-check"></i>
          {{ dialogStatus === 'create' ? '添加图书' : '保存修改' }}
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getBooks, createBook, updateBook, deleteBook } from '@/api/book'
import waves from '@/directive/waves'
import Pagination from '@/components/Pagination'

export default {
  name: 'BookList',
  components: { Pagination },
  directives: { waves },
  data() {
    return {
      tableKey: 0,
      list: null,
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 10,
        title: undefined,
        author: undefined
      },
      temp: {
        id: undefined,
        title: '',
        author: '',
        categoryId: undefined,
        description: ''
      },
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑图书',
        create: '添加图书'
      },
      rules: {
        title: [{ required: true, message: '标题是必填项', trigger: 'change' }],
        author: [{ required: true, message: '作者是必填项', trigger: 'change' }]
      }
    }
  },
  created() {
    this.getList()
  },
  computed: {
    // 统计数据计算
    totalBooks() {
      return this.total || 0
    },
    totalCategories() {
      if (!this.list) return 0
      const categories = new Set(this.list.map(book => book.categoryId))
      return categories.size
    },
    totalAuthors() {
      if (!this.list) return 0
      const authors = new Set(this.list.map(book => book.author))
      return authors.size
    },
    categoryOptions() {
      return [
        { value: 1, label: '文学' },
        { value: 2, label: '科技' },
        { value: 3, label: '历史' },
        { value: 4, label: '艺术' },
        { value: 5, label: '教育' },
        { value: 6, label: '其他' }
      ]
    }
  },
  methods: {
    getList() {
      this.listLoading = true
      getBooks(this.listQuery).then(response => {
        this.list = response.data.items
        this.total = response.data.total
        this.listLoading = false
      })
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    resetTemp() {
      this.temp = {
        id: undefined,
        title: '',
        author: '',
        categoryId: undefined,
        description: ''
      }
    },
    handleCreate() {
      this.resetTemp()
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    createData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          createBook(this.temp).then(() => {
            this.list.unshift(this.temp)
            this.dialogFormVisible = false
            this.$notify({
              title: '成功',
              message: '图书创建成功',
              type: 'success',
              duration: 2000
            })
            this.getList()
          })
        }
      })
    },
    handleUpdate(row) {
      this.temp = Object.assign({}, row)
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    updateData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          const tempData = Object.assign({}, this.temp)
          updateBook(tempData.id, tempData).then(() => {
            const index = this.list.findIndex(v => v.id === this.temp.id)
            this.list.splice(index, 1, this.temp)
            this.dialogFormVisible = false
            this.$notify({
              title: '成功',
              message: '图书更新成功',
              type: 'success',
              duration: 2000
            })
          })
        }
      })
    },
    handleDelete(row) {
      this.$confirm('确认删除该图书?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteBook(row.id).then(() => {
          this.$notify({
            title: '成功',
            message: '删除成功',
            type: 'success',
            duration: 2000
          })
          const index = this.list.indexOf(row)
          this.list.splice(index, 1)
        })
      })
    },
    // 重置搜索
    resetSearch() {
      this.listQuery = {
        page: 1,
        limit: 10,
        title: undefined,
        author: undefined
      }
      this.getList()
    },
    // 获取分类名称
    getCategoryName(categoryId) {
      const category = this.categoryOptions.find(cat => cat.value === categoryId)
      return category ? category.label : '未分类'
    },
    // 获取分类标签类型
    getCategoryType(categoryId) {
      const types = {
        1: 'primary',    // 文学
        2: 'success',    // 科技
        3: 'info',       // 历史
        4: 'warning',    // 艺术
        5: 'danger',     // 教育
        6: 'default'     // 其他
      }
      return types[categoryId] || 'default'
    }
  }
}
</script>

<style lang="scss" scoped>
.books-container {
  min-height: calc(100vh - 84px);
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 0;
  margin: 0;
}

// 页面头部样式
.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 30px 24px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);

  .header-content {
    max-width: 1200px;
    margin: 0 auto;
    display: flex;
    justify-content: space-between;
    align-items: center;
    color: white;

    .title-section {
      .page-title {
        font-size: 32px;
        font-weight: 700;
        margin: 0 0 8px 0;
        display: flex;
        align-items: center;
        gap: 12px;

        i {
          font-size: 36px;
          opacity: 0.9;
        }
      }

      .page-subtitle {
        font-size: 16px;
        opacity: 0.85;
        margin: 0;
        font-weight: 300;
      }
    }

    .action-section {
      .add-button {
        padding: 12px 24px;
        font-size: 16px;
        font-weight: 600;
        border-radius: 8px;
        background: rgba(255, 255, 255, 0.15);
        border: 2px solid rgba(255, 255, 255, 0.3);
        color: white;
        transition: all 0.3s ease;

        &:hover {
          background: rgba(255, 255, 255, 0.25);
          border-color: rgba(255, 255, 255, 0.5);
          transform: translateY(-2px);
          box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
        }
      }
    }
  }
}

// 搜索区域样式
.search-section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px 24px;

  .search-card {
    background: white;
    border-radius: 16px;
    padding: 24px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    border: 1px solid rgba(0, 0, 0, 0.06);

    .search-header {
      margin-bottom: 20px;

      .search-title {
        font-size: 18px;
        font-weight: 600;
        color: #2c3e50;
        margin: 0;
        display: flex;
        align-items: center;
        gap: 8px;

        i {
          color: #667eea;
        }
      }
    }

    .search-form {
      .search-row {
        display: grid;
        grid-template-columns: 1fr 1fr auto;
        gap: 20px;
        align-items: end;

        .search-item {
          .search-label {
            display: block;
            margin-bottom: 8px;
            font-weight: 500;
            color: #2c3e50;
            font-size: 14px;
          }

          .search-input {
            ::v-deep .el-input__inner {
              border-radius: 8px;
              border: 2px solid #e9ecef;
              padding: 12px 16px 12px 40px;
              transition: all 0.3s ease;

              &:focus {
                border-color: #667eea;
                box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
              }
            }

            ::v-deep .el-input__prefix {
              left: 12px;
              color: #6c757d;
            }
          }
        }

        .search-buttons {
          display: flex;
          gap: 12px;

          .search-btn, .reset-btn {
            padding: 12px 20px;
            border-radius: 8px;
            font-weight: 500;
            transition: all 0.3s ease;

            &:hover {
              transform: translateY(-2px);
            }
          }

          .search-btn {
            background: linear-gradient(135deg, #667eea, #764ba2);
            border: none;

            &:hover {
              box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
            }
          }

          .reset-btn {
            background: #f8f9fa;
            border: 2px solid #e9ecef;
            color: #6c757d;

            &:hover {
              border-color: #ced4da;
              background: #e9ecef;
            }
          }
        }
      }
    }
  }
}

// 统计卡片样式
.stats-container {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  padding: 0 24px 24px;

  .stats-card {
    background: white;
    border-radius: 12px;
    padding: 24px;
    display: flex;
    align-items: center;
    gap: 20px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    transition: all 0.3s ease;
    border: 1px solid rgba(0, 0, 0, 0.06);

    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
    }

    .stats-icon {
      width: 60px;
      height: 60px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 24px;
      color: white;

      &.books-icon {
        background: linear-gradient(135deg, #667eea, #764ba2);
      }

      &.categories-icon {
        background: linear-gradient(135deg, #4ecdc4, #26d0ce);
      }

      &.authors-icon {
        background: linear-gradient(135deg, #ff9a9e, #fecfef);
        color: #667eea;
      }
    }

    .stats-content {
      .stats-number {
        font-size: 32px;
        font-weight: 700;
        color: #2c3e50;
        line-height: 1;
        margin-bottom: 4px;
      }

      .stats-label {
        font-size: 14px;
        color: #7f8c8d;
        font-weight: 500;
      }
    }
  }
}

// 表格卡片样式
.table-card {
  max-width: 1200px;
  margin: 0 auto;
  background: white;
  border-radius: 16px;
  box-shadow: 0 6px 30px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  margin-bottom: 24px;

  .card-header {
    background: linear-gradient(135deg, #f8f9fa, #e9ecef);
    padding: 20px 24px;
    border-bottom: 1px solid #e9ecef;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .header-left {
      display: flex;
      align-items: center;
      gap: 16px;

      .card-title {
        margin: 0;
        font-size: 20px;
        font-weight: 600;
        color: #2c3e50;
        display: flex;
        align-items: center;
        gap: 10px;

        i {
          font-size: 22px;
          color: #667eea;
        }
      }

      .total-info {
        background: #667eea;
        color: white;
        padding: 4px 12px;
        border-radius: 12px;
        font-size: 12px;
        font-weight: 500;
      }
    }
  }

  .table-container {
    padding: 0;
    overflow: hidden;

    .modern-table {
      border: none;

      ::v-deep .el-table__header-wrapper {
        .el-table__header {
          th {
            background: #fafbfc;
            color: #606266;
            font-weight: 600;
            border-bottom: 2px solid #e9ecef;
            padding: 16px 0;

            .cell {
              padding: 0 16px;
            }
          }
        }
      }

      ::v-deep .el-table__body-wrapper {
        .el-table__body {
          tr {
            transition: all 0.3s ease;

            &:hover {
              background-color: #f8f9ff !important;
            }

            td {
              border-bottom: 1px solid #f1f2f6;
              padding: 16px 0;

              .cell {
                padding: 0 16px;
              }
            }
          }
        }
      }

      .id-badge {
        display: inline-block;
        background: linear-gradient(135deg, #667eea, #764ba2);
        color: white;
        padding: 4px 12px;
        border-radius: 12px;
        font-size: 12px;
        font-weight: 600;
      }

      .book-info {
        display: flex;
        align-items: center;
        gap: 16px;

        .book-cover {
          width: 50px;
          height: 50px;
          background: linear-gradient(135deg, #667eea, #764ba2);
          border-radius: 8px;
          display: flex;
          align-items: center;
          justify-content: center;
          color: white;
          font-size: 20px;
        }

        .book-details {
          .book-title {
            font-size: 16px;
            font-weight: 600;
            color: #2c3e50;
            cursor: pointer;
            margin-bottom: 4px;
            transition: color 0.3s ease;

            &:hover {
              color: #667eea;
            }
          }

          .book-author {
            font-size: 13px;
            color: #7f8c8d;
            display: flex;
            align-items: center;
            gap: 4px;

            i {
              font-size: 12px;
            }
          }
        }
      }

      .category-tag {
        font-weight: 500;
        padding: 6px 12px;
        border-radius: 8px;
      }

      .description-content {
        .description-text {
          color: #606266;
          font-size: 14px;
          line-height: 1.5;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
          text-overflow: ellipsis;
        }

        .no-description {
          color: #c0c4cc;
          font-size: 13px;
          font-style: italic;
          display: flex;
          align-items: center;
          gap: 4px;

          i {
            font-size: 14px;
          }
        }
      }

      .time-info {
        display: flex;
        align-items: center;
        gap: 6px;
        color: #7f8c8d;
        font-size: 13px;

        i {
          color: #667eea;
        }
      }

      .action-buttons {
        display: flex;
        justify-content: center;
        align-items: center;
        gap: 8px;

        .action-btn {
          transition: all 0.3s ease;

          &:hover {
            transform: translateY(-2px);
          }

          &.edit-btn:hover {
            box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
          }

          &.delete-btn:hover {
            box-shadow: 0 4px 12px rgba(245, 108, 108, 0.4);
          }
        }
      }
    }
  }

  .pagination-container {
    padding: 20px 24px;
    border-top: 1px solid #f1f2f6;
    display: flex;
    justify-content: flex-end;

    .modern-pagination {
      ::v-deep .el-pagination {
        .el-pager li {
          border-radius: 6px;
          margin: 0 2px;

          &.active {
            background: #667eea;
            border-color: #667eea;
          }
        }

        .btn-prev, .btn-next {
          border-radius: 6px;
        }
      }
    }
  }
}

// 对话框样式
::v-deep .modern-dialog {
  .el-dialog {
    border-radius: 16px;
    overflow: hidden;

    .el-dialog__header {
      background: linear-gradient(135deg, #667eea, #764ba2);
      color: white;
      padding: 20px 24px;

      .el-dialog__title {
        color: white;
        font-size: 18px;
        font-weight: 600;
      }

      .el-dialog__close {
        color: white;
        font-size: 20px;

        &:hover {
          background: rgba(255, 255, 255, 0.1);
          border-radius: 50%;
        }
      }
    }

    .el-dialog__body {
      padding: 0;
    }
  }
}

.dialog-content {
  padding: 24px;

  .modern-form {
    .form-grid {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 20px;
      margin-bottom: 20px;
    }

    .form-item {
      ::v-deep .el-form-item__label {
        font-weight: 600;
        color: #2c3e50;
        margin-bottom: 8px;
      }

      .form-input {
        ::v-deep .el-input__inner {
          border-radius: 8px;
          border: 2px solid #e9ecef;
          padding: 12px 16px 12px 40px;
          transition: all 0.3s ease;

          &:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
          }
        }

        ::v-deep .el-input__prefix {
          left: 12px;
          color: #6c757d;
        }
      }

      .form-textarea {
        ::v-deep .el-textarea__inner {
          border-radius: 8px;
          border: 2px solid #e9ecef;
          padding: 12px 16px;
          transition: all 0.3s ease;

          &:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
          }
        }
      }

      ::v-deep .el-select {
        width: 100%;

        .el-input__inner {
          border-radius: 8px;
          border: 2px solid #e9ecef;
          padding: 12px 16px;
          transition: all 0.3s ease;

          &:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
          }
        }
      }
    }
  }
}

.dialog-footer {
  padding: 16px 24px 24px;
  display: flex;
  justify-content: flex-end;
  gap: 12px;

  .cancel-btn {
    padding: 10px 20px;
    border-radius: 8px;
    border: 2px solid #e9ecef;
    background: white;
    color: #6c757d;
    transition: all 0.3s ease;

    &:hover {
      border-color: #ced4da;
      background: #f8f9fa;
    }
  }

  .confirm-btn {
    padding: 10px 20px;
    border-radius: 8px;
    background: linear-gradient(135deg, #667eea, #764ba2);
    border: none;
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
    }

    i {
      margin-right: 4px;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .page-header {
    padding: 20px 16px;

    .header-content {
      flex-direction: column;
      gap: 16px;
      text-align: center;

      .title-section .page-title {
        font-size: 24px;
      }
    }
  }

  .search-section {
    padding: 0 16px 16px;

    .search-card {
      .search-form .search-row {
        grid-template-columns: 1fr;
        gap: 16px;

        .search-buttons {
          justify-content: stretch;

          .search-btn, .reset-btn {
            flex: 1;
          }
        }
      }
    }
  }

  .stats-container {
    padding: 0 16px 16px;
    grid-template-columns: 1fr;
  }

  .table-card {
    margin: 0 16px;
    border-radius: 12px;

    .table-container {
      overflow-x: auto;
    }
  }
}
</style>
