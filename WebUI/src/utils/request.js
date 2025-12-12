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

    let myrouter=router;

    if (!needAuth) {
      return config;
    }
    let userForJson = localStorage.getItem("userToken")
    if (!userForJson || userForJson == 'null') {

      ElMessage.warning("请先登录！")
      setTimeout(() => {
        // window.location.href = '/Login'
        myrouter.replace('/Login')
        localStorage.removeItem('userToken')
      }, 500)
      return Promise.reject(new Error('未登录，跳转到登录页'));
    }

    try {
      let userToken = JSON.parse(userForJson)
      if (userToken) {
        config.headers.Authorization = userToken
      }
    } catch (error) {
      console.error('解析用户信息失败:', error)
      localStorage.removeItem('userToken')
      ElMessage.warning("请先登录!")
      window.location.href = '/Login'
      return Promise.reject(new Error('未登录，跳转到登录页'));
    }
    return config;
  }
)


//axios的响应 response 拦截器
request.interceptors.response.use(
  (response) => { //成功回调
    if (response.data.code == 401) {
      console.log('@@@@@@@@@@@@@@@401')
      localStorage.removeItem('userToken')
      router.push('/Login')
      ElMessage.warning({
        timeout: 3000
      }, "请先登录！")
    }
    return response.data
  },
  (error) => { //失败回调
    return Promise.reject(error)
  }
)

export default request