// User models
export interface User {
  id: number
  email: string
  firstName: string
  lastName: string
  phone?: string
  address?: string
  city?: string
  state?: string
  zipCode?: string
  country?: string
  role: "USER" | "ADMIN" | "SELLER"
  isVerified: boolean
  createdAt: string
  updatedAt: string
}

export interface LoginRequest {
  email: string
  password: string
}

export interface RegisterRequest {
  email: string
  password: string
  firstName: string
  lastName: string
  phone?: string
  address?: string
  city?: string
  state?: string
  zipCode?: string
}

export interface AuthResponse {
  token: string
  refreshToken: string
  user: User
  expiresIn: number
}

// Auto Part models
export interface AutoPart {
  id: number
  sellerId: number
  seller?: User
  title: string
  description?: string
  brand?: string
  model?: string
  yearStart?: number
  yearEnd?: number
  partNumber?: string
  category?: string
  subcategory?: string
  conditionType: "NEW" | "USED" | "REFURBISHED"
  price: number
  originalPrice?: number
  quantity: number
  isAvailable: boolean
  isFeatured: boolean
  viewCount: number
  images?: string[]
  specifications?: Record<string, any>
  compatibility?: Record<string, any>
  shippingCost: number
  returnPolicy?: string
  warrantyInfo?: string
  createdAt: string
  updatedAt: string
}

export interface AutoPartRequest {
  title: string
  description?: string
  brand?: string
  model?: string
  yearStart?: number
  yearEnd?: number
  partNumber?: string
  category?: string
  subcategory?: string
  conditionType: "NEW" | "USED" | "REFURBISHED"
  price: number
  originalPrice?: number
  quantity: number
  specifications?: Record<string, any>
  compatibility?: Record<string, any>
  shippingCost?: number
  returnPolicy?: string
  warrantyInfo?: string
}

export interface AutoPartSearchRequest {
  query?: string
  brand?: string
  model?: string
  yearStart?: number
  yearEnd?: number
  category?: string
  conditionType?: "NEW" | "USED" | "REFURBISHED"
  minPrice?: number
  maxPrice?: number
  location?: string
  radius?: number
  page?: number
  size?: number
  sortBy?: string
  sortDirection?: "ASC" | "DESC"
}

// Order models
export interface Order {
  id: number
  buyerId: number
  sellerId: number
  buyer?: User
  seller?: User
  orderNumber: string
  status: "PENDING" | "CONFIRMED" | "SHIPPED" | "DELIVERED" | "CANCELLED" | "REFUNDED"
  totalAmount: number
  shippingCost: number
  taxAmount: number
  shippingAddress: Address
  billingAddress?: Address
  trackingNumber?: string
  notes?: string
  estimatedDelivery?: string
  deliveredAt?: string
  items: OrderItem[]
  createdAt: string
  updatedAt: string
}

export interface OrderItem {
  id: number
  orderId: number
  partId: number
  part?: AutoPart
  quantity: number
  unitPrice: number
  totalPrice: number
}

export interface Address {
  street: string
  city: string
  state: string
  zipCode: string
  country: string
}

export interface CreateOrderRequest {
  items: Array<{
    partId: number
    quantity: number
  }>
  shippingAddress: Address
  billingAddress?: Address
  notes?: string
}

// Message models
export interface Conversation {
  id: number
  participant1Id: number
  participant2Id: number
  participant1?: User
  participant2?: User
  partId?: number
  part?: AutoPart
  lastMessageAt: string
  unreadCount: number
  createdAt: string
}

export interface Message {
  id: number
  conversationId: number
  senderId: number
  sender?: User
  content: string
  messageType: "TEXT" | "IMAGE" | "FILE"
  attachmentUrl?: string
  isRead: boolean
  readAt?: string
  createdAt: string
}

export interface MessageRequest {
  conversationId: number
  content: string
  messageType?: "TEXT" | "IMAGE" | "FILE"
  attachmentUrl?: string
}

// Other models
export interface Favorite {
  id: number
  userId: number
  partId: number
  part?: AutoPart
  createdAt: string
}

export interface Review {
  id: number
  reviewerId: number
  reviewedUserId: number
  reviewer?: User
  reviewedUser?: User
  orderId?: number
  partId?: number
  part?: AutoPart
  rating: number
  title?: string
  comment?: string
  isVerifiedPurchase: boolean
  createdAt: string
  updatedAt: string
}

export interface ApiResponse<T> {
  success: boolean
  message: string
  data: T
  timestamp: string
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
}
