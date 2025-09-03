import { HttpService } from "./http.service"
import { API_ENDPOINTS } from "./api.config"
import type { Conversation, Message, MessageRequest, ApiResponse, PageResponse } from "../models"

export class MessageService {
  private static instance: MessageService
  private httpService: HttpService
  private websocket: WebSocket | null = null

  private constructor() {
    this.httpService = HttpService.getInstance()
  }

  public static getInstance(): MessageService {
    if (!MessageService.instance) {
      MessageService.instance = new MessageService()
    }
    return MessageService.instance
  }

  async getConversations(): Promise<Conversation[]> {
    const response = await this.httpService.get<ApiResponse<Conversation[]>>(API_ENDPOINTS.MESSAGES.CONVERSATIONS)

    if (response.success && response.data) {
      return response.data
    }

    throw new Error(response.message || "Failed to get conversations")
  }

  async getConversationMessages(conversationId: number, page = 0, size = 50): Promise<PageResponse<Message>> {
    const response = await this.httpService.get<ApiResponse<PageResponse<Message>>>(
      `${API_ENDPOINTS.MESSAGES.BY_CONVERSATION(conversationId)}?page=${page}&size=${size}`,
    )

    if (response.success && response.data) {
      return response.data
    }

    throw new Error(response.message || "Failed to get messages")
  }

  async sendMessage(messageData: MessageRequest): Promise<Message> {
    const response = await this.httpService.post<ApiResponse<Message>>(API_ENDPOINTS.MESSAGES.SEND, messageData)

    if (response.success && response.data) {
      return response.data
    }

    throw new Error(response.message || "Failed to send message")
  }

  async markMessageAsRead(messageId: number): Promise<void> {
    const response = await this.httpService.put<ApiResponse<void>>(API_ENDPOINTS.MESSAGES.MARK_READ(messageId))

    if (!response.success) {
      throw new Error(response.message || "Failed to mark message as read")
    }
  }

  connectWebSocket(onMessage: (message: Message) => void, onError?: (error: Event) => void): void {
    const token = localStorage.getItem("auth_token")
    if (!token) {
      throw new Error("No authentication token available")
    }

    const wsUrl = `ws://localhost:8080/api/ws?token=${token}`
    this.websocket = new WebSocket(wsUrl)

    this.websocket.onopen = () => {
      console.log("[v0] WebSocket connected")
    }

    this.websocket.onmessage = (event) => {
      try {
        const message = JSON.parse(event.data)
        onMessage(message)
      } catch (error) {
        console.error("[v0] Failed to parse WebSocket message:", error)
      }
    }

    this.websocket.onerror = (error) => {
      console.error("[v0] WebSocket error:", error)
      if (onError) {
        onError(error)
      }
    }

    this.websocket.onclose = () => {
      console.log("[v0] WebSocket disconnected")
    }
  }

  disconnectWebSocket(): void {
    if (this.websocket) {
      this.websocket.close()
      this.websocket = null
    }
  }

  sendWebSocketMessage(message: any): void {
    if (this.websocket && this.websocket.readyState === WebSocket.OPEN) {
      this.websocket.send(JSON.stringify(message))
    } else {
      throw new Error("WebSocket is not connected")
    }
  }
}
