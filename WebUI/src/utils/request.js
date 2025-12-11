import axios from 'axios'
import { ElMessage } from 'element-plus';


import router from '@/router';

//创建axios实例对象
const request = axios.create({
  /*   headers: {
      Authorization: userToken
    }, */
  baseURL: '/api',
  timeout: 600000,
  withCredentials: true, //允许携带cookie
})


request.interceptors.request.use(
  config => {
    console.log(config.needAuth)
    let needAuth = config.needAuth == null ? true : config.needAuth


    if (!needAuth) {
      return config;
    }
    let userForJson = localStorage.getItem("userToken")
    if (!userForJson || userForJson == 'null') {
      console.log(111)

      setTimeout(() => {
        window.location.href = '/Login'
      }, 300)
      localStorage.removeItem('userToken')
      ElMessage.warning("请先登录！")
      return;
    }

    try {
      let userToken = JSON.parse(userForJson)
      if (userToken) {
        config.headers.Authorization = userToken
      }
    } catch (error) {
      console.error('解析用户信息失败:', error)
      localStorage.removeItem('userToken')
      ElMessage.warning("请先登录！")
      window.location.href = '/Login'
    }
    return config;
  }
)


//axios的响应 response 拦截器
request.interceptors.response.use(
  (response) => { //成功回调
    if (response.data.code == 401) {
      console.log('@@@@@@@@@@@@@@@401')
      ElMessage.warning("请先登录！")
      localStorage.removeItem('userToken')
      router.replace('/Login')
      return;
    }
    return response.data
  },
  (error) => { //失败回调
    return Promise.reject(error)
  }
)

export default request