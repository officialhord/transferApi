package com.indextest.transferApi.payload.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExternalTransferRequest {

    private BigDecimal amount;
    private String currencyCode;
    private String narration;
    private String beneficiaryAccountNumber;
    private String beneficiaryAccountName;
    private String beneficiaryBankCode;
    private String transactionReference;
    private String maxRetryAttempt;
    private String callBackUrl;

}
