import AuthorizationPage from 'pages/AuthorizationPage'
const routes = [
  {
    path: '/',
    name: 'index',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      { path: '', component: () => import('pages/MainPage.vue') }
    ]
  },
  {
    path: '/assetdetails/:id',
    name: 'assetdetails',
    props: true,
    component: () => import('pages/AssetDetailsPage')
  },
  {
    path: '/catalog',
    name: 'catalog',
    props: true,
    component: () => import('pages/IndexPage'),
    children: [
      { path: '', component: () => import('pages/LocationCatalogPage') },
      { path: '/location', component: () => import('pages/LocationCatalogPage') },
      { path: '/reason', component: () => import('pages/ReasonCatalogPage') },
      { path: '/priority', component: () => import('pages/PriorityCatalogPage') },
      { path: '/onhold', component: () => import('pages/OnholdCatalogPage') }
    ]
  },
  {
    path: '/login',
    name: 'login',
    component: AuthorizationPage,
    hidden: true
  },
  {
    path: '/logout',
    name: 'accounts-logout',
    component: AuthorizationPage,
    meta: { requireAuth: true }
  },
  {
    path: '/:catchAll(.*)*',
    component: () => import('pages/ErrorNotFound.vue')
  }
]

export default routes
