import { Box, Typography, Button, Paper, Grid } from '@mui/material'
import { PersonAdd, Security, Notifications } from '@mui/icons-material'
import { useNavigate } from 'react-router-dom'

const LandingPage = () => {
  const navigate = useNavigate()

  return (
    <Box sx={{ textAlign: 'center', mt: 8 }}>
      <Typography variant="h3" component="h1" gutterBottom>
        Welcome to Bank Onboarding
      </Typography>
      <Typography variant="h6" color="text.secondary" paragraph>
        Complete your customer onboarding process in just a few simple steps.
        Our secure platform ensures your data is protected throughout the process.
      </Typography>
      <Button
        variant="contained"
        size="large"
        sx={{ mt: 3, mb: 6, px: 6, py: 1.5 }}
        onClick={() => navigate('/onboarding/personal')}
      >
        Start Onboarding
      </Button>

      <Grid container spacing={4} sx={{ mt: 4 }}>
        <Grid item xs={12} md={4}>
          <Paper elevation={3} sx={{ p: 4, height: '100%' }}>
            <PersonAdd sx={{ fontSize: 48, color: 'primary.main', mb: 2 }} />
            <Typography variant="h5" gutterBottom>
              Simple Registration
            </Typography>
            <Typography color="text.secondary">
              Complete your personal details, address, and identity documents in our easy 4-step process.
            </Typography>
          </Paper>
        </Grid>
        <Grid item xs={12} md={4}>
          <Paper elevation={3} sx={{ p: 4, height: '100%' }}>
            <Security sx={{ fontSize: 48, color: 'primary.main', mb: 2 }} />
            <Typography variant="h5" gutterBottom>
              Automated KYC
            </Typography>
            <Typography color="text.secondary">
              Our system performs identity verification, document validation, sanctions screening, and PEP checks automatically.
            </Typography>
          </Paper>
        </Grid>
        <Grid item xs={12} md={4}>
          <Paper elevation={3} sx={{ p: 4, height: '100%' }}>
            <Notifications sx={{ fontSize: 48, color: 'primary.main', mb: 2 }} />
            <Typography variant="h5" gutterBottom>
              Real-time Updates
            </Typography>
            <Typography color="text.secondary">
              Track your onboarding status in real-time and receive email notifications at every step.
            </Typography>
          </Paper>
        </Grid>
      </Grid>
    </Box>
  )
}

export default LandingPage
