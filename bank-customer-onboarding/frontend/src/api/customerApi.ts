import apiClient from './client'
import {
  CustomerFormData,
  CustomerResponse,
  CustomerStatusResponse,
  KycStatusResponse,
  KycCheckResponse,
  NotificationResponse,
  PageResponse,
} from '../types'

export const createCustomer = async (data: CustomerFormData): Promise<CustomerResponse> => {
  const response = await apiClient.post<CustomerResponse>('/api/customers', data)
  return response.data
}

export const getCustomer = async (id: string): Promise<CustomerResponse> => {
  const response = await apiClient.get<CustomerResponse>(`/api/customers/${id}`)
  return response.data
}

export const getCustomerStatus = async (id: string): Promise<CustomerStatusResponse> => {
  const response = await apiClient.get<CustomerStatusResponse>(`/api/customers/${id}/status`)
  return response.data
}

export const listCustomers = async (page: number = 0, size: number = 10): Promise<PageResponse<CustomerResponse>> => {
  const response = await apiClient.get<PageResponse<CustomerResponse>>('/api/customers', {
    params: { page, size },
  })
  return response.data
}

export const getKycStatus = async (customerId: string): Promise<KycStatusResponse> => {
  const response = await apiClient.get<KycStatusResponse>(`/api/kyc/${customerId}`)
  return response.data
}

export const getKycChecks = async (customerId: string): Promise<KycCheckResponse[]> => {
  const response = await apiClient.get<KycCheckResponse[]>(`/api/kyc/${customerId}/checks`)
  return response.data
}

export const getNotifications = async (customerId: string): Promise<NotificationResponse[]> => {
  const response = await apiClient.get<NotificationResponse[]>(`/api/notifications/${customerId}`)
  return response.data
}
