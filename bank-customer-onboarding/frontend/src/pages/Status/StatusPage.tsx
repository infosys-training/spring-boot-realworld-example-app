import { Box, Typography, Paper, Chip, CircularProgress, Grid, LinearProgress } from '@mui/material'
import { CheckCircle, Cancel, HourglassEmpty } from '@mui/icons-material'
import { useParams } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { getCustomer, getKycChecks, getKycStatus } from '../../api/customerApi'

const StatusPage = () => {
  const { id } = useParams<{ id: string }>()

  const { data: customer, isLoading: customerLoading } = useQuery({
    queryKey: ['customer', id],
    queryFn: () => getCustomer(id!),
    enabled: !!id,
    refetchInterval: 5000,
  })

  const { data: kycStatus } = useQuery({
    queryKey: ['kycStatus', id],
    queryFn: () => getKycStatus(id!),
    enabled: !!id,
    refetchInterval: 5000,
  })

  const { data: kycChecks } = useQuery({
    queryKey: ['kycChecks', id],
    queryFn: () => getKycChecks(id!),
    enabled: !!id,
    refetchInterval: 5000,
  })

  if (customerLoading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 8 }}>
        <CircularProgress />
      </Box>
    )
  }

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'ACTIVE':
      case 'KYC_APPROVED':
      case 'PASSED':
      case 'APPROVED':
        return 'success'
      case 'KYC_REJECTED':
      case 'FAILED':
        return 'error'
      case 'PENDING':
      case 'KYC_IN_PROGRESS':
      case 'IN_PROGRESS':
        return 'warning'
      default:
        return 'default'
    }
  }

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'PASSED':
        return <CheckCircle color="success" />
      case 'FAILED':
        return <Cancel color="error" />
      default:
        return <HourglassEmpty color="warning" />
    }
  }

  const formatCheckType = (type: string) => {
    return type.replace(/_/g, ' ').replace(/\b\w/g, (c) => c.toUpperCase())
  }

  return (
    <Box sx={{ maxWidth: 700, mx: 'auto' }}>
      <Typography variant="h4" gutterBottom>
        Onboarding Status
      </Typography>

      {customer && (
        <Paper elevation={3} sx={{ p: 3, mb: 3 }}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
            <Typography variant="h6">
              {customer.firstName} {customer.lastName}
            </Typography>
            <Chip
              label={customer.onboardingStatus}
              color={getStatusColor(customer.onboardingStatus) as 'success' | 'error' | 'warning' | 'default'}
            />
          </Box>
          <Typography variant="body2" color="text.secondary">
            Email: {customer.email}
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Customer ID: {customer.id}
          </Typography>
        </Paper>
      )}

      {kycStatus && (
        <Paper elevation={3} sx={{ p: 3, mb: 3 }}>
          <Typography variant="h6" gutterBottom>
            KYC Verification Progress
          </Typography>
          <Box sx={{ mb: 2 }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
              <Typography variant="body2">
                {kycStatus.passedChecks} of {kycStatus.totalChecks} checks completed
              </Typography>
              <Chip
                size="small"
                label={`Risk Score: ${kycStatus.riskScore}`}
                color={kycStatus.riskScore < 50 ? 'success' : 'error'}
              />
            </Box>
            <LinearProgress
              variant="determinate"
              value={kycStatus.totalChecks > 0 ? (kycStatus.passedChecks / kycStatus.totalChecks) * 100 : 0}
              sx={{ height: 10, borderRadius: 5 }}
            />
          </Box>
        </Paper>
      )}

      {kycChecks && kycChecks.length > 0 && (
        <Paper elevation={3} sx={{ p: 3 }}>
          <Typography variant="h6" gutterBottom>
            Individual Check Results
          </Typography>
          <Grid container spacing={2}>
            {kycChecks.map((check) => (
              <Grid item xs={12} key={check.id}>
                <Paper variant="outlined" sx={{ p: 2 }}>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                    {getStatusIcon(check.status)}
                    <Box sx={{ flexGrow: 1 }}>
                      <Typography variant="subtitle1">
                        {formatCheckType(check.checkType)}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        {check.details}
                      </Typography>
                    </Box>
                    <Chip
                      size="small"
                      label={check.status}
                      color={getStatusColor(check.status) as 'success' | 'error' | 'warning' | 'default'}
                    />
                  </Box>
                </Paper>
              </Grid>
            ))}
          </Grid>
        </Paper>
      )}

      {(!kycChecks || kycChecks.length === 0) && (
        <Paper elevation={3} sx={{ p: 3, textAlign: 'center' }}>
          <CircularProgress sx={{ mb: 2 }} />
          <Typography>Waiting for KYC verification results...</Typography>
          <Typography variant="body2" color="text.secondary">
            This page auto-refreshes every 5 seconds
          </Typography>
        </Paper>
      )}
    </Box>
  )
}

export default StatusPage
