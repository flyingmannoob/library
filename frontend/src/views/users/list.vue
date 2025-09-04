<template>
  <div class="users-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">
            <i class="el-icon-user-solid"></i>
            用户管理
          </h1>
          <p class="page-subtitle">管理系统用户账户和权限</p>
        </div>
        <div class="action-section">
          <el-button
            type="primary"
            size="medium"
            icon="el-icon-plus"
            class="add-button"
            @click="handleCreate"
          >
            添加用户
          </el-button>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-container">
      <div class="stats-card">
        <div class="stats-icon admin-icon">
          <i class="el-icon-s-custom"></i>
        </div>
        <div class="stats-content">
          <div class="stats-number">{{ adminCount }}</div>
          <div class="stats-label">管理员</div>
        </div>
      </div>
      <div class="stats-card">
        <div class="stats-icon user-icon">
          <i class="el-icon-user"></i>
        </div>
        <div class="stats-content">
          <div class="stats-number">{{ userCount }}</div>
          <div class="stats-label">普通用户</div>
        </div>
      </div>
      <div class="stats-card">
        <div class="stats-icon total-icon">
          <i class="el-icon-s-data"></i>
        </div>
        <div class="stats-content">
          <div class="stats-number">{{ totalUsers }}</div>
          <div class="stats-label">总用户数</div>
        </div>
      </div>
    </div>

    <!-- 用户表格卡片 -->
    <div class="table-card">
      <div class="card-header">
        <h3 class="card-title">
          <i class="el-icon-menu"></i>
          用户列表
        </h3>
      </div>

      <div class="table-container">
        <el-table
          :key="tableKey"
          v-loading="listLoading"
          :data="list"
          fit
          highlight-current-row
          class="modern-table"
          empty-text="暂无用户数据"
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
            label="用户信息"
            min-width="200"
          >
            <template slot-scope="scope">
              <div class="user-info">
                <div class="avatar-container">
                  <el-avatar
                    :size="40"
                    :src="scope.row.avatar"
                    icon="el-icon-user-solid"
                  />
                </div>
                <div class="user-details">
                  <div class="username">{{ scope.row.username }}</div>
                  <div class="email">{{ scope.row.email }}</div>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column
            label="角色"
            width="120"
            align="center"
          >
            <template slot-scope="scope">
              <el-tag
                :type="scope.row.role === 'admin' ? 'danger' : 'success'"
                :effect="scope.row.role === 'admin' ? 'dark' : 'plain'"
                class="role-tag"
              >
                <i :class="scope.row.role === 'admin' ? 'el-icon-s-custom' : 'el-icon-user'"></i>
                {{ scope.row.role === 'admin' ? '管理员' : '普通用户' }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column
            label="操作"
            align="center"
            width="240"
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
                  @click="handleEdit(scope.row)"
                  v-el-tooltip.top="'修改密码'"
                />
                <el-button
                  v-if="scope.row.username !== 'admin'"
                  type="danger"
                  size="small"
                  icon="el-icon-delete"
                  circle
                  class="action-btn delete-btn"
                  @click="handleDelete(scope.row)"
                  v-el-tooltip.top="'删除用户'"
                />
                <span
                  v-else
                  class="protected-badge"
                  v-el-tooltip.top="'系统管理员，受保护账户'"
                >
                  <i class="el-icon-lock"></i>
                </span>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <!-- 添加用户对话框 -->
    <el-dialog
      title="添加新用户"
      :visible.sync="dialogFormVisible"
      width="500px"
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
          <div class="form-row">
            <el-form-item
              label="用户名"
              prop="username"
              class="form-item"
            >
              <el-input
                v-model="temp.username"
                placeholder="请输入用户名"
                prefix-icon="el-icon-user"
                class="form-input"
              />
            </el-form-item>
          </div>

          <div class="form-row">
            <el-form-item
              label="邮箱"
              prop="email"
              class="form-item"
            >
              <el-input
                v-model="temp.email"
                placeholder="请输入邮箱地址"
                prefix-icon="el-icon-message"
                class="form-input"
              />
            </el-form-item>
          </div>

          <div class="form-row">
            <el-form-item
              label="密码"
              prop="password"
              class="form-item"
            >
              <el-input
                v-model="temp.password"
                type="password"
                placeholder="请输入密码"
                prefix-icon="el-icon-lock"
                show-password
                class="form-input"
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
          @click="createData()"
          class="confirm-btn"
        >
          <i class="el-icon-check"></i>
          确认添加
        </el-button>
      </div>
    </el-dialog>

    <!-- 修改密码对话框 -->
    <el-dialog
      title="修改用户密码"
      :visible.sync="editPasswordVisible"
      width="450px"
      class="modern-dialog"
      :close-on-click-modal="false"
    >
      <div class="dialog-content">
        <el-form
          ref="editPasswordForm"
          :rules="editPasswordRules"
          :model="editPasswordData"
          label-position="top"
          class="modern-form"
        >
          <div class="form-row">
            <el-form-item label="用户名" class="form-item">
              <el-input
                v-model="editPasswordData.username"
                disabled
                prefix-icon="el-icon-user"
                class="form-input"
              />
            </el-form-item>
          </div>

          <div class="form-row">
            <el-form-item
              label="新密码"
              prop="newPassword"
              class="form-item"
            >
              <el-input
                v-model="editPasswordData.newPassword"
                type="password"
                placeholder="请输入新密码"
                prefix-icon="el-icon-lock"
                show-password
                class="form-input"
              />
            </el-form-item>
          </div>

          <div class="form-row">
            <el-form-item
              label="确认新密码"
              prop="confirmPassword"
              class="form-item"
            >
              <el-input
                v-model="editPasswordData.confirmPassword"
                type="password"
                placeholder="请再次输入新密码"
                prefix-icon="el-icon-lock"
                show-password
                class="form-input"
              />
            </el-form-item>
          </div>
        </el-form>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button
          @click="editPasswordVisible = false"
          class="cancel-btn"
        >
          取消
        </el-button>
        <el-button
          type="primary"
          @click="updatePassword()"
          class="confirm-btn"
        >
          <i class="el-icon-check"></i>
          确认修改
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getUsers, createUser, deleteUser, updateUser } from '@/api/admin'
import { parseTime } from '@/utils'
import { mapGetters } from 'vuex'

export default {
  name: 'UserList',
  filters: {
    parseTime
  },
  data() {
    const validatePasswordConfirm = (rule, value, callback) => {
      if (value !== this.editPasswordData.newPassword) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }

    return {
      tableKey: 0,
      list: null,
      total: 0,
      listLoading: true,
      temp: {
        username: '',
        password: '',
        email: ''
      },
      dialogFormVisible: false,
      editPasswordVisible: false,
      editPasswordData: {
        userId: null,
        username: '',
        newPassword: '',
        confirmPassword: ''
      },
      rules: {
        username: [
          { required: true, message: '用户名是必填项', trigger: 'blur' },
          { min: 3, max: 20, message: '用户名长度必须在3-20位之间', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '密码是必填项', trigger: 'blur' },
          { min: 6, message: '密码长度至少6位', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '邮箱是必填项', trigger: 'blur' },
          { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
        ]
      },
      editPasswordRules: {
        newPassword: [
          { required: true, message: '新密码是必填项', trigger: 'blur' },
          { min: 6, message: '密码长度至少6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '确认密码是必填项', trigger: 'blur' },
          { validator: validatePasswordConfirm, trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    ...mapGetters(['name']),
    currentUser() {
      return this.name
    },
    // 统计数据计算
    adminCount() {
      return this.list ? this.list.filter(user => user.role === 'admin').length : 0
    },
    userCount() {
      return this.list ? this.list.filter(user => user.role === 'user').length : 0
    },
    totalUsers() {
      return this.list ? this.list.length : 0
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      getUsers().then(response => {
        this.list = response.data.items
        this.total = response.data.total
        this.listLoading = false
      }).catch(() => {
        this.listLoading = false
      })
    },
    resetTemp() {
      this.temp = {
        username: '',
        password: '',
        email: ''
      }
    },
    handleCreate() {
      this.resetTemp()
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    createData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          createUser(this.temp).then(() => {
            this.dialogFormVisible = false
            this.$notify({
              title: '成功',
              message: '用户创建成功',
              type: 'success',
              duration: 2000
            })
            this.getList()
          }).catch(error => {
            this.$notify({
              title: '错误',
              message: error.message || '创建用户失败',
              type: 'error',
              duration: 2000
            })
          })
        }
      })
    },
    handleEdit(row) {
      this.editPasswordData = {
        userId: row.id,
        username: row.username,
        newPassword: '',
        confirmPassword: ''
      }
      this.editPasswordVisible = true
      this.$nextTick(() => {
        this.$refs['editPasswordForm'].clearValidate()
      })
    },
    updatePassword() {
      this.$refs['editPasswordForm'].validate((valid) => {
        if (valid) {
          const updateData = {
            password: this.editPasswordData.newPassword
          }

          updateUser(this.editPasswordData.userId, updateData).then(() => {
            this.editPasswordVisible = false
            this.$notify({
              title: '成功',
              message: '密码修改成功',
              type: 'success',
              duration: 2000
            })
            // 重置表单数据
            this.editPasswordData = {
              userId: null,
              username: '',
              newPassword: '',
              confirmPassword: ''
            }
          }).catch(error => {
            this.$notify({
              title: '错误',
              message: error.message || '修改密码失败',
              type: 'error',
              duration: 2000
            })
          })
        }
      })
    },
    handleDelete(row) {
      if (row.username === 'admin') {
        this.$notify({
          title: '警告',
          message: '不能删除系统管理员账户',
          type: 'warning',
          duration: 2000
        })
        return
      }

      this.$confirm('确认删除该用户?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteUser(row.id).then(() => {
          this.$notify({
            title: '成功',
            message: '删除成功',
            type: 'success',
            duration: 2000
          })
          const index = this.list.indexOf(row)
          this.list.splice(index, 1)
        }).catch(error => {
          this.$notify({
            title: '错误',
            message: error.message || '删除用户失败',
            type: 'error',
            duration: 2000
          })
        })
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.users-container {
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

      &.admin-icon {
        background: linear-gradient(135deg, #ff6b6b, #ee5a24);
      }

      &.user-icon {
        background: linear-gradient(135deg, #4ecdc4, #26d0ce);
      }

      &.total-icon {
        background: linear-gradient(135deg, #a8edea, #fed6e3);
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

      .user-info {
        display: flex;
        align-items: center;
        gap: 12px;

        .avatar-container {
          ::v-deep .el-avatar {
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
          }
        }

        .user-details {
          .username {
            font-size: 16px;
            font-weight: 600;
            color: #2c3e50;
            margin-bottom: 2px;
          }

          .email {
            font-size: 13px;
            color: #7f8c8d;
          }
        }
      }

      .role-tag {
        font-weight: 500;
        padding: 6px 12px;
        border-radius: 8px;

        i {
          margin-right: 4px;
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

        .protected-badge {
          display: inline-flex;
          align-items: center;
          padding: 6px 12px;
          background: #f8f9fa;
          color: #6c757d;
          border-radius: 6px;
          font-size: 12px;

          i {
            margin-right: 4px;
          }
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
    .form-row {
      margin-bottom: 20px;

      &:last-child {
        margin-bottom: 0;
      }
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
