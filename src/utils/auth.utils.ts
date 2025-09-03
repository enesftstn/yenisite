import { authService } from "../services"
import type { User } from "../services"

export class AuthUtils {
  static isAuthenticated(): boolean {
    return authService.isAuthenticated()
  }

  static getCurrentUser(): User | null {
    return authService.getUser()
  }

  static hasRole(role: string): boolean {
    const user = authService.getUser()
    return user?.role === role
  }

  static canAccessSellerFeatures(): boolean {
    const user = authService.getUser()
    return user?.role === "SELLER" || user?.role === "ADMIN"
  }

  static canAccessAdminFeatures(): boolean {
    const user = authService.getUser()
    return user?.role === "ADMIN"
  }

  static getUserDisplayName(): string {
    const user = authService.getUser()
    if (!user) return "Guest"
    return `${user.firstName} ${user.lastName}`
  }

  static async ensureAuthenticated(): Promise<boolean> {
    if (!authService.isAuthenticated()) {
      return false
    }

    try {
      // Verify token is still valid by fetching profile
      await authService.getProfile()
      return true
    } catch (error) {
      // Token expired or invalid
      authService.logout()
      return false
    }
  }
}
