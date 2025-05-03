export default function ({ store, redirect, route }) {
  const protectedRoutes = ['/booking', '/history', '/order'];
  if (protectedRoutes.includes(route.path)) {
      if (!store.state.auth.token) {
          return redirect('/login');
      }
  }
}