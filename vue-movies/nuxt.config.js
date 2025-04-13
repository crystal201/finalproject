export default {
  // https://nuxtjs.org/deployments/netlify/
  target: "static",

  ssr: false,

  generate: {
    fallback: true,
  },
  build: {
    extend(config) {
      config.watchOptions = {
        aggregateTimeout: 300,
        poll: 1000
      }
    }
  },
  server: {
    host: '0.0.0.0',
    port: 3000,
    watch: {
      usePolling: true
    }
  },
  router: {
    middleware: ['auth'],
    // extendRoutes(routes, resolve) {
    //   routes.push({
    //     name: 'login',
    //     path: '/login',
    //     component: resolve(__dirname, 'pages/login.vue')
    //   })
    // }
  },
  '/api': {
    target: process.env.NODE_ENV === 'development' 
      ? 'http://localhost:8080' 
      : 'http://backend:8080',
    pathRewrite: { '^/api': '' },
    changeOrigin: true
  },
  // Headers of the page
  head: {
    title: "Browse Movies, TV Shows and People",
    meta: [
      { charset: "utf-8" },
      { name: "viewport", content: "width=device-width, initial-scale=1" },
      {
        hid: "description",
        name: "description",
        content: "Browse Movies, TV Shows and People",
      },
      { hid: "author", name: "author", content: "Jason Ujma-Alvis" },
      { hid: "og:locale", property: "og:locale", content: "en_GB" },
      { hid: "og:title", property: "og:title", content: "Movies App" },
      {
        hid: "og:description",
        property: "og:description",
        content: "Browse Movies, TV Shows and People",
      },
      { hid: "og:type", property: "og:type", content: "website" },
      {
        hid: "og:url",
        property: "og:url",
        content: "https://movies.jason.codes/",
      },
      { name: "twitter:card", content: "summary" },
      { name: "twitter:title", content: "Movies" },
      {
        name: "twitter:description",
        content: "Browse Movies, TV Shows and People",
      },
      { name: "twitter:site", content: "@jasonujmaalvis" },
      { name: "twitter:creator", content: "@jasonujmaalvis" },
      {
        name: "twitter:image",
        content: "https://movies.jason.codes/icon-medium.png",
      },
    ],
    link: [
      { rel: "icon", type: "image/x-icon", href: "/favicon.ico" },
      {
        rel: "stylesheet",
        href: "//fonts.googleapis.com/css?family=Roboto:300,400,500",
      },
    ],
  },

  // Global CSS
  css: ["@/assets/css/global.scss"],

  // Plugins to run before rendering page: https://go.nuxtjs.dev/config-plugins
  plugins: [
    "~/plugins/lazyload.js",
    "~/plugins/filters.js",
    "~/plugins/axios.js",
    '~/plugins/auth.js',
    { src: "~/plugins/ga.js", ssr: false },
  ],

  // Auto import components: https://go.nuxtjs.dev/config-components
  components: true,

  // Modules for dev and build (recommended): https://go.nuxtjs.dev/config-modules
  buildModules: [
    // https://go.nuxtjs.dev/eslint
    "@nuxtjs/eslint-module",
  ],

  modules: ["@nuxtjs/dotenv", "@nuxtjs/axios", "@nuxtjs/pwa", "@nuxtjs/proxy"],

  // Axios module configuration: https://go.nuxtjs.dev/config-axios
  axios: {
    baseURL: process.env.NODE_ENV === 'development' 
      ? 'http://localhost:8080' // Khi chạy standalone
      : 'http://cinema-system:8080', // Khi chạy trong Docker
    credentials: true,
    proxy: true
  },
  // PWA module configuration: https://go.nuxtjs.dev/pwa
  pwa: {
    manifest: {
      lang: "en",
      name: "Movies",
      short_name: "Movies",
      description: "Browse Movies, TV Shows and People",
      theme_color: "#2196f3",
      background_color: "#000000",
    },
  },

  // Build Configuration: https://go.nuxtjs.dev/config-build
  loaders: {
    cssModules: {
      camelCase: true,
      localIdentName: "[local]_[hash:base64:5]",
    },
  },

  vue: {
    config: {
      productionTip: false,
      devtools: false,
    },
  },

  env: {
    FRONTEND_URL: process.env.FRONTEND_URL || "",
    API_KEY: process.env.API_KEY || "",
    API_LANG: process.env.API_LANG || "en-US",
    API_COUNTRY: process.env.API_COUNTRY || "GB",
    API_YOUTUBE_KEY: process.env.API_YOUTUBE_KEY || "",
    GA: process.env.GA || "",
  },

  // Customize the progress bar color
  loading: {
    color: "#2196f3",
  },
};
