import { Box, TextField, Button, Typography, Paper, MenuItem } from '@mui/material'
import { useFormik } from 'formik'
import { useNavigate } from 'react-router-dom'
import { useOnboarding } from '../../context/OnboardingContext'
import { documentsSchema } from '../../validation/schemas'
import OnboardingStepper from '../../components/OnboardingStepper'

const documentTypes = [
  { value: 'PASSPORT', label: 'Passport' },
  { value: 'DRIVERS_LICENSE', label: "Driver's License" },
  { value: 'NATIONAL_ID', label: 'National ID' },
]

const DocumentsForm = () => {
  const navigate = useNavigate()
  const { formData, updateFormData } = useOnboarding()

  const formik = useFormik({
    initialValues: {
      idDocumentType: formData.idDocumentType,
      idDocumentNumber: formData.idDocumentNumber,
    },
    validationSchema: documentsSchema,
    onSubmit: (values) => {
      updateFormData(values)
      navigate('/onboarding/review')
    },
  })

  return (
    <Box>
      <OnboardingStepper activeStep={2} />
      <Paper elevation={3} sx={{ p: 4, maxWidth: 600, mx: 'auto' }}>
        <Typography variant="h5" gutterBottom>
          Identity Document
        </Typography>
        <form onSubmit={formik.handleSubmit}>
          <TextField
            fullWidth
            margin="normal"
            id="idDocumentType"
            name="idDocumentType"
            label="Document Type"
            select
            value={formik.values.idDocumentType}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.idDocumentType && Boolean(formik.errors.idDocumentType)}
            helperText={formik.touched.idDocumentType && formik.errors.idDocumentType}
          >
            {documentTypes.map((option) => (
              <MenuItem key={option.value} value={option.value}>
                {option.label}
              </MenuItem>
            ))}
          </TextField>
          <TextField
            fullWidth
            margin="normal"
            id="idDocumentNumber"
            name="idDocumentNumber"
            label="Document Number"
            value={formik.values.idDocumentNumber}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.idDocumentNumber && Boolean(formik.errors.idDocumentNumber)}
            helperText={formik.touched.idDocumentNumber && formik.errors.idDocumentNumber}
          />
          <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 3 }}>
            <Button variant="outlined" onClick={() => navigate('/onboarding/address')}>
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

export default DocumentsForm
