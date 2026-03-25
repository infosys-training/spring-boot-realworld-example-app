import { AppBar, Toolbar, Typography, Button, Box } from '@mui/material'
import { AccountBalance } from '@mui/icons-material'
import { useNavigate } from 'react-router-dom'

const Navbar = () => {
  const navigate = useNavigate()

  return (
    <AppBar position="static">
      <Toolbar>
        <AccountBalance sx={{ mr: 2 }} />
        <Typography
          variant="h6"
          component="div"
          sx={{ flexGrow: 1, cursor: 'pointer' }}
          onClick={() => navigate('/')}
        >
          Bank Customer Onboarding
        </Typography>
        <Box>
          <Button color="inherit" onClick={() => navigate('/')}>
            Home
          </Button>
          <Button color="inherit" onClick={() => navigate('/customers')}>
            Customers
          </Button>
        </Box>
      </Toolbar>
    </AppBar>
  )
}

export default Navbar
