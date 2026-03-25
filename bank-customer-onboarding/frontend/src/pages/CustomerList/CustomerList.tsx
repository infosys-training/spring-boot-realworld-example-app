import { useState } from 'react'
import {
  Box,
  Typography,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TablePagination,
  Chip,
  TextField,
  MenuItem,
  Button,
} from '@mui/material'
import { useQuery } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
import { listCustomers } from '../../api/customerApi'

const statusOptions = ['ALL', 'PENDING', 'KYC_IN_PROGRESS', 'KYC_APPROVED', 'KYC_REJECTED', 'ACTIVE']

const CustomerList = () => {
  const navigate = useNavigate()
  const [page, setPage] = useState(0)
  const [rowsPerPage, setRowsPerPage] = useState(10)
  const [statusFilter, setStatusFilter] = useState('ALL')

  const { data, isLoading } = useQuery({
    queryKey: ['customers', page, rowsPerPage],
    queryFn: () => listCustomers(page, rowsPerPage),
  })

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'ACTIVE': return 'success'
      case 'KYC_APPROVED': return 'success'
      case 'KYC_REJECTED': return 'error'
      case 'PENDING': return 'warning'
      case 'KYC_IN_PROGRESS': return 'info'
      default: return 'default'
    }
  }

  const filteredCustomers = data?.content?.filter(
    (c) => statusFilter === 'ALL' || c.onboardingStatus === statusFilter
  ) || []

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4">Customer Management</Typography>
        <Button variant="contained" onClick={() => navigate('/onboarding/personal')}>
          New Customer
        </Button>
      </Box>

      <Paper elevation={3} sx={{ p: 2, mb: 2 }}>
        <TextField
          select
          size="small"
          label="Filter by Status"
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value)}
          sx={{ minWidth: 200 }}
        >
          {statusOptions.map((status) => (
            <MenuItem key={status} value={status}>
              {status === 'ALL' ? 'All Statuses' : status.replace(/_/g, ' ')}
            </MenuItem>
          ))}
        </TextField>
      </Paper>

      <TableContainer component={Paper} elevation={3}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Name</TableCell>
              <TableCell>Email</TableCell>
              <TableCell>Phone</TableCell>
              <TableCell>Country</TableCell>
              <TableCell>Document Type</TableCell>
              <TableCell>Status</TableCell>
              <TableCell>Created</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {isLoading ? (
              <TableRow>
                <TableCell colSpan={7} align="center">Loading...</TableCell>
              </TableRow>
            ) : filteredCustomers.length === 0 ? (
              <TableRow>
                <TableCell colSpan={7} align="center">No customers found</TableCell>
              </TableRow>
            ) : (
              filteredCustomers.map((customer) => (
                <TableRow
                  key={customer.id}
                  hover
                  sx={{ cursor: 'pointer' }}
                  onClick={() => navigate(`/onboarding/status/${customer.id}`)}
                >
                  <TableCell>{customer.firstName} {customer.lastName}</TableCell>
                  <TableCell>{customer.email}</TableCell>
                  <TableCell>{customer.phone}</TableCell>
                  <TableCell>{customer.country}</TableCell>
                  <TableCell>{customer.idDocumentType}</TableCell>
                  <TableCell>
                    <Chip
                      size="small"
                      label={customer.onboardingStatus}
                      color={getStatusColor(customer.onboardingStatus) as 'success' | 'error' | 'warning' | 'info' | 'default'}
                    />
                  </TableCell>
                  <TableCell>{new Date(customer.createdAt).toLocaleDateString()}</TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
        {data && (
          <TablePagination
            rowsPerPageOptions={[5, 10, 25]}
            component="div"
            count={data.totalElements || 0}
            rowsPerPage={rowsPerPage}
            page={page}
            onPageChange={(_, newPage) => setPage(newPage)}
            onRowsPerPageChange={(e) => {
              setRowsPerPage(parseInt(e.target.value, 10))
              setPage(0)
            }}
          />
        )}
      </TableContainer>
    </Box>
  )
}

export default CustomerList
