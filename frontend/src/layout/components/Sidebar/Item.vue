<script>
export default {
  name: 'MenuItem',
  functional: true,
  props: {
    icon: {
      type: String,
      default: ''
    },
    title: {
      type: String,
      default: ''
    }
  },
  render(h, context) {
    const { icon, title } = context.props
    const vnodes = []

    if (icon) {
      if (icon.includes('el-icon')) {
        vnodes.push(<i class={[icon, 'sub-el-icon menu-icon']} />)
      } else {
        vnodes.push(<svg-icon icon-class={icon} class="menu-icon"/>)
      }
    }

    if (title) {
      vnodes.push(<span slot='title' class="menu-title">{(title)}</span>)
    }
    return vnodes
  }
}
</script>

<style scoped>
.sub-el-icon {
  color: currentColor;
  width: 1em;
  height: 1em;
}

.menu-icon {
  margin-right: 12px;
  font-size: 18px;
  width: 18px;
  height: 18px;
  display: inline-block;
  vertical-align: middle;
  opacity: 0.8;
  transition: all 0.3s ease;
}

.menu-title {
  font-size: 14px;
  font-weight: 500;
  letter-spacing: 0.5px;
  transition: all 0.3s ease;
}
</style>
