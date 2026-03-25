import { Box, TextField, Button, Typography, Paper } from '@mui/material'
import { useFormik } from 'formik'
import { useNavigate } from 'react-router-dom'
import { useOnboarding } from '../../context/OnboardingContext'
import { addressSchema } from '../../validation/schemas'
import OnboardingStepper from '../../components/OnboardingStepper'

const AddressForm = () => {
  const navigate = useNavigate()
  const { formData, updateFormData } = useOnboarding()

  const formik = useFormik({
    initialValues: {
      addressLine1: formData.addressLine1,
      addressLine2: formData.addressLine2,
      city: formData.city,
      state: formData.state,
      postalCode: formData.postalCode,
      country: formData.country,
    },
    validationSchema: addressSchema,
    onSubmit: (values) => {
      updateFormData(values)
      navigate('/onboarding/documents')
    },
  })

  return (
    <Box>
      <OnboardingStepper activeStep={1} />
      <Paper elevation={3} sx={{ p: 4, maxWidth: 600, mx: 'auto' }}>
        <Typography variant="h5" gutterBottom>
          Address Information
        </Typography>
        <form onSubmit={formik.handleSubmit}>
          <TextField
            fullWidth
            margin="normal"
            id="addressLine1"
            name="addressLine1"
            label="Address Line 1"
            value={formik.values.addressLine1}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.addressLine1 && Boolean(formik.errors.addressLine1)}
            helperText={formik.touched.addressLine1 && formik.errors.addressLine1}
          />
          <TextField
            fullWidth
            margin="normal"
            id="addressLine2"
            name="addressLine2"
            label="Address Line 2 (Optional)"
            value={formik.values.addressLine2}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.addressLine2 && Boolean(formik.errors.addressLine2)}
            helperText={formik.touched.addressLine2 && formik.errors.addressLine2}
          />
          <TextField
            fullWidth
            margin="normal"
            id="city"
            name="city"
            label="City"
            value={formik.values.city}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.city && Boolean(formik.errors.city)}
            helperText={formik.touched.city && formik.errors.city}
          />
          <TextField
            fullWidth
            margin="normal"
            id="state"
            name="state"
            label="State"
            value={formik.values.state}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.state && Boolean(formik.errors.state)}
            helperText={formik.touched.state && formik.errors.state}
          />
          <TextField
            fullWidth
            margin="normal"
            id="postalCode"
            name="postalCode"
            label="Postal Code"
            value={formik.values.postalCode}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.postalCode && Boolean(formik.errors.postalCode)}
            helperText={formik.touched.postalCode && formik.errors.postalCode}
          />
          <TextField
            fullWidth
            margin="normal"
            id="country"
            name="country"
            label="Country"
            value={formik.values.country}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.country && Boolean(formik.errors.country)}
            helperText={formik.touched.country && formik.errors.country}
          />
          <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 3 }}>
            <Button variant="outlined" onClick={() => navigate('/onboarding/personal')}>
              Back
            </Button>
            <Button type="submit" variant="contained">
              Next
            </Button>
          </Box>
        </form>
      </Paper>
    </Box>
  )
}

export default AddressForm
