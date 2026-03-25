import { Snackbar, Alert } from '@mui/material'
import { useOnboarding } from '../context/OnboardingContext'

const NotificationSnackbar = () => {
  const { notification, closeNotification } = useOnboarding()

  return (
    <Snackbar
      open={notification.open}
      autoHideDuration={6000}
      onClose={closeNotification}
      anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
    >
      <Alert onClose={closeNotification} severity={notification.severity} sx={{ width: '100%' }}>
        {notification.message}
      </Alert>
    </Snackbar>
  )
}

export default NotificationSnackbar
