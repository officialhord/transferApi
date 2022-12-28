package com.indextest.transferApi.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BankAccountValidationRequest {

    private String code;
    private String accountNumber;
}
