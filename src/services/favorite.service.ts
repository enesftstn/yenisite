import { HttpService } from "./http.service"
import { API_ENDPOINTS } from "./api.config"
import type { Favorite, ApiResponse } from "../models"

export class FavoriteService {
  private static instance: FavoriteService
  private httpService: HttpService

  private constructor() {
    this.httpService = HttpService.getInstance()
  }

  public static getInstance(): FavoriteService {
    if (!FavoriteService.instance) {
      FavoriteService.instance = new FavoriteService()
    }
    return FavoriteService.instance
  }

  async getUserFavorites(userId: number): Promise<Favorite[]> {
    const response = await this.httpService.get<ApiResponse<Favorite[]>>(API_ENDPOINTS.FAVORITES.BY_USER(userId))

    if (response.success && response.data) {
      return response.data
    }

    throw new Error(response.message || "Failed to get favorites")
  }

  async toggleFavorite(partId: number): Promise<{ isFavorite: boolean }> {
    const response = await this.httpService.post<ApiResponse<{ isFavorite: boolean }>>(API_ENDPOINTS.FAVORITES.TOGGLE, {
      partId,
    })

    if (response.success && response.data) {
      return response.data
    }

    throw new Error(response.message || "Failed to toggle favorite")
  }

  async removeFavorite(favoriteId: number): Promise<void> {
    const response = await this.httpService.delete<ApiResponse<void>>(`${API_ENDPOINTS.FAVORITES.BASE}/${favoriteId}`)

    if (!response.success) {
      throw new Error(response.message || "Failed to remove favorite")
    }
  }
}
