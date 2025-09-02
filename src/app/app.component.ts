import { Component } from "@angular/core"
import { CommonModule } from "@angular/common"
import { FormsModule } from "@angular/forms"
import { BrowseComponent } from "./pages/browse/browse.component"
import { FavoritesComponent } from "./pages/favorites/favorites.component"
import { MessagesComponent } from "./pages/messages/messages.component"
import { MyPartsComponent } from "./pages/my-parts/my-parts.component"
import { OrdersComponent } from "./pages/orders/orders.component"

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
    OrdersComponent
  ],
  templateUrl: "./app.component.html",
  styleUrl: "./app.component.css",
})
export class AppComponent {
  title = "Auto Parts Exchange"
  searchQuery = ""

  isSidebarOpen = false
  isSettingsOpen = false
  isLoginOpen = false
  isRegisterOpen = false
  
  currentRoute = "dashboard"

  loginData = { email: "", password: "" }
  registerData = { name: "", email: "", password: "", confirmPassword: "" }

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
      id: 5,
      name: "Xenon Headlights",
      brand: "Mercedes",
      model: "S-680",
      year: "2020",
      price: 900,
      condition: "Used - Good",
      location: "Turkey, Istanbul",
      image: "/honda-civic-headlights.png",
      seller: "Mercedes Benz Türk A.Ş.",
    },
  ]

  onSearch() {
    console.log("Searching for:", this.searchQuery)
    this.setActiveTab('browse')
  }

  onListPart() {
    console.log("Opening list part form")
    this.setActiveTab('my-parts')
  }

  onContactSeller(part: AutoPart) {
    console.log("Contacting seller for:", part.name)
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
  }

  openRegister() {
    this.isRegisterOpen = true
    this.isLoginOpen = false
    this.isSettingsOpen = false
  }

  closeModals() {
    this.isSettingsOpen = false
    this.isLoginOpen = false
    this.isRegisterOpen = false
  }

  setActiveTab(tab: string) {
    this.currentRoute = tab
    this.toggleSidebar()
  }

  onLogin() {
    console.log("Login attempt:", this.loginData)
    this.closeModals()
  }

  onRegister() {
    console.log("Register attempt:", this.registerData)
    this.closeModals()
  }
}