/** @type {import('tailwindcss').Config} */
export default {
  darkMode: 'class', // 启用基于 class 的暗黑模式
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}", // 扫描所有 Vue 组件
  ],
  theme: {
    extend: {
      colors: {
        primary: '#00b1eb', // 自定义颜色
      },
      keyframes: { // 搬运自原文件的动画
        float: {
          '0%, 100%': { transform: 'translateY(0) translateX(0) scale(1)' },
          '50%': { transform: 'translateY(-15px) translateX(15px) scale(1.05)' },
        }
      },
      animation: {
        float: 'float 12s ease-in-out infinite',
      }
    },
  },
  plugins: [],
}