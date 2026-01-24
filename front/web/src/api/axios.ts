import axios from 'axios';

// 创建自定义的 axios 实例
const customAxios = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 1000,
});

// 请求拦截器
customAxios.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, error => {
  return Promise.reject(error);
});
// 将 customAxios 作为命名导出
export { customAxios };