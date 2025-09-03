// API configuration and base settings
export const API_CONFIG = {
  BASE_URL: process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api",
  TIMEOUT: 10000,
  RETRY_ATTEMPTS: 3,
}

export const API_ENDPOINTS = {
  // Authentication
  AUTH: {
    LOGIN: "/auth/login",
    REGISTER: "/auth/register",
    REFRESH: "/auth/refresh",
    VERIFY_EMAIL: "/auth/verify-email",
    FORGOT_PASSWORD: "/auth/forgot-password",
    RESET_PASSWORD: "/auth/reset-password",
    PROFILE: "/auth/profile",
  },
  // Auto Parts
  PARTS: {
    BASE: "/parts",
    SEARCH: "/parts/search",
    BY_ID: (id: number) => `/parts/${id}`,
    BY_SELLER: (sellerId: number) => `/parts/seller/${sellerId}`,
    UPLOAD_IMAGE: "/parts/upload-image",
  },
  // Orders
  ORDERS: {
    BASE: "/orders",
    BY_ID: (id: number) => `/orders/${id}`,
    BY_BUYER: (buyerId: number) => `/orders/buyer/${buyerId}`,
    BY_SELLER: (sellerId: number) => `/orders/seller/${sellerId}`,
    UPDATE_STATUS: (id: number) => `/orders/${id}/status`,
  },
  // Messages
  MESSAGES: {
    CONVERSATIONS: "/messages/conversations",
    BY_CONVERSATION: (id: number) => `/messages/conversations/${id}`,
    SEND: "/messages/send",
    MARK_READ: (id: number) => `/messages/${id}/read`,
  },
  // Favorites
  FAVORITES: {
    BASE: "/favorites",
    BY_USER: (userId: number) => `/favorites/user/${userId}`,
    TOGGLE: "/favorites/toggle",
  },
  // Reviews
  REVIEWS: {
    BASE: "/reviews",
    BY_USER: (userId: number) => `/reviews/user/${userId}`,
    BY_PART: (partId: number) => `/reviews/part/${partId}`,
  },
}
