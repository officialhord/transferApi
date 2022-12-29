package com.indextest.transferApi.payload.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {

    private BigDecimal amount;
    private String currencyCode;
    private String narration;
    private String beneficiaryAccountNumber;
    private String beneficiaryBankCode;

}
