import { AxiosInstance } from 'axios';

declare module 'axios' {
  export const customAxios: AxiosInstance;
}