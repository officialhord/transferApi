package com.indextest.transferApi.controller;

import com.indextest.transferApi.payload.request.TransferRequest;
import com.indextest.transferApi.payload.response.APIResponse;
import com.indextest.transferApi.payload.request.BankAccountValidationRequest;
import com.indextest.transferApi.service.TransferApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/core-banking/")
public class TransferApiController {

    @Autowired
    TransferApiService transferApiService;

    @RequestMapping(value = "banks", method = RequestMethod.GET)
    private APIResponse getBanks(@RequestHeader("Authorization") String apiKey) {
        return transferApiService.getAllBanks(apiKey);
    }

    @RequestMapping(value = "validateBankAccount", method = RequestMethod.POST)
    private APIResponse validateBankAccount(@RequestHeader("Authorization") String apiKey, @RequestBody
    BankAccountValidationRequest bankAccountValidationRequest) {
        return transferApiService.validateBankAccount(apiKey, bankAccountValidationRequest);
    }


    @RequestMapping(value = "doNIPTransfer", method = RequestMethod.POST)
    private APIResponse doNipTransfer(@RequestHeader("Authorization") String apiKey, @RequestBody
    TransferRequest transferRequest) {
        return transferApiService.handleTransferRequest(apiKey, transferRequest);
    }

}
