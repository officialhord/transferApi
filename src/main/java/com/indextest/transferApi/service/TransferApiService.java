package com.indextest.transferApi.service;

import com.indextest.transferApi.payload.request.BankAccountValidationRequest;
import com.indextest.transferApi.payload.request.TransferRequest;
import com.indextest.transferApi.payload.response.APIResponse;

public interface TransferApiService {

    public APIResponse getAllBanks(String key);


    public APIResponse validateBankAccount(String key, BankAccountValidationRequest bankAccountValidationRequest);

    public APIResponse handleTransferRequest(String key, TransferRequest transferRequest);
}
