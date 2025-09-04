<template>
  <div class="login-container">
    <div class="login-wrapper">
      <div class="login-form-container">
        <div class="login-header">
          <div class="logo-container">
            <div class="logo-circle">
              <svg-icon icon-class="user" class="logo-icon" />
            </div>
            <h1 class="system-title">图书管理系统</h1>
            <p class="system-subtitle">Library Management System</p>
          </div>
        </div>

        <el-form
          ref="loginForm"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
          auto-complete="on"
          label-position="left"
        >
          <div class="title-container">
            <h3 class="title">登录账户</h3>
            <p class="subtitle">请输入您的账户信息</p>
          </div>

          <el-form-item prop="username" class="form-item-custom">
            <div class="input-wrapper">
              <span class="svg-container">
                <svg-icon icon-class="user" />
              </span>
              <el-input
                ref="username"
                v-model="loginForm.username"
                placeholder="请输入用户名"
                name="username"
                type="text"
                tabindex="1"
                auto-complete="on"
                class="custom-input"
              />
            </div>
          </el-form-item>

          <el-form-item prop="password" class="form-item-custom">
            <div class="input-wrapper">
              <span class="svg-container">
                <svg-icon icon-class="password" />
              </span>
              <el-input
                :key="passwordType"
                ref="password"
                v-model="loginForm.password"
                :type="passwordType"
                placeholder="请输入密码"
                name="password"
                tabindex="2"
                auto-complete="on"
                class="custom-input"
                @keyup.enter.native="handleLogin"
              />
              <span class="show-pwd" @click="showPwd">
                <svg-icon :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'" />
              </span>
            </div>
          </el-form-item>

          <el-button
            :loading="loading"
            type="primary"
            class="login-button"
            @click.native.prevent="handleLogin"
          >
            <span v-if="!loading">登录</span>
            <span v-else>登录中...</span>
          </el-button>

          <div class="demo-tips">
            <el-alert
              title="演示账号"
              type="info"
              :closable="false"
              show-icon
            >
              <div slot="default">
                <p><strong>管理员：</strong>admin / admin123</p>
              </div>
            </el-alert>
          </div>

          <div class="register-link">
            <span>还没有账号？</span>
            <router-link to="/register" class="link-type">立即注册</router-link>
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
import { validUsername } from '@/utils/validate'

export default {
  name: 'Login',
  data() {
    const validateUsername = (rule, value, callback) => {
      if (!validUsername(value)) {
        callback(new Error('Please enter the correct user name'))
      } else {
        callback()
      }
    }
    const validatePassword = (rule, value, callback) => {
      if (value.length < 6) {
        callback(new Error('The password can not be less than 6 digits'))
      } else {
        callback()
      }
    }
    return {
      loginForm: {
        username: 'admin',
        password: 'admin123'
      },
      loginRules: {
        username: [{ required: true, trigger: 'blur', validator: validateUsername }],
        password: [{ required: true, trigger: 'blur', validator: validatePassword }]
      },
      loading: false,
      passwordType: 'password',
      redirect: undefined
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  methods: {
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
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          this.$store.dispatch('user/login', this.loginForm).then(() => {
            this.$router.push({ path: this.redirect || '/' })
            this.loading = false
          }).catch(() => {
            this.loading = false
          })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    }
  }
}
</script>

<style lang="scss">
/* 重置Element UI的输入框样式 */
.login-container {
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
    }
  }
}
</style>

<style lang="scss" scoped>
$bg: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
$primary-color: #409EFF;
$text-color: #333;
$light-gray: #fff;
$input-bg: rgba(255, 255, 255, 0.95);

.login-container {
  min-height: 100vh;
  width: 100%;
  background: $bg;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  position: relative;

  .login-wrapper {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100%;
  }

  .login-form-container {
    background: rgba(255, 255, 255, 0.15);
    backdrop-filter: blur(10px);
    border-radius: 20px;
    padding: 50px 40px;
    box-shadow:
      0 8px 32px rgba(0, 0, 0, 0.1),
      0 0 0 1px rgba(255, 255, 255, 0.2);
    width: 100%;
    max-width: 420px;
    position: relative;
    z-index: 2;
  }

  .login-header {
    text-align: center;
    margin-bottom: 40px;

    .logo-container {
      .logo-circle {
        width: 80px;
        height: 80px;
        border-radius: 50%;
        background: linear-gradient(135deg, $primary-color, #66b1ff);
        display: flex;
        justify-content: center;
        align-items: center;
        margin: 0 auto 20px;
        box-shadow: 0 4px 20px rgba(64, 158, 255, 0.3);

        .logo-icon {
          font-size: 36px;
          color: white;
        }
      }

      .system-title {
        font-size: 28px;
        color: white;
        margin: 0 0 8px 0;
        font-weight: 600;
        text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
      }

      .system-subtitle {
        font-size: 14px;
        color: rgba(255, 255, 255, 0.8);
        margin: 0;
        font-weight: 300;
      }
    }
  }

  .login-form {
    .title-container {
      text-align: center;
      margin-bottom: 30px;

      .title {
        font-size: 24px;
        color: white;
        margin: 0 0 8px 0;
        font-weight: 500;
      }

      .subtitle {
        font-size: 14px;
        color: rgba(255, 255, 255, 0.7);
        margin: 0;
      }
    }

    .form-item-custom {
      margin-bottom: 25px;

      .input-wrapper {
        position: relative;

        .svg-container {
          position: absolute;
          left: 15px;
          top: 50%;
          transform: translateY(-50%);
          color: #666;
          font-size: 18px;
          z-index: 3;
        }

        .show-pwd {
          position: absolute;
          right: 15px;
          top: 50%;
          transform: translateY(-50%);
          font-size: 18px;
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

    .login-button {
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

    .demo-tips {
      margin: 25px 0;

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
          }

          p {
            margin: 5px 0 0 0;
            font-size: 13px;
            color: rgba(255, 255, 255, 0.9) !important;
          }
        }

        .el-alert__icon {
          color: rgba(255, 255, 255, 0.8) !important;
        }
      }
    }

    .register-link {
      text-align: center;
      font-size: 14px;
      color: rgba(255, 255, 255, 0.8);
      margin-top: 20px;

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

  // ...existing background decoration styles...
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
        width: 300px;
        height: 300px;
        top: -100px;
        left: -100px;
        background: radial-gradient(circle, rgba(255, 255, 255, 0.3) 0%, rgba(255, 255, 255, 0) 70%);
      }

      &.circle-2 {
        width: 200px;
        height: 200px;
        bottom: -50px;
        right: -50px;
        background: radial-gradient(circle, rgba(255, 255, 255, 0.3) 0%, rgba(255, 255, 255, 0) 70%);
      }

      &.circle-3 {
        width: 400px;
        height: 400px;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        background: radial-gradient(circle, rgba(255, 255, 255, 0.2) 0%, rgba(255, 255, 255, 0) 70%);
      }
    }
  }
}
</style>
