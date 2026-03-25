import * as yup from 'yup'

const eighteenYearsAgo = new Date()
eighteenYearsAgo.setFullYear(eighteenYearsAgo.getFullYear() - 18)

export const personalDetailsSchema = yup.object({
  firstName: yup.string().required('First name is required').min(1).max(100),
  lastName: yup.string().required('Last name is required').min(1).max(100),
  dateOfBirth: yup
    .string()
    .required('Date of birth is required')
    .test('age', 'Customer must be at least 18 years old', (value) => {
      if (!value) return false
      const dob = new Date(value)
      return dob <= eighteenYearsAgo
    }),
  email: yup.string().required('Email is required').email('Must be a valid email'),
  phone: yup
    .string()
    .required('Phone number is required')
    .matches(/^\+?[1-9]\d{1,14}$/, 'Phone number must be valid (E.164 format)'),
})

export const addressSchema = yup.object({
  addressLine1: yup.string().required('Address line 1 is required').max(255),
  addressLine2: yup.string().max(255),
  city: yup.string().required('City is required').max(100),
  state: yup.string().required('State is required').max(100),
  postalCode: yup
    .string()
    .required('Postal code is required')
    .matches(/^[A-Za-z0-9\s-]{3,10}$/, 'Postal code must be valid'),
  country: yup.string().required('Country is required').max(100),
})

export const documentsSchema = yup.object({
  idDocumentType: yup.string().required('Document type is required').oneOf(['PASSPORT', 'DRIVERS_LICENSE', 'NATIONAL_ID']),
  idDocumentNumber: yup
    .string()
    .required('Document number is required')
    .min(5, 'Document number must be at least 5 characters')
    .max(50)
    .when('idDocumentType', {
      is: 'PASSPORT',
      then: (schema) => schema.matches(/^[A-Z]{1,2}[0-9]{6,9}$/, 'Passport number format: 1-2 letters followed by 6-9 digits'),
    })
    .when('idDocumentType', {
      is: 'DRIVERS_LICENSE',
      then: (schema) => schema.matches(/^[A-Z0-9]{5,20}$/, 'Driver\'s license must be 5-20 alphanumeric characters'),
    })
    .when('idDocumentType', {
      is: 'NATIONAL_ID',
      then: (schema) => schema.matches(/^[A-Z0-9]{5,20}$/, 'National ID must be 5-20 alphanumeric characters'),
    }),
})
