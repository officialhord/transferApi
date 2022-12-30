package com.indextest.transferApi.model;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.UUID;


@Data
@Entity
public class NIPTransferRequests {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;
    private BigDecimal amount;
    private String currencyCode;
    private String narration;
    private String beneficiaryAccountNumber;
    private String beneficiaryAccountName;
    private String beneficiaryBankCode;
    private String transactionReference;
    private String maxRetryAttempt;
    private String callBackUrl;
    private String responseCode;
    private String responseMessage;

}
