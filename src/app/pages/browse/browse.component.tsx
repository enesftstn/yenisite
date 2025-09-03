import { Component, type OnInit } from "@angular/core"
import { CommonModule } from "@angular/common"
import { FormsModule } from "@angular/forms"
import type { ActivatedRoute, Router } from "@angular/router"
import { autoPartService, type AutoPart, type AutoPartSearchRequest } from "../../../services"

@Component({
  selector: "app-browse",
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="page-container">
      <div class="container">
        <h1>🔍 Browse Parts</h1>
        <p>Search and browse through available auto parts.</p>
        
        <div class="browse-content">
          <!-- Added comprehensive search filters -->
          <div class="filters-sidebar">
            <div class="card">
              <h3>Search Filters</h3>
              
              <div class="filter-group">
                <label>Search Query</label>
                <input 
                  type="text" 
                  [(ngModel)]="searchRequest.query"
                  placeholder="Search parts, brands, models..."
                  class="form-input"
                  (keyup.enter)="onSearch()"
                >
              </div>

              <div class="filter-group">
                <label>Brand</label>
                <input 
                  type="text" 
                  [(ngModel)]="searchRequest.brand"
                  placeholder="e.g., BMW, Honda, Ford"
                  class="form-input"
                >
              </div>

              <div class="filter-group">
                <label>Model</label>
                <input 
                  type="text" 
                  [(ngModel)]="searchRequest.model"
                  placeholder="e.g., Civic, F-150, 3 Series"
                  class="form-input"
                >
              </div>

              <div class="filter-group">
                <label>Category</label>
                <select [(ngModel)]="searchRequest.category" class="form-input">
                  <option value="">All Categories</option>
                  <option value="Brakes">Brakes</option>
                  <option value="Engine">Engine</option>
                  <option value="Lighting">Lighting</option>
                  <option value="Body">Body</option>
                  <option value="Exhaust">Exhaust</option>
                  <option value="Suspension">Suspension</option>
                </select>
              </div>

              <div class="filter-group">
                <label>Condition</label>
                <select [(ngModel)]="searchRequest.conditionType" class="form-input">
                  <option value="">Any Condition</option>
                  <option value="NEW">New</option>
                  <option value="USED">Used</option>
                  <option value="REFURBISHED">Refurbished</option>
                </select>
              </div>

              <div class="filter-group">
                <label>Price Range</label>
                <div class="price-range">
                  <input 
                    type="number" 
                    [(ngModel)]="searchRequest.minPrice"
                    placeholder="Min"
                    class="form-input price-input"
                  >
                  <span>to</span>
                  <input 
                    type="number" 
                    [(ngModel)]="searchRequest.maxPrice"
                    placeholder="Max"
                    class="form-input price-input"
                  >
                </div>
              </div>

              <div class="filter-group">
                <label>Year Range</label>
                <div class="year-range">
                  <input 
                    type="number" 
                    [(ngModel)]="searchRequest.yearStart"
                    placeholder="From"
                    class="form-input year-input"
                  >
                  <span>to</span>
                  <input 
                    type="number" 
                    [(ngModel)]="searchRequest.yearEnd"
                    placeholder="To"
                    class="form-input year-input"
                  >
                </div>
              </div>

              <button class="btn btn-primary search-btn" (click)="onSearch()" [disabled]="isLoading">
                🔍 Search Parts
              </button>
              <button class="btn btn-secondary clear-btn" (click)="onClearFilters()">
                Clear Filters
              </button>
            </div>
          </div>
          
          <!-- Added parts catalog with real data -->
          <div class="parts-catalog">
            <div class="catalog-header">
              <h3>Parts Catalog</h3>
              <div class="catalog-controls">
                <select [(ngModel)]="searchRequest.sortBy" (change)="onSearch()" class="sort-select">
                  <option value="createdAt">Newest First</option>
                  <option value="price">Price: Low to High</option>
                  <option value="price">Price: High to Low</option>
                  <option value="viewCount">Most Popular</option>
                </select>
              </div>
            </div>

            <div *ngIf="isLoading" class="loading-message">
              Searching for parts...
            </div>

            <div *ngIf="error" class="error-message">
              {{ error }}
            </div>

            <div *ngIf="!isLoading && !error && parts.length === 0" class="no-results">
              No parts found matching your criteria. Try adjusting your filters.
            </div>

            <div class="parts-grid" *ngIf="!isLoading && !error && parts.length > 0">
              <div class="part-card card" *ngFor="let part of parts">
                <div class="part-image">
                  <img [src]="getPartImage(part)" [alt]="part.title" />
                  <div class="condition-badge" [class]="part.conditionType.toLowerCase().replace('_', '-')">
                    {{ part.conditionType }}
                  </div>
                </div>
                
                <div class="part-info">
                  <h4>{{ part.title }}</h4>
                  <p class="part-details">{{ part.brand }} {{ part.model }} ({{ getYearRange(part) }})</p>
                  <p class="part-description">{{ part.description | slice:0:100 }}{{ part.description && part.description.length > 100 ? '...' : '' }}</p>
                  <div class="part-meta">
                    <span class="price">\${{ part.price }}</span>
                    <span class="location">📍 {{ getSellerLocation(part) }}</span>
                  </div>
                  <p class="seller">Sold by: {{ getSellerName(part) }}</p>
                  
                  <div class="part-actions">
                    <button class="btn btn-primary" (click)="onViewPart(part)">
                      View Details
                    </button>
                    <button class="btn btn-secondary" (click)="onContactSeller(part)">
                      Contact Seller
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <!-- Added pagination -->
            <div class="pagination" *ngIf="!isLoading && !error && totalPages > 1">
              <button 
                class="btn btn-secondary" 
                (click)="onPageChange(currentPage - 1)"
                [disabled]="currentPage === 0"
              >
                Previous
              </button>
              
              <span class="page-info">
                Page {{ currentPage + 1 }} of {{ totalPages }}
              </span>
              
              <button 
                class="btn btn-secondary" 
                (click)="onPageChange(currentPage + 1)"
                [disabled]="currentPage >= totalPages - 1"
              >
                Next
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [
    `
    .page-container {
      padding: 2rem 0;
      min-height: calc(100vh - 120px);
    }
    
    .browse-content {
      display: grid;
      grid-template-columns: 300px 1fr;
      gap: 2rem;
      margin-top: 2rem;
    }

    .filters-sidebar {
      position: sticky;
      top: 2rem;
      height: fit-content;
    }

    .filter-group {
      margin-bottom: 1rem;
    }

    .filter-group label {
      display: block;
      margin-bottom: 0.5rem;
      font-weight: 600;
    }

    .form-input {
      width: 100%;
      padding: 0.5rem;
      border: 1px solid #ddd;
      border-radius: 4px;
      font-size: 0.9rem;
    }

    .price-range, .year-range {
      display: flex;
      align-items: center;
      gap: 0.5rem;
    }

    .price-input, .year-input {
      flex: 1;
    }

    .search-btn, .clear-btn {
      width: 100%;
      margin-top: 1rem;
    }

    .clear-btn {
      margin-top: 0.5rem;
    }

    .catalog-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 1rem;
    }

    .sort-select {
      padding: 0.5rem;
      border: 1px solid #ddd;
      border-radius: 4px;
    }

    .parts-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
      gap: 1.5rem;
    }

    .part-card {
      border: 1px solid #ddd;
      border-radius: 8px;
      overflow: hidden;
      transition: transform 0.2s;
    }

    .part-card:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0,0,0,0.1);
    }

    .part-image {
      position: relative;
      height: 200px;
      overflow: hidden;
    }

    .part-image img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .condition-badge {
      position: absolute;
      top: 0.5rem;
      right: 0.5rem;
      padding: 0.25rem 0.5rem;
      border-radius: 4px;
      font-size: 0.75rem;
      font-weight: 600;
      text-transform: uppercase;
    }

    .condition-badge.new {
      background-color: #28a745;
      color: white;
    }

    .condition-badge.used {
      background-color: #ffc107;
      color: #212529;
    }

    .condition-badge.refurbished {
      background-color: #17a2b8;
      color: white;
    }

    .part-info {
      padding: 1rem;
    }

    .part-info h4 {
      margin: 0 0 0.5rem 0;
      font-size: 1.1rem;
    }

    .part-details {
      color: #666;
      font-size: 0.9rem;
      margin: 0 0 0.5rem 0;
    }

    .part-description {
      color: #777;
      font-size: 0.85rem;
      margin: 0 0 1rem 0;
      line-height: 1.4;
    }

    .part-meta {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin: 0.5rem 0;
    }

    .price {
      font-size: 1.2rem;
      font-weight: 600;
      color: #28a745;
    }

    .location {
      font-size: 0.85rem;
      color: #666;
    }

    .seller {
      font-size: 0.85rem;
      color: #666;
      margin: 0.5rem 0 1rem 0;
    }

    .part-actions {
      display: flex;
      gap: 0.5rem;
    }

    .part-actions .btn {
      flex: 1;
      padding: 0.5rem;
      font-size: 0.85rem;
    }

    .pagination {
      display: flex;
      justify-content: center;
      align-items: center;
      gap: 1rem;
      margin-top: 2rem;
    }

    .page-info {
      font-weight: 600;
    }

    .loading-message, .error-message, .no-results {
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
    
    .loading-message, .no-results {
      color: #6c757d;
    }

    @media (max-width: 768px) {
      .browse-content {
        grid-template-columns: 1fr;
      }
      
      .filters-sidebar {
        position: static;
      }
    }
  `,
  ],
})
export class BrowseComponent implements OnInit {
  searchRequest: AutoPartSearchRequest = {
    page: 0,
    size: 12,
    sortBy: "createdAt",
    sortDirection: "DESC",
  }

  parts: AutoPart[] = []
  isLoading = false
  error: string | null = null
  currentPage = 0
  totalPages = 0
  totalElements = 0

  constructor(
    private route: ActivatedRoute,
    private router: Router,
  ) {}

  async ngOnInit() {
    // Get search query from URL parameters
    this.route.queryParams.subscribe((params) => {
      if (params["q"]) {
        this.searchRequest.query = params["q"]
      }
      this.onSearch()
    })
  }

  async onSearch() {
    try {
      this.isLoading = true
      this.error = null

      const response = await autoPartService.searchParts(this.searchRequest)
      this.parts = response.content
      this.currentPage = response.number
      this.totalPages = response.totalPages
      this.totalElements = response.totalElements
    } catch (error) {
      console.error("[v0] Error searching parts:", error)
      this.error = "Failed to search parts. Please try again later."
    } finally {
      this.isLoading = false
    }
  }

  onClearFilters() {
    this.searchRequest = {
      page: 0,
      size: 12,
      sortBy: "createdAt",
      sortDirection: "DESC",
    }
    this.onSearch()
  }

  onPageChange(page: number) {
    this.searchRequest.page = page
    this.onSearch()
  }

  onViewPart(part: AutoPart) {
    // Navigate to part details page (to be implemented)
    console.log("[v0] Viewing part:", part.title)
  }

  onContactSeller(part: AutoPart) {
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
