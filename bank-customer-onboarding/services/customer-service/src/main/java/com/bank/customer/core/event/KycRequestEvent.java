package com.bank.customer.core.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KycRequestEvent implements Serializable {

    private UUID customerId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String email;
    private String idDocumentType;
    private String idDocumentNumber;
}
