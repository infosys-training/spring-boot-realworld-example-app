package com.bank.customer.application;

import com.bank.customer.api.dto.CreateCustomerRequest;
import com.bank.customer.api.dto.CustomerResponse;
import com.bank.customer.api.dto.CustomerStatusResponse;
import com.bank.customer.api.dto.UpdateCustomerRequest;
import com.bank.customer.core.event.KycRequestEvent;
import com.bank.customer.core.event.KycResultEvent;
import com.bank.customer.core.model.Customer;
import com.bank.customer.core.model.OnboardingStatus;
import com.bank.customer.core.repository.CustomerRepository;
import com.bank.customer.infrastructure.messaging.RabbitMQPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final RabbitMQPublisher rabbitMQPublisher;

    @Transactional
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        log.info("Creating customer with email: {}", request.getEmail());

        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Customer with email " + request.getEmail() + " already exists");
        }

        Customer customer = customerMapper.toEntity(request);
        customer.setOnboardingStatus(OnboardingStatus.PENDING);
        Customer savedCustomer = customerRepository.save(customer);

        log.info("Customer created with ID: {}", savedCustomer.getId());

        // Publish KYC request event
        KycRequestEvent event = KycRequestEvent.builder()
                .customerId(savedCustomer.getId())
                .firstName(savedCustomer.getFirstName())
                .lastName(savedCustomer.getLastName())
                .dateOfBirth(savedCustomer.getDateOfBirth())
                .email(savedCustomer.getEmail())
                .idDocumentType(savedCustomer.getIdDocumentType().name())
                .idDocumentNumber(savedCustomer.getIdDocumentNumber())
                .build();

        rabbitMQPublisher.publishKycRequest(event);

        // Update status to KYC_IN_PROGRESS
        savedCustomer.setOnboardingStatus(OnboardingStatus.KYC_IN_PROGRESS);
        customerRepository.save(savedCustomer);

        return customerMapper.toResponse(savedCustomer);
    }

    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(UUID id) {
        log.info("Fetching customer with ID: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + id));
        return customerMapper.toResponse(customer);
    }

    @Transactional(readOnly = true)
    public CustomerStatusResponse getCustomerStatus(UUID id) {
        log.info("Fetching status for customer ID: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + id));
        return CustomerStatusResponse.builder()
                .customerId(customer.getId())
                .onboardingStatus(customer.getOnboardingStatus().name())
                .build();
    }

    @Transactional
    public CustomerResponse updateCustomer(UUID id, UpdateCustomerRequest request) {
        log.info("Updating customer with ID: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + id));

        if (request.getFirstName() != null) customer.setFirstName(request.getFirstName());
        if (request.getLastName() != null) customer.setLastName(request.getLastName());
        if (request.getEmail() != null) customer.setEmail(request.getEmail());
        if (request.getPhone() != null) customer.setPhone(request.getPhone());
        if (request.getAddressLine1() != null) customer.setAddressLine1(request.getAddressLine1());
        if (request.getAddressLine2() != null) customer.setAddressLine2(request.getAddressLine2());
        if (request.getCity() != null) customer.setCity(request.getCity());
        if (request.getState() != null) customer.setState(request.getState());
        if (request.getPostalCode() != null) customer.setPostalCode(request.getPostalCode());
        if (request.getCountry() != null) customer.setCountry(request.getCountry());

        Customer updatedCustomer = customerRepository.save(customer);
        return customerMapper.toResponse(updatedCustomer);
    }

    @Transactional(readOnly = true)
    public Page<CustomerResponse> listCustomers(Pageable pageable) {
        log.info("Listing customers, page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return customerRepository.findAll(pageable).map(customerMapper::toResponse);
    }

    @Transactional
    public void handleKycResult(KycResultEvent event) {
        log.info("Handling KYC result for customer ID: {}, status: {}", event.getCustomerId(), event.getStatus());
        Customer customer = customerRepository.findById(event.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + event.getCustomerId()));

        OnboardingStatus newStatus = "KYC_APPROVED".equals(event.getStatus())
                ? OnboardingStatus.KYC_APPROVED
                : OnboardingStatus.KYC_REJECTED;

        customer.setOnboardingStatus(newStatus);
        customerRepository.save(customer);

        if (newStatus == OnboardingStatus.KYC_APPROVED) {
            customer.setOnboardingStatus(OnboardingStatus.ACTIVE);
            customerRepository.save(customer);
            log.info("Customer {} activated", event.getCustomerId());
        }
    }
}
