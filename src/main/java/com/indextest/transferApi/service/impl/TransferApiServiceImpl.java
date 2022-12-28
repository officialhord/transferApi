package com.indextest.transferApi.service.impl;

import com.indextest.transferApi.model.NIPBanks;
import com.indextest.transferApi.payload.request.BankAccountValidationRequest;
import com.indextest.transferApi.payload.response.APIResponse;
import com.indextest.transferApi.payload.response.BanksResponse;
import com.indextest.transferApi.repo.NIPBanksRepo;
import com.indextest.transferApi.service.TransferApiService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;

@Service
public class TransferApiServiceImpl implements TransferApiService {

    //Fetching API key from application properties
    // (Preferably should come from encrypted value in DB or Environment variable)...
    @Value("${apiKey}")
    private String apiKey;
    @Autowired
    private NIPBanksRepo banksRepo;

    @Override
    public APIResponse getAllBanks(String key) {

        System.out.println("Key recieved is ===> " + key);

        APIResponse response = new APIResponse();
        if (keyIsValid(key)) {
            List<NIPBanks> banksList = banksRepo.findAll();

            List<BanksResponse> banksResponse = new ArrayList<>();

            if (!ObjectUtils.isEmpty(banksList)) {
                banksList.forEach(individualRecord -> {
                    BanksResponse bankResponse = new BanksResponse();
                    bankResponse.setBankName(individualRecord.getBankName());
                    bankResponse.setNipCode(individualRecord.getNipCode());

                    String longCode = getLongCode(individualRecord);
                    bankResponse.setLongCode(longCode);

                    banksResponse.add(bankResponse);
                });

                response.setCode("00");
                response.setDescription("Success");
                response.setResponseContent(Collections.singletonList(banksResponse));
            } else {
                response.setCode("01");
                response.setDescription("No Data found");
            }

        } else {
            response.setCode("99");
            response.setDescription("Invalid API Key");
        }
        return response;
    }

    private static String getLongCode(NIPBanks individualRecord) {
        String longCode = individualRecord.getNipCode();
        if (longCode.length() < 6) {
            int length = longCode.length();
            for (int x = 0; x < (6 - length); x++) {
                longCode = "0" + longCode;
            }
        }
        return longCode;
    }

    @Override
    public APIResponse validateBankAccount(String key, BankAccountValidationRequest bankAccountValidationRequest) {

        APIResponse response = new APIResponse();

        if (bankCodeIsValid(bankAccountValidationRequest.getCode())) {

            //TODO: Send request for Account name enquiry



        }

        return response;
    }

    private boolean bankCodeIsValid(String code) {

        if (!ObjectUtils.isEmpty(code)) {
            if (NumberUtils.isCreatable(code)) {
                if (code.length() > 2 && code.length() < 7) {
                    if (Integer.parseInt(code) > 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean keyIsValid(String key) {

        System.out.println("API Key is ====> " + apiKey);
        if (apiKey.equals(key)) {

            System.out.println("Key is valid...");
            return true;
        }

        return false;
    }
}
