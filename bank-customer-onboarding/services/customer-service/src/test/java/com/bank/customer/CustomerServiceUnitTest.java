package com.bank.customer;

import com.bank.customer.api.dto.CreateCustomerRequest;
import com.bank.customer.api.dto.CustomerResponse;
import com.bank.customer.application.CustomerMapper;
import com.bank.customer.application.CustomerService;
import com.bank.customer.core.model.Customer;
import com.bank.customer.core.model.IdDocumentType;
import com.bank.customer.core.model.OnboardingStatus;
import com.bank.customer.core.repository.CustomerRepository;
import com.bank.customer.infrastructure.messaging.RabbitMQPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceUnitTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private RabbitMQPublisher rabbitMQPublisher;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void createCustomer_shouldCreateAndPublishEvent() {
        CreateCustomerRequest request = CreateCustomerRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .email("john@example.com")
                .phone("+1234567890")
                .addressLine1("123 Main St")
                .city("New York")
                .state("NY")
                .postalCode("10001")
                .country("US")
                .idDocumentType("PASSPORT")
                .idDocumentNumber("AB1234567")
                .build();

        UUID customerId = UUID.randomUUID();
        Customer customer = Customer.builder()
                .id(customerId)
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .email("john@example.com")
                .phone("+1234567890")
                .addressLine1("123 Main St")
                .city("New York")
                .state("NY")
                .postalCode("10001")
                .country("US")
                .idDocumentType(IdDocumentType.PASSPORT)
                .idDocumentNumber("AB1234567")
                .onboardingStatus(OnboardingStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        CustomerResponse expectedResponse = CustomerResponse.builder()
                .id(customerId)
                .firstName("John")
                .lastName("Doe")
                .onboardingStatus("KYC_IN_PROGRESS")
                .build();

        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(customerMapper.toEntity(any())).thenReturn(customer);
        when(customerRepository.save(any())).thenReturn(customer);
        when(customerMapper.toResponse(any())).thenReturn(expectedResponse);

        CustomerResponse response = customerService.createCustomer(request);

        assertNotNull(response);
        assertEquals("John", response.getFirstName());
        verify(rabbitMQPublisher, times(1)).publishKycRequest(any());
        verify(customerRepository, times(2)).save(any());
    }

    @Test
    void getCustomerById_shouldReturnCustomer() {
        UUID customerId = UUID.randomUUID();
        Customer customer = Customer.builder()
                .id(customerId)
                .firstName("Jane")
                .lastName("Smith")
                .onboardingStatus(OnboardingStatus.ACTIVE)
                .build();

        CustomerResponse expectedResponse = CustomerResponse.builder()
                .id(customerId)
                .firstName("Jane")
                .lastName("Smith")
                .onboardingStatus("ACTIVE")
                .build();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerMapper.toResponse(customer)).thenReturn(expectedResponse);

        CustomerResponse response = customerService.getCustomerById(customerId);

        assertNotNull(response);
        assertEquals("Jane", response.getFirstName());
        assertEquals("ACTIVE", response.getOnboardingStatus());
    }
}
