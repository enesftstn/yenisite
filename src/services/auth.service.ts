import { HttpService } from "./http.service"
import { API_ENDPOINTS } from "./api.config"
import type { LoginRequest, RegisterRequest, AuthResponse, User, ApiResponse } from "../models"

export class AuthService {
  private static instance: AuthService
  private httpService: HttpService

  private constructor() {
    this.httpService = HttpService.getInstance()
  }

  public static getInstance(): AuthService {
    if (!AuthService.instance) {
      AuthService.instance = new AuthService()
    }
    return AuthService.instance
  }

  async login(credentials: LoginRequest): Promise<AuthResponse> {
    const response = await this.httpService.post<ApiResponse<AuthResponse>>(
      API_ENDPOINTS.AUTH.LOGIN,
      credentials,
      false,
    )

    if (response.success && response.data) {
      this.setAuthToken(response.data.token)
      this.setRefreshToken(response.data.refreshToken)
      this.setUser(response.data.user)
      return response.data
    }

    throw new Error(response.message || "Login failed")
  }

  async register(userData: RegisterRequest): Promise<AuthResponse> {
    const response = await this.httpService.post<ApiResponse<AuthResponse>>(
      API_ENDPOINTS.AUTH.REGISTER,
      userData,
      false,
    )

    if (response.success && response.data) {
      this.setAuthToken(response.data.token)
      this.setRefreshToken(response.data.refreshToken)
      this.setUser(response.data.user)
      return response.data
    }

    throw new Error(response.message || "Registration failed")
  }

  async refreshToken(): Promise<AuthResponse> {
    const refreshToken = this.getRefreshToken()
    if (!refreshToken) {
      throw new Error("No refresh token available")
    }

    const response = await this.httpService.post<ApiResponse<AuthResponse>>(
      API_ENDPOINTS.AUTH.REFRESH,
      { refreshToken },
      false,
    )

    if (response.success && response.data) {
      this.setAuthToken(response.data.token)
      this.setRefreshToken(response.data.refreshToken)
      return response.data
    }

    throw new Error(response.message || "Token refresh failed")
  }

  async getProfile(): Promise<User> {
    const response = await this.httpService.get<ApiResponse<User>>(API_ENDPOINTS.AUTH.PROFILE)

    if (response.success && response.data) {
      this.setUser(response.data)
      return response.data
    }

    throw new Error(response.message || "Failed to get profile")
  }

  async updateProfile(userData: Partial<User>): Promise<User> {
    const response = await this.httpService.put<ApiResponse<User>>(API_ENDPOINTS.AUTH.PROFILE, userData)

    if (response.success && response.data) {
      this.setUser(response.data)
      return response.data
    }

    throw new Error(response.message || "Failed to update profile")
  }

  async verifyEmail(token: string): Promise<void> {
    const response = await this.httpService.post<ApiResponse<void>>(API_ENDPOINTS.AUTH.VERIFY_EMAIL, { token }, false)

    if (!response.success) {
      throw new Error(response.message || "Email verification failed")
    }
  }

  async forgotPassword(email: string): Promise<void> {
    const response = await this.httpService.post<ApiResponse<void>>(
      API_ENDPOINTS.AUTH.FORGOT_PASSWORD,
      { email },
      false,
    )

    if (!response.success) {
      throw new Error(response.message || "Password reset request failed")
    }
  }

  async resetPassword(token: string, newPassword: string): Promise<void> {
    const response = await this.httpService.post<ApiResponse<void>>(
      API_ENDPOINTS.AUTH.RESET_PASSWORD,
      { token, newPassword },
      false,
    )

    if (!response.success) {
      throw new Error(response.message || "Password reset failed")
    }
  }

  logout(): void {
    if (typeof window !== "undefined") {
      localStorage.removeItem("auth_token")
      localStorage.removeItem("refresh_token")
      localStorage.removeItem("user_data")
    }
  }

  isAuthenticated(): boolean {
    return !!this.getAuthToken()
  }

  getAuthToken(): string | null {
    if (typeof window !== "undefined") {
      return localStorage.getItem("auth_token")
    }
    return null
  }

  getUser(): User | null {
    if (typeof window !== "undefined") {
      const userData = localStorage.getItem("user_data")
      return userData ? JSON.parse(userData) : null
    }
    return null
  }

  private setAuthToken(token: string): void {
    if (typeof window !== "undefined") {
      localStorage.setItem("auth_token", token)
    }
  }

  private setRefreshToken(token: string): void {
    if (typeof window !== "undefined") {
      localStorage.setItem("refresh_token", token)
    }
  }

  private getRefreshToken(): string | null {
    if (typeof window !== "undefined") {
      return localStorage.getItem("refresh_token")
    }
    return null
  }

  private setUser(user: User): void {
    if (typeof window !== "undefined") {
      localStorage.setItem("user_data", JSON.stringify(user))
    }
  }
}
