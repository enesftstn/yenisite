import { Component } from "@angular/core"
import { CommonModule } from "@angular/common"
import { FormsModule } from "@angular/forms"

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
  imports: [CommonModule, FormsModule],
  templateUrl: "./app.component.html",
  styleUrl: "./app.component.css",
})
export class AppComponent {
  title = "Auto Parts Exchange"
  searchQuery = ""

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
  ]

  onSearch() {
    console.log("Searching for:", this.searchQuery)
    // TODO: Implement search functionality
  }

  onListPart() {
    console.log("Opening list part form")
    // TODO: Implement list part functionality
  }

  onContactSeller(part: AutoPart) {
    console.log("Contacting seller for:", part.name)
    // TODO: Implement contact seller functionality
  }
}
