import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-my-parts',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-container">
      <div class="container">
        <h1>📦 My Parts</h1>
        <p>Manage your listed auto parts and inventory.</p>
        
        <div class="my-parts-content">
          <div class="card">
            <h3>Listed Parts</h3>
            <p>Parts you've listed for sale will appear here.</p>
          </div>
          
          <div class="card">
            <h3>Draft Listings</h3>
            <p>Incomplete or draft listings you're working on.</p>
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
    
    .my-parts-content {
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
export class MyPartsComponent { }