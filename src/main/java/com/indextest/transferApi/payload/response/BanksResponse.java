package com.indextest.transferApi.payload.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BanksResponse {

        private String nipCode;
        private String bankName;
        private String longCode;

}
