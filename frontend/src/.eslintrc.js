// 文件: 'frontend/..eslintrc.js'
module.exports = {
  root: true,
  env: {
    browser: true,
    node: true,
    es6: true
  },
  extends: ['plugin:vue/recommended', 'eslint:recommended'],
  parserOptions: {
    parser: 'babel-eslint',
    ecmaVersion: 2020,
    sourceType: 'module'
  },
  rules: {
    // 在这里按需添加/调整规则
  }
};

// /*
// 可选：若需临时关闭开发时的 ESLint 校验，添加以下文件
// 文件: 'frontend/vue.config.js'
// */
// module.exports = {
//   lintOnSave: false
// };