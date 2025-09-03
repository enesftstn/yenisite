import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-favorites',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-container">
      <div class="container">
        <h1>❤️ Favorites</h1>
        <p>Your saved and favorite auto parts.</p>
        
        <div class="card">
          <h3>Saved Parts</h3>
          <p>Parts you've marked as favorites will be displayed here.</p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .page-container {
      padding: 2rem 0;
      min-height: calc(100vh - 120px);
    }
    
    h1 {
      margin-bottom: 1rem;
      color: var(--dark-gray);
    }
  `]
})
export class FavoritesComponent { }
