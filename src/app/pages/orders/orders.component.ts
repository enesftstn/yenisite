import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-container">
      <div class="container">
        <h1>📋 Orders</h1>
        <p>Track your purchase and sale orders.</p>
        
        <div class="orders-content">
          <div class="card">
            <h3>Purchase Orders</h3>
            <p>Parts you've ordered from other sellers.</p>
          </div>
          
          <div class="card">
            <h3>Sale Orders</h3>
            <p>Parts others have ordered from you.</p>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .page-container {
      padding: 2rem 0;
      min-height: calc(100vh - 120px);
    }
    
    .orders-content {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
      gap: 2rem;
      margin-top: 2rem;
    }
    
    h1 {
      margin-bottom: 1rem;
      color: var(--dark-gray);
    }
  `]
})
export class OrdersComponent { }