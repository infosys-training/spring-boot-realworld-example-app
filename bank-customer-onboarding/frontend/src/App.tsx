import { Routes, Route } from 'react-router-dom'
import { Container } from '@mui/material'
import { OnboardingProvider } from './context/OnboardingContext'
import Navbar from './components/Navbar'
import NotificationSnackbar from './components/NotificationSnackbar'
import LandingPage from './pages/Landing/LandingPage'
import PersonalDetailsForm from './pages/Onboarding/PersonalDetailsForm'
import AddressForm from './pages/Onboarding/AddressForm'
import DocumentsForm from './pages/Onboarding/DocumentsForm'
import ReviewSubmit from './pages/Onboarding/ReviewSubmit'
import StatusPage from './pages/Status/StatusPage'
import CustomerList from './pages/CustomerList/CustomerList'

function App() {
  return (
    <OnboardingProvider>
      <Navbar />
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        <Routes>
          <Route path="/" element={<LandingPage />} />
          <Route path="/onboarding/personal" element={<PersonalDetailsForm />} />
          <Route path="/onboarding/address" element={<AddressForm />} />
          <Route path="/onboarding/documents" element={<DocumentsForm />} />
          <Route path="/onboarding/review" element={<ReviewSubmit />} />
          <Route path="/onboarding/status/:id" element={<StatusPage />} />
          <Route path="/customers" element={<CustomerList />} />
        </Routes>
      </Container>
      <NotificationSnackbar />
    </OnboardingProvider>
  )
}

export default App
