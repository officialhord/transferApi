package com.indextest.transferApi.service.impl;

import com.google.gson.Gson;
import com.indextest.transferApi.model.APICall;
import com.indextest.transferApi.model.NIPBanks;
import com.indextest.transferApi.payload.request.BankAccountValidationRequest;
import com.indextest.transferApi.payload.request.ExternalTransferRequest;
import com.indextest.transferApi.payload.request.TransferRequest;
import com.indextest.transferApi.payload.response.APIResponse;
import com.indextest.transferApi.payload.response.AccountNameEnquiryResponse;
import com.indextest.transferApi.payload.response.BanksResponse;
import com.indextest.transferApi.repo.APICallRepository;
import com.indextest.transferApi.repo.NIPBanksRepository;
import com.indextest.transferApi.repo.NIPTransferRequestsRepository;
import com.indextest.transferApi.service.TransferApiService;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeoutException;

@Service
public class TransferApiServiceImpl implements TransferApiService {

    //Fetching All Keys from application properties
    // (Preferably should come from encrypted value in DB or Environment variable)...
    @Value("${apiKey}")
    private String apiKey;

    @Value("${paystackSecretKey}")
    private String paystackKey;
    @Autowired
    private NIPBanksRepository banksRepo;
    @Autowired
    private NIPTransferRequestsRepository nipTransferRequestsRepository;
    @Autowired
    private APICallRepository apiCallRepository;

    @Override
    public APIResponse getAllBanks(String key) {
        Gson gson = new Gson();

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

        Gson gson = new Gson();
        APICall apiCall = saveAPIRequest(bankAccountValidationRequest, gson);

        APIResponse response = new APIResponse();
        if (keyIsValid(key)) {
            if (bankCodeIsValid(bankAccountValidationRequest.getBankCode())) {
                if (accountNumberIsValid(bankAccountValidationRequest.getAccountNumber())) {

                    AccountNameEnquiryResponse enquiryResponse = doAccountEnquiry(bankAccountValidationRequest);
                    if (enquiryResponse != null) {
                        response.setCode("00");
                        response.setDescription("Account name enquiry successful");
                        response.setResponseContent(new ArrayList<>((Collection) enquiryResponse));

                    }
                } else {
                    response.setCode("99");
                    response.setDescription("Invalid account number");
                }
            } else {
                response.setCode("99");
                response.setDescription("Invalid bank code received");
            }
        }
        updateRequest(gson, apiCall, response);
        return response;
    }

    private void updateRequest(Gson gson, APICall apiCall, Object object) {
        apiCall.setResponseBody(gson.toJson(object));
        apiCall.setResponseTime(LocalDateTime.now());
        apiCallRepository.save(apiCall);
    }

    private APICall saveAPIRequest(Object object, Gson gson) {
        APICall apiCall = new APICall();
        apiCall.setMethod("GET");
        apiCall.setRequestId(UUID.randomUUID());
        apiCall.setRequestBody(gson.toJson(object));
        apiCall.setRequestTime(LocalDateTime.now());
        apiCallRepository.save(apiCall);
        return apiCall;
    }

    private AccountNameEnquiryResponse doAccountEnquiry(BankAccountValidationRequest bankAccountValidationRequest) {

        String url = resolveUrl(bankAccountValidationRequest);
        try {

            System.out.println("Sending request ===> " + url);
            String response = sendGetRequestApache(url, "");

            if (!ObjectUtils.isEmpty(response)) {
                if (response.contains("Account number resolved")) {

                    Gson gson = new Gson();
                    AccountNameEnquiryResponse enquiryResponse = gson.fromJson(response, AccountNameEnquiryResponse.class);

                    return enquiryResponse;

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String resolveUrl(BankAccountValidationRequest bankAccountValidationRequest) {
        return "https://api.paystack.co/bank/resolve?account_number=" +
                bankAccountValidationRequest.getAccountNumber() + "&bank_code=" + bankAccountValidationRequest.getBankCode() + "";
    }

    private boolean accountNumberIsValid(String accountNumber) {
        if (!ObjectUtils.isEmpty(accountNumber)) {
            if (accountNumber.matches("[0-9]+") && Long.parseLong(accountNumber) > 0) {
                return true;
            }
        }
        return false;
    }

    private boolean bankCodeIsValid(String code) {

        if (!ObjectUtils.isEmpty(code)) {
            if (code.matches("[0-9]+")) {
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

    @Override
    public APIResponse handleTransferRequest(String key, TransferRequest transferRequest) {
        APIResponse response = new APIResponse();
        String url = "https://api.flutterwave.com/v3/transfers";

        Gson gson = new Gson();
        APICall apiCall = saveAPIRequest(transferRequest, gson);

        if (keyIsValid(key)) {
            //Validate request body....
            if (bankCodeIsValid(transferRequest.getBeneficiaryBankCode())) {
                if (accountNumberIsValid(transferRequest.getBeneficiaryAccountNumber())) {
                    //Create url and post body

                    ExternalTransferRequest externalTransferRequest = new ExternalTransferRequest();
                    externalTransferRequest.setAmount(transferRequest.getAmount());
                    externalTransferRequest.setNarration(transferRequest.getNarration());
                    externalTransferRequest.setBeneficiaryAccountName("");

                    String payload = gson.toJson(externalTransferRequest);


                    //Send request
                    try {
                        String transferResponse = sendPostRequestApache(url, payload);

                        //TODO: Parse response and update transaction status
                        // updateRequest(gson, transferResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }


        }
        return response;
    }

    public APIResponse handleGetTransactionStatus(String key) {
        APIResponse response = new APIResponse();
        if (keyIsValid(key)) {
            //TODO: Complete logic and sending request to Partner API
        }
        return response;
    }


    private String sendPostRequestApache(String url, String payload) throws TimeoutException, IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Authorization", paystackKey);
        String finalResponse = null;

        httpPost.setEntity(new StringEntity(payload));
        CloseableHttpResponse response = httpClient.execute(httpPost);

        System.out.println(response.getStatusLine());
        HttpEntity entity = response.getEntity();

        // Read the response from the server
        InputStreamReader streamReader = new InputStreamReader(entity.getContent());
        BufferedReader reader = new BufferedReader(streamReader);
        StringBuilder response1 = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response1.append(line);
        }
        finalResponse = response1.toString();
        reader.close();

        System.out.println("response message: " + response1);

        response.close();

        return finalResponse;

    }

    private String sendGetRequestApache(String url, String payload) throws TimeoutException, IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpPost = new HttpGet(url);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Authorization", paystackKey);
        String finalResponse = null;

//        httpPost.setEntity(new StringEntity(payload));
        CloseableHttpResponse response = httpClient.execute(httpPost);

        System.out.println(response.getStatusLine());
        HttpEntity entity = response.getEntity();

        // Read the response from the server
        InputStreamReader streamReader = new InputStreamReader(entity.getContent());
        BufferedReader reader = new BufferedReader(streamReader);
        StringBuilder response1 = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response1.append(line);
        }
        finalResponse = response1.toString();
        reader.close();

        System.out.println("response message: " + response1);

        response.close();

        return finalResponse;

    }
}
