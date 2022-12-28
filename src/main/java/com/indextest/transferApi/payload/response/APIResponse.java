package com.indextest.transferApi.payload.response;

import lombok.Data;

import java.util.List;

@Data
public class APIResponse {

    private String code;
    private String description;
    private List<Object> responseContent;

}
