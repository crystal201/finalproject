export default function ({ store, redirect, route }) {
  if (route.path === '/login' || route.path === '/register') return;
  if (!store.state.isAuthenticated) {
    return redirect('/login');
  }
}