import { HttpService } from "./http.service"
import { API_ENDPOINTS } from "./api.config"
import type { LoginRequest, RegisterRequest, AuthResponse, User, ApiResponse } from "../models"

export class SecureAuthService {
  private static instance: SecureAuthService
  private httpService: HttpService
  private user: User | null = null
  private tokenRefreshPromise: Promise<AuthResponse> | null = null

  private constructor() {
    this.httpService = HttpService.getInstance()
    this.initializeAuth()
  }

  public static getInstance(): SecureAuthService {
    if (!SecureAuthService.instance) {
      SecureAuthService.instance = new SecureAuthService()
    }
    return SecureAuthService.instance
  }

  private async initializeAuth(): Promise<void> {
    try {
      await this.getProfile()
    } catch (error) {
      // No valid session, user needs to login
      this.user = null
    }
  }

  async login(credentials: LoginRequest): Promise<AuthResponse> {
    const response = await this.httpService.post<ApiResponse<AuthResponse>>(
      API_ENDPOINTS.AUTH.LOGIN,
      {
        ...credentials,
        _token: this.getCSRFToken(),
      },
      false,
    )

    if (response.success && response.data) {
      this.user = response.data.user
      // No localStorage usage for security
      return response.data
    }

    throw new Error(response.message || "Login failed")
  }

  async register(userData: RegisterRequest): Promise<AuthResponse> {
    const response = await this.httpService.post<ApiResponse<AuthResponse>>(
      API_ENDPOINTS.AUTH.REGISTER,
      {
        ...userData,
        _token: this.getCSRFToken(),
      },
      false,
    )

    if (response.success && response.data) {
      this.user = response.data.user
      return response.data
    }

    throw new Error(response.message || "Registration failed")
  }

  async refreshToken(): Promise<AuthResponse> {
    if (this.tokenRefreshPromise) {
      return this.tokenRefreshPromise
    }

    this.tokenRefreshPromise = this.performTokenRefresh()

    try {
      const result = await this.tokenRefreshPromise
      return result
    } finally {
      this.tokenRefreshPromise = null
    }
  }

  private async performTokenRefresh(): Promise<AuthResponse> {
    const response = await this.httpService.post<ApiResponse<AuthResponse>>(
      API_ENDPOINTS.AUTH.REFRESH,
      { _token: this.getCSRFToken() },
      false,
    )

    if (response.success && response.data) {
      this.user = response.data.user
      return response.data
    }

    throw new Error(response.message || "Token refresh failed")
  }

  async getProfile(): Promise<User> {
    const response = await this.httpService.get<ApiResponse<User>>(API_ENDPOINTS.AUTH.PROFILE)

    if (response.success && response.data) {
      this.user = response.data
      return response.data
    }

    throw new Error(response.message || "Failed to get profile")
  }

  async updateProfile(userData: Partial<User>): Promise<User> {
    const response = await this.httpService.put<ApiResponse<User>>(API_ENDPOINTS.AUTH.PROFILE, {
      ...userData,
      _token: this.getCSRFToken(),
    })

    if (response.success && response.data) {
      this.user = response.data
      return response.data
    }

    throw new Error(response.message || "Failed to update profile")
  }

  async logout(): Promise<void> {
    try {
      await this.httpService.post<ApiResponse<void>>(API_ENDPOINTS.AUTH.LOGOUT, { _token: this.getCSRFToken() })
    } catch (error) {
      console.error("Logout request failed:", error)
    } finally {
      this.user = null
      // Cookies are cleared by backend
    }
  }

  isAuthenticated(): boolean {
    return this.user !== null
  }

  getUser(): User | null {
    return this.user
  }

  private getCSRFToken(): string {
    if (typeof document !== "undefined") {
      const metaToken = document.querySelector('meta[name="csrf-token"]')?.getAttribute("content")
      if (metaToken) return metaToken

      // Fallback to cookie
      const cookies = document.cookie.split(";")
      const csrfCookie = cookies.find((cookie) => cookie.trim().startsWith("XSRF-TOKEN="))
      return csrfCookie ? decodeURIComponent(csrfCookie.split("=")[1]) : ""
    }
    return ""
  }

  async withRetry<T>(operation: () => Promise<T>, maxRetries = 1): Promise<T> {
    let lastError: Error

    for (let attempt = 0; attempt <= maxRetries; attempt++) {
      try {
        return await operation()
      } catch (error) {
        lastError = error as Error

        // If it's an auth error and we haven't exceeded retries, try refreshing token
        if (attempt < maxRetries && this.isAuthError(error)) {
          try {
            await this.refreshToken()
            continue
          } catch (refreshError) {
            // Refresh failed, logout user
            await this.logout()
            throw refreshError
          }
        }
      }
    }

    throw lastError!
  }

  private isAuthError(error: any): boolean {
    return error?.status === 401 || error?.message?.includes("unauthorized")
  }
}
