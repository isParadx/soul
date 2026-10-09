import axios from 'axios';

// 后端服务地址（与下方baseURL保持一致）
const SERVER_BASE_URL = 'http://localhost:8080';

// 创建自定义的 axios 实例
const customAxios = axios.create({
  baseURL: SERVER_BASE_URL,
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
    'X-Device-Type': 'pc'  // 标识为PC端
  }
});

// 请求拦截器
customAxios.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    // 后端JwtAuthInterceptor读取"token"请求头（兼容Authorization）
    config.headers['token'] = token;
    config.headers.Authorization = `Bearer ${token}`;
  }
  // 确保每次请求都携带设备类型标识
  config.headers['X-Device-Type'] = 'pc';
  return config;
}, error => {
  return Promise.reject(error);
});

// 响应拦截器
customAxios.interceptors.response.use(
  response => {
    // 统一处理响应数据
    const res = response.data;
    if (res.code !== 200) {
      // 业务错误处理
      console.error('API Error:', res.message);
      return Promise.reject(new Error(res.message || '请求失败'));
    }
    return response;
  },
  error => {
    // HTTP错误处理
    if (error.response) {
      switch (error.response.status) {
        case 401:
          console.error('未授权，请重新登录');
          localStorage.removeItem('token');
          sessionStorage.clear();
          window.location.href = '/';
          break;
        case 403:
          console.error('拒绝访问，权限不足');
          break;
        case 500:
          console.error('服务器内部错误');
          break;
        default:
          console.error(`请求错误: ${error.response.status}`);
      }
    } else if (error.code === 'ECONNABORTED') {
      console.error('请求超时，请稍后重试');
    }
    return Promise.reject(error);
  }
);

// 将后端返回的头像相对路径解析为可直接访问的完整地址
export function resolveAvatarUrl(img?: string | null): string {
  if (!img) return '';
  if (img.startsWith('http://') || img.startsWith('https://')) return img;
  return img.startsWith('/') ? SERVER_BASE_URL + img : SERVER_BASE_URL + '/' + img;
}

// 将 customAxios 作为命名导出
export { customAxios, SERVER_BASE_URL };