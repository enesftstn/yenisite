import { Component, type OnInit } from "@angular/core"
import { CommonModule } from "@angular/common"
import { FormsModule } from "@angular/forms"
import type { Router } from "@angular/router"
import { autoPartService, type AutoPart, type AutoPartSearchRequest } from "../../../services"

@Component({
  selector: "app-dashboard",
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
              <button class="btn btn-primary search-btn" (click)="onSearch()" [disabled]="isLoading">
                🔍 Search
              </button>
            </div>
            
            <div class="quick-filters">
              <span class="filter-label">Popular:</span>
              <button class="filter-tag" (click)="onQuickSearch('Brake Pads')">Brake Pads</button>
              <button class="filter-tag" (click)="onQuickSearch('Headlights')">Headlights</button>
              <button class="filter-tag" (click)="onQuickSearch('Bumpers')">Bumpers</button>
              <button class="filter-tag" (click)="onQuickSearch('Engines')">Engines</button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Featured Parts -->
    <section class="featured-parts">
      <div class="container">
        <h3>Featured Parts</h3>
        <!-- Added loading state and error handling -->
        <div *ngIf="isLoading" class="loading-message">
          Loading featured parts...
        </div>
        <div *ngIf="error" class="error-message">
          {{ error }}
        </div>
        <div class="parts-grid" *ngIf="!isLoading && !error">
          <div class="part-card card" *ngFor="let part of featuredParts">
            <div class="part-image">
              <!-- Updated image handling for API data -->
              <img [src]="getPartImage(part)" [alt]="part.title" />
              <div class="condition-badge" [class]="part.conditionType.toLowerCase().replace('_', '-')">
                {{ part.conditionType }}
              </div>
            </div>
            
            <div class="part-info">
              <!-- Updated to use API model properties -->
              <h4>{{ part.title }}</h4>
              <p class="part-details">{{ part.brand }} {{ part.model }} ({{ getYearRange(part) }})</p>
              <div class="part-meta">
                <span class="price">\${{ part.price }}</span>
                <span class="location">📍 {{ getSellerLocation(part) }}</span>
              </div>
              <p class="seller">Sold by: {{ getSellerName(part) }}</p>
              
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
  styles: [
    `
    /* Inherit styles from parent component */
    .loading-message, .error-message {
      text-align: center;
      padding: 2rem;
      margin: 1rem 0;
    }
    
    .error-message {
      color: #dc3545;
      background-color: #f8d7da;
      border: 1px solid #f5c6cb;
      border-radius: 4px;
    }
    
    .loading-message {
      color: #6c757d;
    }
  `,
  ],
})
export class DashboardComponent implements OnInit {
  searchQuery = ""
  featuredParts: AutoPart[] = []
  isLoading = false
  error: string | null = null

  constructor(private router: Router) {}

  async ngOnInit() {
    await this.loadFeaturedParts()
  }

  async loadFeaturedParts() {
    try {
      this.isLoading = true
      this.error = null

      // Search for featured parts
      const searchRequest: AutoPartSearchRequest = {
        page: 0,
        size: 6,
        sortBy: "viewCount",
        sortDirection: "DESC",
      }

      const response = await autoPartService.searchParts(searchRequest)
      this.featuredParts = response.content
    } catch (error) {
      console.error("[v0] Error loading featured parts:", error)
      this.error = "Failed to load featured parts. Please try again later."
    } finally {
      this.isLoading = false
    }
  }

  async onSearch() {
    if (!this.searchQuery.trim()) {
      this.router.navigate(["/browse"])
      return
    }

    // Navigate to browse with search query
    this.router.navigate(["/browse"], {
      queryParams: { q: this.searchQuery },
    })
  }

  onQuickSearch(query: string) {
    this.searchQuery = query
    this.onSearch()
  }

  onListPart() {
    console.log("Opening list part form")
    this.router.navigate(["/my-parts"])
  }

  onContactSeller(part: AutoPart) {
    console.log("Contacting seller for:", part.title)
    this.router.navigate(["/messages"], {
      queryParams: { partId: part.id, sellerId: part.sellerId },
    })
  }

  getPartImage(part: AutoPart): string {
    if (part.images && part.images.length > 0) {
      return part.images[0]
    }
    return `/placeholder.svg?height=200&width=300&query=${encodeURIComponent(part.title)}`
  }

  getYearRange(part: AutoPart): string {
    if (part.yearStart && part.yearEnd) {
      return part.yearStart === part.yearEnd ? part.yearStart.toString() : `${part.yearStart}-${part.yearEnd}`
    }
    return "Universal"
  }

  getSellerLocation(part: AutoPart): string {
    if (part.seller) {
      return `${part.seller.city || "Unknown"}, ${part.seller.state || "Unknown"}`
    }
    return "Location not specified"
  }

  getSellerName(part: AutoPart): string {
    if (part.seller) {
      return `${part.seller.firstName} ${part.seller.lastName}`
    }
    return "Unknown Seller"
  }
}
