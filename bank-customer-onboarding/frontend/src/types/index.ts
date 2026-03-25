export interface CustomerFormData {
  firstName: string
  lastName: string
  dateOfBirth: string
  email: string
  phone: string
  addressLine1: string
  addressLine2: string
  city: string
  state: string
  postalCode: string
  country: string
  idDocumentType: string
  idDocumentNumber: string
}

export interface CustomerResponse {
  id: string
  firstName: string
  lastName: string
  dateOfBirth: string
  email: string
  phone: string
  addressLine1: string
  addressLine2: string
  city: string
  state: string
  postalCode: string
  country: string
  idDocumentType: string
  idDocumentNumber: string
  onboardingStatus: string
  createdAt: string
  updatedAt: string
}

export interface CustomerStatusResponse {
  customerId: string
  onboardingStatus: string
}

export interface KycStatusResponse {
  customerId: string
  overallStatus: string
  riskScore: number
  totalChecks: number
  passedChecks: number
  failedChecks: number
}

export interface KycCheckResponse {
  id: string
  customerId: string
  checkType: string
  status: string
  details: string
  riskScore: number
  performedAt: string
}

export interface NotificationResponse {
  id: string
  customerId: string
  type: string
  recipientEmail: string
  subject: string
  message: string
  status: string
  sentAt: string
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

export const initialFormData: CustomerFormData = {
  firstName: '',
  lastName: '',
  dateOfBirth: '',
  email: '',
  phone: '',
  addressLine1: '',
  addressLine2: '',
  city: '',
  state: '',
  postalCode: '',
  country: '',
  idDocumentType: '',
  idDocumentNumber: '',
}
