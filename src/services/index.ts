// Export all services for easy importing
import { AuthService } from "./auth.service"
import { AutoPartService } from "./autopart.service"
import { OrderService } from "./order.service"
import { MessageService } from "./message.service"
import { FavoriteService } from "./favorite.service"

// Export configuration
export { API_CONFIG, API_ENDPOINTS } from "./api.config"

// Export models
export * from "../models"

// Service instances for easy access
export const authService = AuthService.getInstance()
export const autoPartService = AutoPartService.getInstance()
export const orderService = OrderService.getInstance()
export const messageService = MessageService.getInstance()
export const favoriteService = FavoriteService.getInstance()
