package com.indextest.transferApi.payload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountNameEnquiryResponse {

    private String bankName;
    private String bankCode;
    private String accountName;
}
