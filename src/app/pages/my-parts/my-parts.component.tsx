import { Component, type OnInit } from "@angular/core"
import { CommonModule } from "@angular/common"
import { FormsModule } from "@angular/forms"
import type { Router } from "@angular/router"
import { autoPartService, authService, type AutoPart, type AutoPartRequest, type User } from "../../../services"

@Component({
  selector: "app-my-parts",
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="page-container">
      <div class="container">
        <div class="page-header">
          <h1>📦 My Parts</h1>
          <p>Manage your listed auto parts and inventory.</p>
          <button class="btn btn-primary" (click)="onAddNewPart()">
            + Add New Part
          </button>
        </div>
        
        <!-- Added authentication check -->
        <div *ngIf="!isAuthenticated" class="auth-required">
          <div class="card">
            <h3>Authentication Required</h3>
            <p>Please log in to manage your parts.</p>
            <button class="btn btn-primary" (click)="onLogin()">
              Log In
            </button>
          </div>
        </div>

        <div class="my-parts-content" *ngIf="isAuthenticated">
          <!-- Added real parts management -->
          <div class="parts-section">
            <div class="section-header">
              <h3>Listed Parts ({{ myParts.length }})</h3>
              <div class="section-controls">
                <select [(ngModel)]="filterStatus" (change)="onFilterChange()" class="filter-select">
                  <option value="">All Parts</option>
                  <option value="available">Available</option>
                  <option value="sold">Sold</option>
                  <option value="draft">Draft</option>
                </select>
              </div>
            </div>

            <div *ngIf="isLoading" class="loading-message">
              Loading your parts...
            </div>

            <div *ngIf="error" class="error-message">
              {{ error }}
            </div>

            <div *ngIf="!isLoading && !error && myParts.length === 0" class="no-parts">
              <div class="card">
                <h4>No parts listed yet</h4>
                <p>Start by adding your first auto part for sale.</p>
                <button class="btn btn-primary" (click)="onAddNewPart()">
                  + Add Your First Part
                </button>
              </div>
            </div>

            <div class="parts-grid" *ngIf="!isLoading && !error && myParts.length > 0">
              <div class="part-card card" *ngFor="let part of myParts">
                <div class="part-image">
                  <img [src]="getPartImage(part)" [alt]="part.title" />
                  <div class="status-badge" [class]="getStatusClass(part)">
                    {{ getPartStatus(part) }}
                  </div>
                </div>
                
                <div class="part-info">
                  <h4>{{ part.title }}</h4>
                  <p class="part-details">{{ part.brand }} {{ part.model }} ({{ getYearRange(part) }})</p>
                  <p class="part-description">{{ part.description | slice:0:80 }}{{ part.description && part.description.length > 80 ? '...' : '' }}</p>
                  
                  <div class="part-stats">
                    <div class="stat">
                      <span class="stat-label">Price:</span>
                      <span class="stat-value price">\${{ part.price }}</span>
                    </div>
                    <div class="stat">
                      <span class="stat-label">Views:</span>
                      <span class="stat-value">{{ part.viewCount }}</span>
                    </div>
                    <div class="stat">
                      <span class="stat-label">Quantity:</span>
                      <span class="stat-value">{{ part.quantity }}</span>
                    </div>
                  </div>
                  
                  <div class="part-actions">
                    <button class="btn btn-secondary" (click)="onEditPart(part)">
                      Edit
                    </button>
                    <button class="btn btn-secondary" (click)="onViewPart(part)">
                      View
                    </button>
                    <button class="btn btn-danger" (click)="onDeletePart(part)">
                      Delete
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Added part form modal (basic) -->
        <div class="modal" *ngIf="showPartForm" (click)="onCloseForm()">
          <div class="modal-content" (click)="$event.stopPropagation()">
            <div class="modal-header">
              <h3>{{ editingPart ? 'Edit Part' : 'Add New Part' }}</h3>
              <button class="close-btn" (click)="onCloseForm()">×</button>
            </div>
            
            <form class="part-form" (ngSubmit)="onSubmitPart()" #partForm="ngForm">
              <div class="form-group">
                <label>Title *</label>
                <input 
                  type="text" 
                  [(ngModel)]="partFormData.title"
                  name="title"
                  required
                  class="form-input"
                  placeholder="e.g., BMW E46 Brake Pads"
                >
              </div>

              <div class="form-row">
                <div class="form-group">
                  <label>Brand</label>
                  <input 
                    type="text" 
                    [(ngModel)]="partFormData.brand"
                    name="brand"
                    class="form-input"
                    placeholder="e.g., BMW"
                  >
                </div>
                <div class="form-group">
                  <label>Model</label>
                  <input 
                    type="text" 
                    [(ngModel)]="partFormData.model"
                    name="model"
                    class="form-input"
                    placeholder="e.g., 3 Series"
                  >
                </div>
              </div>

              <div class="form-row">
                <div class="form-group">
                  <label>Year Start</label>
                  <input 
                    type="number" 
                    [(ngModel)]="partFormData.yearStart"
                    name="yearStart"
                    class="form-input"
                    placeholder="e.g., 1999"
                  >
                </div>
                <div class="form-group">
                  <label>Year End</label>
                  <input 
                    type="number" 
                    [(ngModel)]="partFormData.yearEnd"
                    name="yearEnd"
                    class="form-input"
                    placeholder="e.g., 2006"
                  >
                </div>
              </div>

              <div class="form-row">
                <div class="form-group">
                  <label>Category</label>
                  <select [(ngModel)]="partFormData.category" name="category" class="form-input">
                    <option value="">Select Category</option>
                    <option value="Brakes">Brakes</option>
                    <option value="Engine">Engine</option>
                    <option value="Lighting">Lighting</option>
                    <option value="Body">Body</option>
                    <option value="Exhaust">Exhaust</option>
                    <option value="Suspension">Suspension</option>
                  </select>
                </div>
                <div class="form-group">
                  <label>Condition *</label>
                  <select [(ngModel)]="partFormData.conditionType" name="conditionType" required class="form-input">
                    <option value="NEW">New</option>
                    <option value="USED">Used</option>
                    <option value="REFURBISHED">Refurbished</option>
                  </select>
                </div>
              </div>

              <div class="form-row">
                <div class="form-group">
                  <label>Price *</label>
                  <input 
                    type="number" 
                    [(ngModel)]="partFormData.price"
                    name="price"
                    required
                    step="0.01"
                    class="form-input"
                    placeholder="0.00"
                  >
                </div>
                <div class="form-group">
                  <label>Quantity *</label>
                  <input 
                    type="number" 
                    [(ngModel)]="partFormData.quantity"
                    name="quantity"
                    required
                    min="1"
                    class="form-input"
                    placeholder="1"
                  >
                </div>
              </div>

              <div class="form-group">
                <label>Description</label>
                <textarea 
                  [(ngModel)]="partFormData.description"
                  name="description"
                  class="form-textarea"
                  rows="4"
                  placeholder="Describe the part condition, compatibility, etc."
                ></textarea>
              </div>

              <div class="form-actions">
                <button type="button" class="btn btn-secondary" (click)="onCloseForm()">
                  Cancel
                </button>
                <button type="submit" class="btn btn-primary" [disabled]="!partForm.valid || isSubmitting">
                  {{ isSubmitting ? 'Saving...' : (editingPart ? 'Update Part' : 'Add Part') }}
                </button>
              </div>
            </form>
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

    .page-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 2rem;
    }

    .page-header h1 {
      margin: 0;
      color: var(--dark-gray);
    }

    .page-header p {
      margin: 0.5rem 0 0 0;
      color: #666;
    }
    
    .my-parts-content {
      margin-top: 2rem;
    }

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 1rem;
    }

    .filter-select {
      padding: 0.5rem;
      border: 1px solid #ddd;
      border-radius: 4px;
    }

    .parts-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
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

    .status-badge {
      position: absolute;
      top: 0.5rem;
      right: 0.5rem;
      padding: 0.25rem 0.5rem;
      border-radius: 4px;
      font-size: 0.75rem;
      font-weight: 600;
      text-transform: uppercase;
    }

    .status-badge.available {
      background-color: #28a745;
      color: white;
    }

    .status-badge.sold {
      background-color: #dc3545;
      color: white;
    }

    .status-badge.draft {
      background-color: #6c757d;
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

    .part-stats {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 0.5rem;
      margin: 1rem 0;
      padding: 0.5rem;
      background-color: #f8f9fa;
      border-radius: 4px;
    }

    .stat {
      text-align: center;
    }

    .stat-label {
      display: block;
      font-size: 0.75rem;
      color: #666;
      margin-bottom: 0.25rem;
    }

    .stat-value {
      display: block;
      font-weight: 600;
    }

    .stat-value.price {
      color: #28a745;
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

    .modal {
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background-color: rgba(0, 0, 0, 0.5);
      display: flex;
      justify-content: center;
      align-items: center;
      z-index: 1000;
    }

    .modal-content {
      background: white;
      border-radius: 8px;
      width: 90%;
      max-width: 600px;
      max-height: 90vh;
      overflow-y: auto;
    }

    .modal-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 1rem;
      border-bottom: 1px solid #ddd;
    }

    .close-btn {
      background: none;
      border: none;
      font-size: 1.5rem;
      cursor: pointer;
      padding: 0;
      width: 30px;
      height: 30px;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .part-form {
      padding: 1rem;
    }

    .form-group {
      margin-bottom: 1rem;
    }

    .form-row {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 1rem;
    }

    .form-group label {
      display: block;
      margin-bottom: 0.5rem;
      font-weight: 600;
    }

    .form-input, .form-textarea {
      width: 100%;
      padding: 0.5rem;
      border: 1px solid #ddd;
      border-radius: 4px;
      font-size: 0.9rem;
    }

    .form-textarea {
      resize: vertical;
    }

    .form-actions {
      display: flex;
      gap: 1rem;
      justify-content: flex-end;
      margin-top: 1.5rem;
      padding-top: 1rem;
      border-top: 1px solid #ddd;
    }

    .loading-message, .error-message, .no-parts {
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

    .auth-required {
      text-align: center;
      margin-top: 2rem;
    }

    @media (max-width: 768px) {
      .page-header {
        flex-direction: column;
        align-items: flex-start;
        gap: 1rem;
      }

      .section-header {
        flex-direction: column;
        align-items: flex-start;
        gap: 1rem;
      }

      .parts-grid {
        grid-template-columns: 1fr;
      }

      .form-row {
        grid-template-columns: 1fr;
      }
    }
  `,
  ],
})
export class MyPartsComponent implements OnInit {
  myParts: AutoPart[] = []
  isLoading = false
  error: string | null = null
  filterStatus = ""

  // Form state
  showPartForm = false
  editingPart: AutoPart | null = null
  isSubmitting = false
  partFormData: AutoPartRequest = this.getEmptyPartForm()

  // User state
  currentUser: User | null = null
  isAuthenticated = false

  constructor(private router: Router) {}

  async ngOnInit() {
    this.checkAuthentication()
    if (this.isAuthenticated) {
      await this.loadMyParts()
    }
  }

  checkAuthentication() {
    this.isAuthenticated = authService.isAuthenticated()
    if (this.isAuthenticated) {
      this.currentUser = authService.getUser()
    }
  }

  async loadMyParts() {
    if (!this.currentUser) return

    try {
      this.isLoading = true
      this.error = null

      const response = await autoPartService.getPartsBySeller(this.currentUser.id)
      this.myParts = response.content
    } catch (error) {
      console.error("[v0] Error loading my parts:", error)
      this.error = "Failed to load your parts. Please try again later."
    } finally {
      this.isLoading = false
    }
  }

  onFilterChange() {
    // Filter parts based on status (client-side for now)
    // In a real app, this would be handled by the API
  }

  onAddNewPart() {
    this.editingPart = null
    this.partFormData = this.getEmptyPartForm()
    this.showPartForm = true
  }

  onEditPart(part: AutoPart) {
    this.editingPart = part
    this.partFormData = {
      title: part.title,
      description: part.description || "",
      brand: part.brand || "",
      model: part.model || "",
      yearStart: part.yearStart,
      yearEnd: part.yearEnd,
      partNumber: part.partNumber || "",
      category: part.category || "",
      subcategory: part.subcategory || "",
      conditionType: part.conditionType,
      price: part.price,
      originalPrice: part.originalPrice,
      quantity: part.quantity,
      shippingCost: part.shippingCost,
      returnPolicy: part.returnPolicy || "",
      warrantyInfo: part.warrantyInfo || "",
    }
    this.showPartForm = true
  }

  async onDeletePart(part: AutoPart) {
    if (!confirm(`Are you sure you want to delete "${part.title}"?`)) {
      return
    }

    try {
      await autoPartService.deletePart(part.id)
      await this.loadMyParts() // Reload the list
    } catch (error) {
      console.error("[v0] Error deleting part:", error)
      alert("Failed to delete part. Please try again.")
    }
  }

  onViewPart(part: AutoPart) {
    // Navigate to part details page (to be implemented)
    console.log("[v0] Viewing part:", part.title)
  }

  async onSubmitPart() {
    if (!this.currentUser) return

    try {
      this.isSubmitting = true

      if (this.editingPart) {
        await autoPartService.updatePart(this.editingPart.id, this.partFormData)
      } else {
        await autoPartService.createPart(this.partFormData)
      }

      this.showPartForm = false
      await this.loadMyParts() // Reload the list
    } catch (error) {
      console.error("[v0] Error saving part:", error)
      alert("Failed to save part. Please try again.")
    } finally {
      this.isSubmitting = false
    }
  }

  onCloseForm() {
    this.showPartForm = false
    this.editingPart = null
    this.partFormData = this.getEmptyPartForm()
  }

  onLogin() {
    this.router.navigate(["/login"])
  }

  getEmptyPartForm(): AutoPartRequest {
    return {
      title: "",
      description: "",
      brand: "",
      model: "",
      yearStart: undefined,
      yearEnd: undefined,
      partNumber: "",
      category: "",
      subcategory: "",
      conditionType: "USED",
      price: 0,
      originalPrice: undefined,
      quantity: 1,
      shippingCost: 0,
      returnPolicy: "",
      warrantyInfo: "",
    }
  }

  getPartImage(part: AutoPart): string {
    if (part.images && part.images.length > 0) {
      return part.images[0]
    }
    return `/placeholder.svg?height=200&width=350&query=${encodeURIComponent(part.title)}`
  }

  getYearRange(part: AutoPart): string {
    if (part.yearStart && part.yearEnd) {
      return part.yearStart === part.yearEnd ? part.yearStart.toString() : `${part.yearStart}-${part.yearEnd}`
    }
    return "Universal"
  }

  getPartStatus(part: AutoPart): string {
    if (!part.isAvailable) return "Sold"
    if (part.quantity === 0) return "Out of Stock"
    return "Available"
  }

  getStatusClass(part: AutoPart): string {
    if (!part.isAvailable) return "sold"
    if (part.quantity === 0) return "sold"
    return "available"
  }
}
