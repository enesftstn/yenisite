import { API_CONFIG } from "./api.config"

export class HttpService {
  private static instance: HttpService
  private baseURL: string

  private constructor() {
    this.baseURL = API_CONFIG.BASE_URL
  }

  public static getInstance(): HttpService {
    if (!HttpService.instance) {
      HttpService.instance = new HttpService()
    }
    return HttpService.instance
  }

  private getAuthToken(): string | null {
    if (typeof window !== "undefined") {
      return localStorage.getItem("auth_token")
    }
    return null
  }

  private getHeaders(includeAuth = true): HeadersInit {
    const headers: HeadersInit = {
      "Content-Type": "application/json",
    }

    if (includeAuth) {
      const token = this.getAuthToken()
      if (token) {
        headers["Authorization"] = `Bearer ${token}`
      }
    }

    return headers
  }

  private async handleResponse<T>(response: Response): Promise<T> {
    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}))
      throw new Error(errorData.message || `HTTP error! status: ${response.status}`)
    }

    const contentType = response.headers.get("content-type")
    if (contentType && contentType.includes("application/json")) {
      return response.json()
    }

    return response.text() as any
  }

  async get<T>(endpoint: string, includeAuth = true): Promise<T> {
    const response = await fetch(`${this.baseURL}${endpoint}`, {
      method: "GET",
      headers: this.getHeaders(includeAuth),
    })

    return this.handleResponse<T>(response)
  }

  async post<T>(endpoint: string, data?: any, includeAuth = true): Promise<T> {
    const response = await fetch(`${this.baseURL}${endpoint}`, {
      method: "POST",
      headers: this.getHeaders(includeAuth),
      body: data ? JSON.stringify(data) : undefined,
    })

    return this.handleResponse<T>(response)
  }

  async put<T>(endpoint: string, data?: any, includeAuth = true): Promise<T> {
    const response = await fetch(`${this.baseURL}${endpoint}`, {
      method: "PUT",
      headers: this.getHeaders(includeAuth),
      body: data ? JSON.stringify(data) : undefined,
    })

    return this.handleResponse<T>(response)
  }

  async delete<T>(endpoint: string, includeAuth = true): Promise<T> {
    const response = await fetch(`${this.baseURL}${endpoint}`, {
      method: "DELETE",
      headers: this.getHeaders(includeAuth),
    })

    return this.handleResponse<T>(response)
  }

  async uploadFile<T>(endpoint: string, file: File, includeAuth = true): Promise<T> {
    const formData = new FormData()
    formData.append("file", file)

    const headers: HeadersInit = {}
    if (includeAuth) {
      const token = this.getAuthToken()
      if (token) {
        headers["Authorization"] = `Bearer ${token}`
      }
    }

    const response = await fetch(`${this.baseURL}${endpoint}`, {
      method: "POST",
      headers,
      body: formData,
    })

    return this.handleResponse<T>(response)
  }
}
