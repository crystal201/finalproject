export default function ({ store, redirect, route }) {
  const protectedRoutes = ['/book', '/history', '/order'];
  if (protectedRoutes.includes(route.path)) {
    if (!store.state.isAuthenticated) {
      return redirect('/login');
    }
  }
}