import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  scrollBehavior(to, from, savedPosition) {
    return savedPosition || { left: 0, top: 0 }
  },
  routes: [
    {
      path: '/',
      name: 'Dashboard',
      component: () => import('../views/ReservaCancha/DashboardView.vue'),
      meta: {
        title: 'Panel principal',
      },
    },
    {
      path: '/canchas',
      name: 'Canchas',
      component: () => import('../views/ReservaCancha/CanchasView.vue'),
      meta: {
        title: 'Canchas',
      },
    },
    {
      path: '/reservas',
      name: 'Reservas',
      component: () => import('../views/ReservaCancha/ReservasView.vue'),
      meta: {
        title: 'Reservas',
      },
    },
    {
      path: '/reservas/nueva',
      name: 'Nueva Reserva',
      component: () => import('../views/ReservaCancha/NuevaReservaView.vue'),
      meta: {
        title: 'Nueva reserva',
      },
    },
    {
      path: '/reservas/:id',
      name: 'Detalle Reserva',
      component: () => import('../views/ReservaCancha/DetalleReservaView.vue'),
      meta: {
        title: 'Detalle de reserva',
      },
    },
    {
      path: '/profile',
      name: 'Profile',
      component: () => import('../views/Profile.vue'),
      meta: {
        title: 'Editar perfil',
      },
    },
    {
      path: '/signin',
      name: 'Signin',
      component: () => import('../views/Auth/Signin.vue'),
      meta: {
        title: 'Iniciar sesión',
      },
    },
    {
      path: '/signup',
      name: 'Signup',
      component: () => import('../views/Auth/Signup.vue'),
      meta: {
        title: 'Registrarse',
      },
    },
    {
      path: '/error-404',
      name: '404 Error',
      component: () => import('../views/Errors/FourZeroFour.vue'),
      meta: {
        title: 'Página no encontrada',
      },
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'NotFound',
      component: () => import('../views/Errors/FourZeroFour.vue'),
      meta: {
        title: 'Página no encontrada',
      },
    },
  ],
})

export default router

router.beforeEach((to, from, next) => {
  document.title = `${to.meta.title || 'Panel principal'} | ReservaCancha`
  next()
})
