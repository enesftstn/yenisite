import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

interface AutoPart {
  id: number
  name: string
  brand: string
  model: string
  year: string
  price: number
  condition: string
  location: string
  image: string
  seller: string
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <!-- Hero Section -->
    <section class="hero">
      <div class="container">
        <div class="hero-content">
          <h2>Find the Perfect Auto Part</h2>
          <p>Connect with trusted sellers and buyers through our secure platform</p>
          
          <div class="search-section">
            <div class="search-bar">
              <input 
                type="text" 
                [(ngModel)]="searchQuery"
                placeholder="Search for parts, brands, or models..."
                class="search-input"
                (keyup.enter)="onSearch()"
              >
              <button class="btn btn-primary search-btn" (click)="onSearch()">
                🔍 Search
              </button>
            </div>
            
            <div class="quick-filters">
              <span class="filter-label">Popular:</span>
              <button class="filter-tag">Brake Pads</button>
              <button class="filter-tag">Headlights</button>
              <button class="filter-tag">Bumpers</button>
              <button class="filter-tag">Engines</button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Featured Parts -->
    <section class="featured-parts">
      <div class="container">
        <h3>Featured Parts</h3>
        <div class="parts-grid">
          <div class="part-card card" *ngFor="let part of featuredParts">
            <div class="part-image">
              <img [src]="part.image" [alt]="part.name" />
              <div class="condition-badge" [class]="part.condition.toLowerCase().replace(' ', '-')">
                {{ part.condition }}
              </div>
            </div>
            
            <div class="part-info">
              <h4>{{ part.name }}</h4>
              <p class="part-details">{{ part.brand }} {{ part.model }} ({{ part.year }})</p>
              <div class="part-meta">
                <span class="price">\${{ part.price }}</span>
                <span class="location">📍 {{ part.location }}</span>
              </div>
              <p class="seller">Sold by: {{ part.seller }}</p>
              
              <button class="btn btn-primary contact-btn" (click)="onContactSeller(part)">
                Contact Seller
              </button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- How It Works -->
    <section class="how-it-works">
      <div class="container">
        <h3>How It Works</h3>
        <div class="steps-grid">
          <div class="step card">
            <div class="step-icon">🔍</div>
            <h4>1. Search & Browse</h4>
            <p>Find the exact part you need from our extensive database of listings</p>
          </div>
          
          <div class="step card">
            <div class="step-icon">🤝</div>
            <h4>2. Connect Safely</h4>
            <p>We facilitate secure connections between buyers and sellers</p>
          </div>
          
          <div class="step card">
            <div class="step-icon">✅</div>
            <h4>3. Complete Transaction</h4>
            <p>Complete your purchase with confidence through our trusted platform</p>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA Section -->
    <section class="cta-section">
      <div class="container">
        <div class="cta-content">
          <h3>Ready to Start Trading?</h3>
          <p>Join thousands of auto enthusiasts buying and selling parts</p>
          <div class="cta-buttons">
            <button class="btn btn-primary" (click)="onSearch()">Browse Parts</button>
            <button class="btn btn-secondary" (click)="onListPart()">List Your Parts</button>
          </div>
        </div>
      </div>
    </section>
  `,
  styles: [`
    /* Inherit styles from parent component */
  `]
})
export class DashboardComponent {
  searchQuery = "";

  featuredParts: AutoPart[] = [
    {
      id: 1,
      name: "Brake Pads Set",
      brand: "BMW",
      model: "3 Series",
      year: "2018-2023",
      price: 89,
      condition: "New",
      location: "Los Angeles, CA",
      image: "/bmw-brake-pads-automotive-parts.png",
      seller: "AutoParts Pro",
    },
    {
      id: 2,
      name: "LED Headlights",
      brand: "Honda",
      model: "Civic",
      year: "2019-2024",
      price: 245,
      condition: "Like New",
      location: "Miami, FL",
      image: "/honda-civic-led-headlights-automotive.png",
      seller: "Parts Direct",
    },
    {
      id: 3,
      name: "Tailgate Assembly",
      brand: "Ford",
      model: "F-150",
      year: "2020-2023",
      price: 450,
      condition: "Used - Good",
      location: "Dallas, TX",
      image: "/ford-f-150-tailgate-truck-parts.png",
      seller: "Truck Parts Hub",
    },
  ];

  constructor(private router: Router) {}

  onSearch() {
    console.log("Searching for:", this.searchQuery);
    this.router.navigate(['/browse']);
  }

  onListPart() {
    console.log("Opening list part form");
    this.router.navigate(['/my-parts']);
  }

  onContactSeller(part: AutoPart) {
    console.log("Contacting seller for:", part.name);
    this.router.navigate(['/messages']);
  }
}