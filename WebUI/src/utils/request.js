import axios from 'axios'

let userForJson = localStorage.getItem("userInfo")

let userToken = JSON.parse(userForJson)

//创建axios实例对象
const request = axios.create({
  headers: {
    Authorization: userToken
  },
  baseURL: '/api',
  timeout: 600000,
  withCredentials: true, //允许携带cookie
})

//axios的响应 response 拦截器
request.interceptors.response.use(
  (response) => { //成功回调
    return response.data
  },
  (error) => { //失败回调
    return Promise.reject(error)
  }
)

export default request