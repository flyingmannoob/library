<template>
  <div class="register-container">
    <div class="register-wrapper">
      <div class="register-form-container">
        <div class="register-header">
          <div class="logo-container">
            <div class="logo-circle">
              <svg-icon icon-class="user" class="logo-icon" />
            </div>
            <h1 class="system-title">图书管理系统</h1>
            <p class="system-subtitle">Library Management System</p>
          </div>
        </div>

        <el-form
          ref="registerForm"
          :model="registerForm"
          :rules="registerRules"
          class="register-form"
          autocomplete="on"
          label-position="left"
        >
          <div class="title-container">
            <h3 class="title">创建新账户</h3>
            <p class="subtitle">请填写以下信息完成注册</p>
          </div>

          <el-form-item prop="username" class="form-item-custom">
            <div class="input-wrapper">
              <span class="svg-container">
                <svg-icon icon-class="user" />
              </span>
              <el-input
                ref="username"
                v-model="registerForm.username"
                placeholder="请输入用户名 (3-20位)"
                name="username"
                type="text"
                tabindex="1"
                autocomplete="on"
                class="custom-input"
              />
            </div>
          </el-form-item>

          <el-form-item prop="email" class="form-item-custom">
            <div class="input-wrapper">
              <span class="svg-container">
                <svg-icon icon-class="form" />
              </span>
              <el-input
                ref="email"
                v-model="registerForm.email"
                placeholder="请输入邮箱地址"
                name="email"
                type="email"
                tabindex="2"
                autocomplete="on"
                class="custom-input"
              />
            </div>
          </el-form-item>

          <el-tooltip v-model="capsTooltip" content="大写锁定已开启" placement="right" manual>
            <el-form-item prop="password" class="form-item-custom">
              <div class="input-wrapper">
                <span class="svg-container">
                  <svg-icon icon-class="password" />
                </span>
                <el-input
                  :key="passwordType"
                  ref="password"
                  v-model="registerForm.password"
                  :type="passwordType"
                  placeholder="请输入密码 (至少6位)"
                  name="password"
                  tabindex="3"
                  autocomplete="on"
                  class="custom-input"
                  @keyup.native="checkCapslock"
                  @blur="capsTooltip = false"
                />
                <span class="show-pwd" @click="showPwd">
                  <svg-icon :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'" />
                </span>
              </div>
            </el-form-item>
          </el-tooltip>

          <el-form-item prop="confirmPassword" class="form-item-custom">
            <div class="input-wrapper">
              <span class="svg-container">
                <svg-icon icon-class="password" />
              </span>
              <el-input
                :key="confirmPasswordType"
                ref="confirmPassword"
                v-model="registerForm.confirmPassword"
                :type="confirmPasswordType"
                placeholder="请再次输入密码"
                name="confirmPassword"
                tabindex="4"
                autocomplete="on"
                class="custom-input"
                @keyup.native="checkCapslock"
                @blur="capsTooltip = false"
              />
              <span class="show-pwd" @click="showConfirmPwd">
                <svg-icon :icon-class="confirmPasswordType === 'password' ? 'eye' : 'eye-open'" />
              </span>
            </div>
          </el-form-item>

          <el-button
            :loading="loading"
            type="primary"
            class="register-button"
            @click.native.prevent="handleRegister"
          >
            <span v-if="!loading">立即注册</span>
            <span v-else>注册中...</span>
          </el-button>

          <div class="register-tips">
            <el-alert
              title="注册须知"
              type="warning"
              :closable="false"
              show-icon
            >
              <div slot="default">
                <p>• 用户名长度为3-20位字符</p>
                <p>• 密码至少6位，建议包含字母和数字</p>
                <p>• 请确保邮箱地址真实有效</p>
              </div>
            </el-alert>
          </div>

          <div class="login-link">
            <span>已有账号？</span>
            <router-link to="/login" class="link-type">立即登录</router-link>
          </div>
        </el-form>
      </div>
    </div>

    <!-- 装饰性背景元素 -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>
  </div>
</template>

<script>
import { register } from '@/api/user'
import { setToken } from '@/utils/auth'

export default {
  name: 'Register',
  data() {
    const validateUsername = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入用户名'))
      } else if (value.length < 3 || value.length > 20) {
        callback(new Error('用户名长度必须在3-20位之间'))
      } else {
        callback()
      }
    }
    const validatePassword = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入密码'))
      } else if (value.length < 6) {
        callback(new Error('密码长度至少6位'))
      } else {
        callback()
      }
    }
    const validateConfirmPassword = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请确认密码'))
      } else if (value !== this.registerForm.password) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
    const validateEmail = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入邮箱'))
      } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
        callback(new Error('邮箱格式不正确'))
      } else {
        callback()
      }
    }
    return {
      registerForm: {
        username: '',
        password: '',
        confirmPassword: '',
        email: ''
      },
      registerRules: {
        username: [{ required: true, trigger: 'blur', validator: validateUsername }],
        password: [{ required: true, trigger: 'blur', validator: validatePassword }],
        confirmPassword: [{ required: true, trigger: 'blur', validator: validateConfirmPassword }],
        email: [{ required: true, trigger: 'blur', validator: validateEmail }]
      },
      passwordType: 'password',
      confirmPasswordType: 'password',
      capsTooltip: false,
      loading: false,
      showDialog: false,
      redirect: undefined,
      otherQuery: {}
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        const query = route.query
        if (query) {
          this.redirect = query.redirect
          this.otherQuery = this.getOtherQuery(query)
        }
      },
      immediate: true
    }
  },
  created() {
    // window.addEventListener('storage', this.afterQRScan)
  },
  mounted() {
    if (this.registerForm.username === '') {
      this.$refs.username.focus()
    } else if (this.registerForm.password === '') {
      this.$refs.password.focus()
    }
  },
  destroyed() {
    // window.removeEventListener('storage', this.afterQRScan)
  },
  methods: {
    checkCapslock(e) {
      const { key } = e
      this.capsTooltip = key && key.length === 1 && (key >= 'A' && key <= 'Z')
    },
    showPwd() {
      if (this.passwordType === 'password') {
        this.passwordType = ''
      } else {
        this.passwordType = 'password'
      }
      this.$nextTick(() => {
        this.$refs.password.focus()
      })
    },
    showConfirmPwd() {
      if (this.confirmPasswordType === 'password') {
        this.confirmPasswordType = ''
      } else {
        this.confirmPasswordType = 'password'
      }
      this.$nextTick(() => {
        this.$refs.confirmPassword.focus()
      })
    },
    handleRegister() {
      this.$refs.registerForm.validate(valid => {
        if (valid) {
          this.loading = true
          register({
            username: this.registerForm.username,
            password: this.registerForm.password,
            email: this.registerForm.email
          }).then(response => {
            const { data } = response
            setToken(data.token)
            this.$router.push({ path: this.redirect || '/', query: this.otherQuery })
            this.$message({
              message: '注册成功！欢迎使用',
              type: 'success'
            })
            this.loading = false
          }).catch(error => {
            this.loading = false
            this.$message({
              message: error.message || '注册失败',
              type: 'error'
            })
          })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    getOtherQuery(query) {
      return Object.keys(query).reduce((acc, cur) => {
        if (cur !== 'redirect') {
          acc[cur] = query[cur]
        }
        return acc
      }, {})
    }
  }
}
</script>

<style lang="scss">
/* 重置Element UI的输入框样式 - 注册页面 */
.register-container {
  .el-input {
    display: inline-block;
    height: 50px;
    width: 100%;

    input {
      background: rgba(255, 255, 255, 0.95) !important;
      border: 1px solid rgba(255, 255, 255, 0.3) !important;
      border-radius: 8px !important;
      padding: 12px 15px 12px 45px !important;
      color: #333 !important;
      height: 50px !important;
      font-size: 14px;
      transition: all 0.3s ease;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

      &::placeholder {
        color: #999 !important;
        font-size: 14px;
      }

      &:focus {
        background: rgba(255, 255, 255, 1) !important;
        border-color: #409EFF !important;
        box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2), 0 4px 12px rgba(0, 0, 0, 0.15) !important;
        outline: none;
      }

      &:hover {
        border-color: #409EFF !important;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15) !important;
      }

      /* 修复自动填充样式 */
      &:-webkit-autofill {
        -webkit-box-shadow: 0 0 0px 1000px rgba(255, 255, 255, 0.95) inset !important;
        -webkit-text-fill-color: #333 !important;
        background-color: rgba(255, 255, 255, 0.95) !important;
      }

      &:-webkit-autofill:hover {
        -webkit-box-shadow: 0 0 0px 1000px rgba(255, 255, 255, 1) inset !important;
      }

      &:-webkit-autofill:focus {
        -webkit-box-shadow: 0 0 0px 1000px rgba(255, 255, 255, 1) inset !important;
      }
    }
  }

  .el-form-item {
    border: none !important;
    background: transparent !important;
    border-radius: 0 !important;
    margin-bottom: 20px;

    .el-form-item__error {
      color: #ff4757;
      font-size: 12px;
      margin-top: 5px;
      background: rgba(255, 255, 255, 0.9);
      padding: 2px 6px;
      border-radius: 4px;
    }
  }

  /* 修复tooltip样式 */
  .el-tooltip__popper {
    background: rgba(0, 0, 0, 0.8) !important;
    color: white !important;
  }
}
</style>

<style lang="scss" scoped>
$bg: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
$primary-color: #409EFF;
$text-color: #333;
$light-gray: #fff;

.register-container {
  min-height: 100vh;
  width: 100%;
  background: $bg;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  position: relative;

  .register-wrapper {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100%;
    padding: 20px;
  }

  .register-form-container {
    background: rgba(255, 255, 255, 0.15);
    backdrop-filter: blur(10px);
    border-radius: 20px;
    padding: 40px;
    box-shadow:
      0 8px 32px rgba(0, 0, 0, 0.1),
      0 0 0 1px rgba(255, 255, 255, 0.2);
    width: 100%;
    max-width: 450px;
    position: relative;
    z-index: 2;
  }

  .register-header {
    text-align: center;
    margin-bottom: 30px;

    .logo-container {
      .logo-circle {
        width: 70px;
        height: 70px;
        border-radius: 50%;
        background: linear-gradient(135deg, $primary-color, #66b1ff);
        display: flex;
        justify-content: center;
        align-items: center;
        margin: 0 auto 15px;
        box-shadow: 0 4px 20px rgba(64, 158, 255, 0.3);

        .logo-icon {
          font-size: 32px;
          color: white;
        }
      }

      .system-title {
        font-size: 26px;
        color: white;
        margin: 0 0 6px 0;
        font-weight: 600;
        text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
      }

      .system-subtitle {
        font-size: 13px;
        color: rgba(255, 255, 255, 0.8);
        margin: 0;
        font-weight: 300;
      }
    }
  }

  .register-form {
    .title-container {
      text-align: center;
      margin-bottom: 25px;

      .title {
        font-size: 22px;
        color: white;
        margin: 0 0 6px 0;
        font-weight: 500;
      }

      .subtitle {
        font-size: 13px;
        color: rgba(255, 255, 255, 0.7);
        margin: 0;
      }
    }

    .form-item-custom {
      margin-bottom: 20px;

      .input-wrapper {
        position: relative;

        .svg-container {
          position: absolute;
          left: 15px;
          top: 50%;
          transform: translateY(-50%);
          color: #666;
          font-size: 16px;
          z-index: 3;
        }

        .show-pwd {
          position: absolute;
          right: 15px;
          top: 50%;
          transform: translateY(-50%);
          font-size: 16px;
          color: #666;
          cursor: pointer;
          user-select: none;
          z-index: 3;
          transition: color 0.3s ease;

          &:hover {
            color: $primary-color;
          }
        }
      }
    }

    .register-button {
      width: 100%;
      height: 50px;
      border-radius: 8px;
      font-size: 16px;
      font-weight: 600;
      background: linear-gradient(135deg, $primary-color, #66b1ff);
      border: none;
      box-shadow: 0 4px 20px rgba(64, 158, 255, 0.4);
      transition: all 0.3s ease;
      margin-top: 10px;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 6px 25px rgba(64, 158, 255, 0.5);
      }

      &:active {
        transform: translateY(0);
      }
    }

    .register-tips {
      margin: 20px 0;

      .el-alert {
        background: rgba(255, 255, 255, 0.1) !important;
        border: 1px solid rgba(255, 255, 255, 0.2) !important;
        border-radius: 8px;
        padding: 15px;

        .el-alert__content {
          color: white !important;

          .el-alert__title {
            color: white !important;
            font-weight: 500;
            margin-bottom: 8px;
          }

          p {
            margin: 3px 0;
            font-size: 12px;
            color: rgba(255, 255, 255, 0.9) !important;
            line-height: 1.4;
          }
        }

        .el-alert__icon {
          color: rgba(255, 255, 255, 0.8) !important;
        }
      }
    }

    .login-link {
      text-align: center;
      font-size: 14px;
      color: rgba(255, 255, 255, 0.8);
      margin-top: 15px;

      .link-type {
        color: #66b1ff;
        text-decoration: none;
        font-weight: 500;
        transition: color 0.3s ease;

        &:hover {
          color: white;
        }
      }
    }
  }

  .bg-decoration {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    overflow: hidden;
    z-index: 1;

    .circle {
      position: absolute;
      border-radius: 50%;
      opacity: 0.1;
      pointer-events: none;

      &.circle-1 {
        width: 350px;
        height: 350px;
        top: -100px;
        left: -150px;
        background: radial-gradient(circle, rgba(255, 255, 255, 0.3) 0%, rgba(255, 255, 255, 0) 70%);
      }

      &.circle-2 {
        width: 250px;
        height: 250px;
        top: 30%;
        right: -100px;
        background: radial-gradient(circle, rgba(255, 255, 255, 0.2) 0%, rgba(255, 255, 255, 0) 70%);
      }

      &.circle-3 {
        width: 450px;
        height: 450px;
        bottom: -150px;
        left: 50%;
        transform: translateX(-50%);
        background: radial-gradient(circle, rgba(255, 255, 255, 0.2) 0%, rgba(255, 255, 255, 0) 70%);
      }
    }
  }
}

.link-type {
  color: #66b1ff;
  text-decoration: none;

  &:hover {
    color: white;
  }
}
</style>
