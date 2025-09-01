import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-browse',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-container">
      <div class="container">
        <h1>🔍 Browse Parts</h1>
        <p>Search and browse through available auto parts.</p>
        
        <div class="browse-content">
          <div class="card">
            <h3>Search Filters</h3>
            <p>Advanced search filters will be available here.</p>
          </div>
          
          <div class="card">
            <h3>Parts Catalog</h3>
            <p>Browse through our extensive parts catalog.</p>
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
    
    .browse-content {
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
export class BrowseComponent { }