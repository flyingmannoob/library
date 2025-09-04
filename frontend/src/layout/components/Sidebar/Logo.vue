<template>
  <div class="sidebar-logo-container" :class="{'collapse':collapse}">
    <transition name="sidebarLogoFade">
      <router-link v-if="collapse" key="collapse" class="sidebar-logo-link" to="/">
        <div class="logo-circle">
          <i class="el-icon-reading logo-icon"></i>
        </div>
      </router-link>
      <router-link v-else key="expand" class="sidebar-logo-link" to="/">
        <div class="logo-content">
          <div class="logo-circle">
            <i class="el-icon-reading logo-icon"></i>
          </div>
          <div class="logo-text">
            <h1 class="sidebar-title">{{ title }}</h1>
            <p class="sidebar-subtitle">{{ subtitle }}</p>
          </div>
        </div>
      </router-link>
    </transition>
  </div>
</template>

<script>
export default {
  name: 'SidebarLogo',
  props: {
    collapse: {
      type: Boolean,
      required: true
    }
  },
  data() {
    return {
      title: '图书管理系统',
      subtitle: 'Library Management'
    }
  }
}
</script>

<style lang="scss" scoped>
.sidebarLogoFade-enter-active {
  transition: opacity 0.6s ease;
}

.sidebarLogoFade-enter,
.sidebarLogoFade-leave-to {
  opacity: 0;
}

.sidebar-logo-container {
  position: relative;
  width: 100%;
  height: 80px;
  // 使用与侧边栏主体完全一致的渐变色
  background: linear-gradient(145deg, #6366f1 0%, #8b5cf6 35%, #a855f7 70%, #9333ea 100%);
  text-align: center;
  overflow: hidden;
  // 移除可能造成色差的阴影
  box-shadow: none;

  // 添加与侧边栏一致的光泽效果
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 100%;
    background: linear-gradient(145deg,
      rgba(255, 255, 255, 0.15) 0%,
      rgba(255, 255, 255, 0.05) 30%,
      rgba(255, 255, 255, 0.02) 60%,
      rgba(255, 255, 255, 0.08) 100%);
    pointer-events: none;
    z-index: 1;
  }

  // 移除底部边框，确保无缝连接
  &::after {
    display: none;
  }

  .sidebar-logo-link {
    height: 100%;
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 20px;
    text-decoration: none;
    transition: all 0.3s ease;
    position: relative;
    z-index: 2;

    &:hover {
      background: rgba(255, 255, 255, 0.08);
      backdrop-filter: blur(10px);
    }

    .logo-circle {
      width: 48px;
      height: 48px;
      background: rgba(255, 255, 255, 0.25);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 0 4px 20px rgba(255, 255, 255, 0.1);
      backdrop-filter: blur(15px);
      border: 2px solid rgba(255, 255, 255, 0.3);
      transition: all 0.3s ease;

      .logo-icon {
        font-size: 24px;
        color: white;
        text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
        filter: drop-shadow(0 0 8px rgba(255, 255, 255, 0.3));
      }
    }

    .logo-content {
      display: flex;
      align-items: center;
      gap: 16px;
      width: 100%;
    }

    .logo-text {
      flex: 1;
      text-align: left;

      .sidebar-title {
        margin: 0;
        color: white;
        font-size: 19px;
        font-weight: 700;
        line-height: 1.2;
        text-shadow: 0 2px 6px rgba(0, 0, 0, 0.3);
        font-family: 'Microsoft YaHei', 'PingFang SC', 'Helvetica Neue', Arial, sans-serif;
        letter-spacing: 0.5px;
      }

      .sidebar-subtitle {
        margin: 3px 0 0 0;
        color: rgba(255, 255, 255, 0.85);
        font-size: 11px;
        font-weight: 500;
        line-height: 1;
        text-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        letter-spacing: 0.3px;
      }
    }

    &:hover .logo-circle {
      transform: scale(1.05);
      box-shadow: 0 6px 25px rgba(255, 255, 255, 0.15);
    }
  }

  &.collapse {
    .sidebar-logo-link {
      justify-content: center;
      padding: 0;

      .logo-circle {
        width: 42px;
        height: 42px;

        .logo-icon {
          font-size: 22px;
        }
      }
    }
  }
}
</style>
