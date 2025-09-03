import { Component, type OnInit } from "@angular/core"
import { CommonModule } from "@angular/common"
import { FormsModule } from "@angular/forms"
import { BrowseComponent } from "./pages/browse/browse.component"
import { FavoritesComponent } from "./pages/favorites/favorites.component"
import { MessagesComponent } from "./pages/messages/messages.component"
import { MyPartsComponent } from "./pages/my-parts/my-parts.component"
import { OrdersComponent } from "./pages/orders/orders.component"
import { authService } from "../services"
import type { User, LoginRequest, RegisterRequest } from "../services"

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
  selector: "app-root",
  imports: [
    CommonModule,
    FormsModule,
    BrowseComponent,
    FavoritesComponent,
    MessagesComponent,
    MyPartsComponent,
    OrdersComponent,
  ],
  templateUrl: "./app.component.html",
  styleUrl: "./app.component.css",
})
export class AppComponent implements OnInit {
  title = "Auto Parts Exchange"
  searchQuery = ""

  isSidebarOpen = false
  isSettingsOpen = false
  isLoginOpen = false
  isRegisterOpen = false

  currentRoute = "dashboard"

  isAuthenticated = false
  currentUser: User | null = null
  isLoading = false
  authError = ""

  loginData: LoginRequest = { email: "", password: "" }
  registerData: RegisterRequest = {
    email: "",
    password: "",
    firstName: "",
    lastName: "",
    phone: "",
    address: "",
    city: "",
    state: "",
    zipCode: "",
  }
  confirmPassword = ""

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
    {
      id: 4,
      name: "Rear Door",
      brand: "Rolls Royce",
      model: "Flying Spur II",
      year: "2025",
      price: 4500,
      condition: "New",
      location: "Germany",
      image: "/rolls-royce-phantom-5120x2880-23523.jpg",
      seller: "Rolls Royce Deustchland",
    },
    {
      id: 5,
      name: "Baldwin's Helm",
      brand: "Crusader",
      model: "Kingdom of Heaven",
      year: "1174",
      price: 450000,
      condition: "Used - Good",
      location: "Jerusalem - Israel",
      image: "/motroc-tudor-render-2-artstation.jpg",
      seller: "Jews",
    },
    {
      id: 6,
      name: "Xenon Headlights",
      brand: "Mercedes",
      model: "S-680",
      year: "2020",
      price: 900,
      condition: "Like New",
      location: "Turkey, Istanbul",
      image: "/honda-civic-headlights.png",
      seller: "Mercedes Benz Türk A.Ş.",
    },
  ]

  ngOnInit() {
    this.checkAuthenticationStatus()
  }

  private checkAuthenticationStatus() {
    this.isAuthenticated = authService.isAuthenticated()
    this.currentUser = authService.getUser()

    // If user is authenticated, try to refresh profile
    if (this.isAuthenticated && this.currentUser) {
      this.loadUserProfile()
    }
  }

  private async loadUserProfile() {
    try {
      this.currentUser = await authService.getProfile()
    } catch (error) {
      console.error("[v0] Failed to load user profile:", error)
      // Token might be expired, logout user
      this.onLogout()
    }
  }

  onSearch() {
    console.log("Searching for:", this.searchQuery)
    this.setActiveTab("browse")
  }

  onListPart() {
    if (!this.isAuthenticated) {
      this.openLogin()
      return
    }
    console.log("Opening list part form")
    this.setActiveTab("my-parts")
  }

  onContactSeller(part: AutoPart) {
    if (!this.isAuthenticated) {
      this.openLogin()
      return
    }
    console.log("Contacting seller for:", part.name)
    this.setActiveTab("messages")
  }

  toggleSidebar() {
    this.isSidebarOpen = !this.isSidebarOpen
  }

  toggleSettings() {
    this.isSettingsOpen = !this.isSettingsOpen
  }

  openLogin() {
    this.isLoginOpen = true
    this.isRegisterOpen = false
    this.isSettingsOpen = false
    this.authError = ""
  }

  openRegister() {
    this.isRegisterOpen = true
    this.isLoginOpen = false
    this.isSettingsOpen = false
    this.authError = ""
  }

  closeModals() {
    this.isSettingsOpen = false
    this.isLoginOpen = false
    this.isRegisterOpen = false
    this.authError = ""
  }

  setActiveTab(tab: string) {
    const protectedRoutes = ["my-parts", "messages", "orders", "favorites"]

    if (protectedRoutes.includes(tab) && !this.isAuthenticated) {
      this.openLogin()
      return
    }

    this.currentRoute = tab
    this.toggleSidebar()
  }

  async onLogin() {
    if (!this.loginData.email || !this.loginData.password) {
      this.authError = "Please fill in all fields"
      return
    }

    this.isLoading = true
    this.authError = ""

    try {
      console.log("[v0] Attempting login for:", this.loginData.email)
      const authResponse = await authService.login(this.loginData)

      this.isAuthenticated = true
      this.currentUser = authResponse.user
      this.closeModals()

      // Reset form
      this.loginData = { email: "", password: "" }

      console.log("[v0] Login successful for user:", this.currentUser?.firstName)
    } catch (error: any) {
      console.error("[v0] Login failed:", error)
      this.authError = error.message || "Login failed. Please try again."
    } finally {
      this.isLoading = false
    }
  }

  async onRegister() {
    if (
      !this.registerData.email ||
      !this.registerData.password ||
      !this.registerData.firstName ||
      !this.registerData.lastName
    ) {
      this.authError = "Please fill in all required fields"
      return
    }

    if (this.registerData.password !== this.confirmPassword) {
      this.authError = "Passwords do not match"
      return
    }

    if (this.registerData.password.length < 6) {
      this.authError = "Password must be at least 6 characters long"
      return
    }

    this.isLoading = true
    this.authError = ""

    try {
      console.log("[v0] Attempting registration for:", this.registerData.email)
      const authResponse = await authService.register(this.registerData)

      this.isAuthenticated = true
      this.currentUser = authResponse.user
      this.closeModals()

      // Reset form
      this.registerData = {
        email: "",
        password: "",
        firstName: "",
        lastName: "",
        phone: "",
        address: "",
        city: "",
        state: "",
        zipCode: "",
      }
      this.confirmPassword = ""

      console.log("[v0] Registration successful for user:", this.currentUser?.firstName)
    } catch (error: any) {
      console.error("[v0] Registration failed:", error)
      this.authError = error.message || "Registration failed. Please try again."
    } finally {
      this.isLoading = false
    }
  }

  onLogout() {
    authService.logout()
    this.isAuthenticated = false
    this.currentUser = null
    this.currentRoute = "dashboard"
    this.closeModals()
    console.log("[v0] User logged out successfully")
  }

  async updateProfile() {
    if (!this.currentUser) return

    this.isLoading = true
    this.authError = ""

    try {
      const updatedUser = await authService.updateProfile(this.currentUser)
      this.currentUser = updatedUser
      this.closeModals()
      console.log("[v0] Profile updated successfully")
    } catch (error: any) {
      console.error("[v0] Profile update failed:", error)
      this.authError = error.message || "Failed to update profile"
    } finally {
      this.isLoading = false
    }
  }

  getUserDisplayName(): string {
    if (!this.currentUser) return "Guest"
    return `${this.currentUser.firstName} ${this.currentUser.lastName}`
  }

  isUserSeller(): boolean {
    return this.currentUser?.role === "SELLER" || this.currentUser?.role === "ADMIN"
  }
}
