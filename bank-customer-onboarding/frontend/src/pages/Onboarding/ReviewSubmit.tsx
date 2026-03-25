import { useState } from 'react'
import {
  Box,
  Typography,
  Paper,
  Button,
  Grid,
  Divider,
  CircularProgress,
  Link,
} from '@mui/material'
import { useNavigate } from 'react-router-dom'
import { useMutation } from '@tanstack/react-query'
import { useOnboarding } from '../../context/OnboardingContext'
import { createCustomer } from '../../api/customerApi'
import OnboardingStepper from '../../components/OnboardingStepper'

const ReviewSubmit = () => {
  const navigate = useNavigate()
  const { formData, showNotification, resetFormData } = useOnboarding()
  const [isSubmitting, setIsSubmitting] = useState(false)

  const mutation = useMutation({
    mutationFn: createCustomer,
    onSuccess: (data) => {
      showNotification('Customer onboarding initiated successfully!', 'success')
      resetFormData()
      navigate(`/onboarding/status/${data.id}`)
    },
    onError: (error: { response?: { data?: { message?: string } } }) => {
      showNotification(
        error.response?.data?.message || 'Failed to submit onboarding request',
        'error'
      )
      setIsSubmitting(false)
    },
  })

  const handleSubmit = () => {
    setIsSubmitting(true)
    mutation.mutate(formData)
  }

  const documentTypeLabels: Record<string, string> = {
    PASSPORT: 'Passport',
    DRIVERS_LICENSE: "Driver's License",
    NATIONAL_ID: 'National ID',
  }

  return (
    <Box>
      <OnboardingStepper activeStep={3} />
      <Paper elevation={3} sx={{ p: 4, maxWidth: 700, mx: 'auto' }}>
        <Typography variant="h5" gutterBottom>
          Review Your Information
        </Typography>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
          Please review all details before submitting. Click on section headers to go back and edit.
        </Typography>

        <Box sx={{ mb: 3 }}>
          <Typography variant="h6" gutterBottom>
            <Link
              component="button"
              onClick={() => navigate('/onboarding/personal')}
              underline="hover"
            >
              Personal Details
            </Link>
          </Typography>
          <Grid container spacing={2}>
            <Grid item xs={6}><Typography variant="body2" color="text.secondary">First Name</Typography><Typography>{formData.firstName}</Typography></Grid>
            <Grid item xs={6}><Typography variant="body2" color="text.secondary">Last Name</Typography><Typography>{formData.lastName}</Typography></Grid>
            <Grid item xs={6}><Typography variant="body2" color="text.secondary">Date of Birth</Typography><Typography>{formData.dateOfBirth}</Typography></Grid>
            <Grid item xs={6}><Typography variant="body2" color="text.secondary">Email</Typography><Typography>{formData.email}</Typography></Grid>
            <Grid item xs={6}><Typography variant="body2" color="text.secondary">Phone</Typography><Typography>{formData.phone}</Typography></Grid>
          </Grid>
        </Box>

        <Divider sx={{ my: 2 }} />

        <Box sx={{ mb: 3 }}>
          <Typography variant="h6" gutterBottom>
            <Link
              component="button"
              onClick={() => navigate('/onboarding/address')}
              underline="hover"
            >
              Address
            </Link>
          </Typography>
          <Grid container spacing={2}>
            <Grid item xs={12}><Typography variant="body2" color="text.secondary">Address Line 1</Typography><Typography>{formData.addressLine1}</Typography></Grid>
            {formData.addressLine2 && (
              <Grid item xs={12}><Typography variant="body2" color="text.secondary">Address Line 2</Typography><Typography>{formData.addressLine2}</Typography></Grid>
            )}
            <Grid item xs={6}><Typography variant="body2" color="text.secondary">City</Typography><Typography>{formData.city}</Typography></Grid>
            <Grid item xs={6}><Typography variant="body2" color="text.secondary">State</Typography><Typography>{formData.state}</Typography></Grid>
            <Grid item xs={6}><Typography variant="body2" color="text.secondary">Postal Code</Typography><Typography>{formData.postalCode}</Typography></Grid>
            <Grid item xs={6}><Typography variant="body2" color="text.secondary">Country</Typography><Typography>{formData.country}</Typography></Grid>
          </Grid>
        </Box>

        <Divider sx={{ my: 2 }} />

        <Box sx={{ mb: 3 }}>
          <Typography variant="h6" gutterBottom>
            <Link
              component="button"
              onClick={() => navigate('/onboarding/documents')}
              underline="hover"
            >
              Identity Document
            </Link>
          </Typography>
          <Grid container spacing={2}>
            <Grid item xs={6}><Typography variant="body2" color="text.secondary">Document Type</Typography><Typography>{documentTypeLabels[formData.idDocumentType] || formData.idDocumentType}</Typography></Grid>
            <Grid item xs={6}><Typography variant="body2" color="text.secondary">Document Number</Typography><Typography>{formData.idDocumentNumber}</Typography></Grid>
          </Grid>
        </Box>

        <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 4 }}>
          <Button variant="outlined" onClick={() => navigate('/onboarding/documents')}>
            Back
          </Button>
          <Button
            variant="contained"
            color="primary"
            size="large"
            onClick={handleSubmit}
            disabled={isSubmitting}
          >
            {isSubmitting ? <CircularProgress size={24} /> : 'Submit Application'}
          </Button>
        </Box>
      </Paper>
    </Box>
  )
}

export default ReviewSubmit
