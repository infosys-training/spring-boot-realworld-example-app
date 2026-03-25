package com.bank.customer.application;

import com.bank.customer.api.dto.CreateCustomerRequest;
import com.bank.customer.api.dto.CustomerResponse;
import com.bank.customer.core.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "onboardingStatus", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "idDocumentType", expression = "java(com.bank.customer.core.model.IdDocumentType.valueOf(request.getIdDocumentType()))")
    Customer toEntity(CreateCustomerRequest request);

    @Mapping(target = "idDocumentType", expression = "java(customer.getIdDocumentType().name())")
    @Mapping(target = "onboardingStatus", expression = "java(customer.getOnboardingStatus().name())")
    CustomerResponse toResponse(Customer customer);
}
