import { Stepper, Step, StepLabel, Box } from '@mui/material'

const steps = ['Personal Details', 'Address', 'Documents', 'Review & Submit']

interface OnboardingStepperProps {
  activeStep: number
}

const OnboardingStepper = ({ activeStep }: OnboardingStepperProps) => {
  return (
    <Box sx={{ width: '100%', mb: 4 }}>
      <Stepper activeStep={activeStep} alternativeLabel>
        {steps.map((label) => (
          <Step key={label}>
            <StepLabel>{label}</StepLabel>
          </Step>
        ))}
      </Stepper>
    </Box>
  )
}

export default OnboardingStepper
