package com.indextest.transferApi.payload.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BankAccountValidationRequest {

    @NotNull(message = "{code cannot empty}")
    @JsonProperty("bankCode")
    private String bankCode;

    @NotNull(message = "{accountNumber cannot empty}")
    @JsonProperty("accountNumber")
    private String accountNumber;

    @JsonProperty("provider")
    private String provider;
}
