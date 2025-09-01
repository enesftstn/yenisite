import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-messages',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-container">
      <div class="container">
        <h1>💬 Messages</h1>
        <p>Chat with buyers and sellers about parts.</p>
        
        <div class="card">
          <h3>Message Center</h3>
          <p>Your conversations with other users will appear here.</p>
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
export class MessagesComponent { }