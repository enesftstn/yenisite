import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: 'dashboard', loadComponent: () => import('./pages/dashboard/dashboard.component').then(m => m.DashboardComponent) },
  { path: 'browse', loadComponent: () => import('./pages/browse/browse.component').then(m => m.BrowseComponent) },
  { path: 'my-parts', loadComponent: () => import('./pages/my-parts/my-parts.component').then(m => m.MyPartsComponent) },
  { path: 'messages', loadComponent: () => import('./pages/messages/messages.component').then(m => m.MessagesComponent) },
  { path: 'favorites', loadComponent: () => import('./pages/favorites/favorites.component').then(m => m.FavoritesComponent) },
  { path: 'orders', loadComponent: () => import('./pages/orders/orders.component').then(m => m.OrdersComponent) },
  { path: '**', redirectTo: '/dashboard' }
];
