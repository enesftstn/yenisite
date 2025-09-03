import { HttpService } from "./http.service"
import { API_ENDPOINTS } from "./api.config"
import type { AutoPart, AutoPartRequest, AutoPartSearchRequest, ApiResponse, PageResponse } from "../models"

export class AutoPartService {
  private static instance: AutoPartService
  private httpService: HttpService

  private constructor() {
    this.httpService = HttpService.getInstance()
  }

  public static getInstance(): AutoPartService {
    if (!AutoPartService.instance) {
      AutoPartService.instance = new AutoPartService()
    }
    return AutoPartService.instance
  }

  async searchParts(searchRequest: AutoPartSearchRequest): Promise<PageResponse<AutoPart>> {
    const queryParams = new URLSearchParams()

    Object.entries(searchRequest).forEach(([key, value]) => {
      if (value !== undefined && value !== null) {
        queryParams.append(key, value.toString())
      }
    })

    const response = await this.httpService.get<ApiResponse<PageResponse<AutoPart>>>(
      `${API_ENDPOINTS.PARTS.SEARCH}?${queryParams.toString()}`,
      false,
    )

    if (response.success && response.data) {
      return response.data
    }

    throw new Error(response.message || "Search failed")
  }

  async getAllParts(page = 0, size = 20): Promise<PageResponse<AutoPart>> {
    const response = await this.httpService.get<ApiResponse<PageResponse<AutoPart>>>(
      `${API_ENDPOINTS.PARTS.BASE}?page=${page}&size=${size}`,
      false,
    )

    if (response.success && response.data) {
      return response.data
    }

    throw new Error(response.message || "Failed to get parts")
  }

  async getPartById(id: number): Promise<AutoPart> {
    const response = await this.httpService.get<ApiResponse<AutoPart>>(API_ENDPOINTS.PARTS.BY_ID(id), false)

    if (response.success && response.data) {
      return response.data
    }

    throw new Error(response.message || "Part not found")
  }

  async getPartsBySeller(sellerId: number, page = 0, size = 20): Promise<PageResponse<AutoPart>> {
    const response = await this.httpService.get<ApiResponse<PageResponse<AutoPart>>>(
      `${API_ENDPOINTS.PARTS.BY_SELLER(sellerId)}?page=${page}&size=${size}`,
    )

    if (response.success && response.data) {
      return response.data
    }

    throw new Error(response.message || "Failed to get seller parts")
  }

  async createPart(partData: AutoPartRequest): Promise<AutoPart> {
    const response = await this.httpService.post<ApiResponse<AutoPart>>(API_ENDPOINTS.PARTS.BASE, partData)

    if (response.success && response.data) {
      return response.data
    }

    throw new Error(response.message || "Failed to create part")
  }

  async updatePart(id: number, partData: Partial<AutoPartRequest>): Promise<AutoPart> {
    const response = await this.httpService.put<ApiResponse<AutoPart>>(API_ENDPOINTS.PARTS.BY_ID(id), partData)

    if (response.success && response.data) {
      return response.data
    }

    throw new Error(response.message || "Failed to update part")
  }

  async deletePart(id: number): Promise<void> {
    const response = await this.httpService.delete<ApiResponse<void>>(API_ENDPOINTS.PARTS.BY_ID(id))

    if (!response.success) {
      throw new Error(response.message || "Failed to delete part")
    }
  }

  async uploadPartImage(partId: number, file: File): Promise<string> {
    const response = await this.httpService.uploadFile<ApiResponse<{ imageUrl: string }>>(
      `${API_ENDPOINTS.PARTS.UPLOAD_IMAGE}?partId=${partId}`,
      file,
    )

    if (response.success && response.data) {
      return response.data.imageUrl
    }

    throw new Error(response.message || "Failed to upload image")
  }
}
