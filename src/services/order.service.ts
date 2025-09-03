import { HttpService } from "./http.service"
import { API_ENDPOINTS } from "./api.config"
import type { Order, CreateOrderRequest, ApiResponse, PageResponse } from "../models"

export class OrderService {
  private static instance: OrderService
  private httpService: HttpService

  private constructor() {
    this.httpService = HttpService.getInstance()
  }

  public static getInstance(): OrderService {
    if (!OrderService.instance) {
      OrderService.instance = new OrderService()
    }
    return OrderService.instance
  }

  async createOrder(orderData: CreateOrderRequest): Promise<Order> {
    const response = await this.httpService.post<ApiResponse<Order>>(API_ENDPOINTS.ORDERS.BASE, orderData)

    if (response.success && response.data) {
      return response.data
    }

    throw new Error(response.message || "Failed to create order")
  }

  async getOrderById(id: number): Promise<Order> {
    const response = await this.httpService.get<ApiResponse<Order>>(API_ENDPOINTS.ORDERS.BY_ID(id))

    if (response.success && response.data) {
      return response.data
    }

    throw new Error(response.message || "Order not found")
  }

  async getBuyerOrders(buyerId: number, page = 0, size = 20): Promise<PageResponse<Order>> {
    const response = await this.httpService.get<ApiResponse<PageResponse<Order>>>(
      `${API_ENDPOINTS.ORDERS.BY_BUYER(buyerId)}?page=${page}&size=${size}`,
    )

    if (response.success && response.data) {
      return response.data
    }

    throw new Error(response.message || "Failed to get buyer orders")
  }

  async getSellerOrders(sellerId: number, page = 0, size = 20): Promise<PageResponse<Order>> {
    const response = await this.httpService.get<ApiResponse<PageResponse<Order>>>(
      `${API_ENDPOINTS.ORDERS.BY_SELLER(sellerId)}?page=${page}&size=${size}`,
    )

    if (response.success && response.data) {
      return response.data
    }

    throw new Error(response.message || "Failed to get seller orders")
  }

  async updateOrderStatus(id: number, status: string): Promise<Order> {
    const response = await this.httpService.put<ApiResponse<Order>>(API_ENDPOINTS.ORDERS.UPDATE_STATUS(id), { status })

    if (response.success && response.data) {
      return response.data
    }

    throw new Error(response.message || "Failed to update order status")
  }
}
