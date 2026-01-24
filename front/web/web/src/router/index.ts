import { createRouter, createWebHistory } from 'vue-router';
import Start from '../views/LAR/start.vue';
import Login from '../components/OutMain/Login.vue';
import Register from '../components/OutMain/Register.vue';
import Witeinfo from '../components/OutMain/WriteInfo.vue';


import Master from '../views/Role/master.vue';
import Userinfo from '../components/Master/userManager.vue';
import Doctorinfo from '../components/Master/doctorManage.vue';
import Consultationinfo from '../components/Master/consultationManage.vue';
import Advice from '../components/Master/advice.vue';


import Doctor from '../views/Role/doctor.vue';
import Appointments from '../components/Doctor/Appointments.vue'
import DocInfo from '../components/Doctor/DocInfo.vue'


 import CozeChat from '../components/CozeChat/CozeChat.vue';

const routes = [
   {
    path: '/CozeChat',
    name: 'CozeChat',
    component: CozeChat
  },
  {
    path: '/',
    name: 'Start',
    component: Start
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },

  {
    path: '/writeinfo',
    name: 'Writeinfo',
    component: Witeinfo
  },
  {
    path: '/master',
    name: 'Master',
    component: Master,
    children:[
      {path: '/master',component: Userinfo},
      {path: 'userinfo',component: Userinfo},
      {path: 'doctorinfo',component: Doctorinfo},
      {path: 'consultationinfo',component: Consultationinfo},
      {path: 'advice',component: Advice}
    ]
  },
  {
    path: '/doctor',
    name: 'Doctor',
    component: Doctor,
    children:[
      { path: '/doctor', component: Appointments },
      { path: 'appointments', component: Appointments },
      { path: 'docinfo', component: DocInfo },
    ]
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;