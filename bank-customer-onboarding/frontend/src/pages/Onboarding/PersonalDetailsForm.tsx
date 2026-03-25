import { Box, TextField, Button, Typography, Paper } from '@mui/material'
import { useFormik } from 'formik'
import { useNavigate } from 'react-router-dom'
import { useOnboarding } from '../../context/OnboardingContext'
import { personalDetailsSchema } from '../../validation/schemas'
import OnboardingStepper from '../../components/OnboardingStepper'

const PersonalDetailsForm = () => {
  const navigate = useNavigate()
  const { formData, updateFormData } = useOnboarding()

  const formik = useFormik({
    initialValues: {
      firstName: formData.firstName,
      lastName: formData.lastName,
      dateOfBirth: formData.dateOfBirth,
      email: formData.email,
      phone: formData.phone,
    },
    validationSchema: personalDetailsSchema,
    onSubmit: (values) => {
      updateFormData(values)
      navigate('/onboarding/address')
    },
  })

  return (
    <Box>
      <OnboardingStepper activeStep={0} />
      <Paper elevation={3} sx={{ p: 4, maxWidth: 600, mx: 'auto' }}>
        <Typography variant="h5" gutterBottom>
          Personal Details
        </Typography>
        <form onSubmit={formik.handleSubmit}>
          <TextField
            fullWidth
            margin="normal"
            id="firstName"
            name="firstName"
            label="First Name"
            value={formik.values.firstName}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.firstName && Boolean(formik.errors.firstName)}
            helperText={formik.touched.firstName && formik.errors.firstName}
          />
          <TextField
            fullWidth
            margin="normal"
            id="lastName"
            name="lastName"
            label="Last Name"
            value={formik.values.lastName}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.lastName && Boolean(formik.errors.lastName)}
            helperText={formik.touched.lastName && formik.errors.lastName}
          />
          <TextField
            fullWidth
            margin="normal"
            id="dateOfBirth"
            name="dateOfBirth"
            label="Date of Birth"
            type="date"
            InputLabelProps={{ shrink: true }}
            value={formik.values.dateOfBirth}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.dateOfBirth && Boolean(formik.errors.dateOfBirth)}
            helperText={formik.touched.dateOfBirth && formik.errors.dateOfBirth}
          />
          <TextField
            fullWidth
            margin="normal"
            id="email"
            name="email"
            label="Email"
            type="email"
            value={formik.values.email}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.email && Boolean(formik.errors.email)}
            helperText={formik.touched.email && formik.errors.email}
          />
          <TextField
            fullWidth
            margin="normal"
            id="phone"
            name="phone"
            label="Phone Number"
            placeholder="+1234567890"
            value={formik.values.phone}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.phone && Boolean(formik.errors.phone)}
            helperText={formik.touched.phone && formik.errors.phone}
          />
          <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 3 }}>
            <Button variant="outlined" onClick={() => navigate('/')}>
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

export default PersonalDetailsForm
