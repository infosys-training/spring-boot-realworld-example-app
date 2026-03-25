import React, { createContext, useContext, useState, useCallback } from 'react'
import { CustomerFormData, initialFormData } from '../types'

interface OnboardingContextType {
  formData: CustomerFormData
  updateFormData: (data: Partial<CustomerFormData>) => void
  resetFormData: () => void
  notification: { open: boolean; message: string; severity: 'success' | 'error' | 'info' }
  showNotification: (message: string, severity: 'success' | 'error' | 'info') => void
  closeNotification: () => void
}

const OnboardingContext = createContext<OnboardingContextType | undefined>(undefined)

export const OnboardingProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [formData, setFormData] = useState<CustomerFormData>(initialFormData)
  const [notification, setNotification] = useState<{
    open: boolean
    message: string
    severity: 'success' | 'error' | 'info'
  }>({ open: false, message: '', severity: 'info' })

  const updateFormData = useCallback((data: Partial<CustomerFormData>) => {
    setFormData((prev) => ({ ...prev, ...data }))
  }, [])

  const resetFormData = useCallback(() => {
    setFormData(initialFormData)
  }, [])

  const showNotification = useCallback((message: string, severity: 'success' | 'error' | 'info') => {
    setNotification({ open: true, message, severity })
  }, [])

  const closeNotification = useCallback(() => {
    setNotification((prev) => ({ ...prev, open: false }))
  }, [])

  return (
    <OnboardingContext.Provider
      value={{ formData, updateFormData, resetFormData, notification, showNotification, closeNotification }}
    >
      {children}
    </OnboardingContext.Provider>
  )
}

export const useOnboarding = (): OnboardingContextType => {
  const context = useContext(OnboardingContext)
  if (!context) {
    throw new Error('useOnboarding must be used within an OnboardingProvider')
  }
  return context
}
