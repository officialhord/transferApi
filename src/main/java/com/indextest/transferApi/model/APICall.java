package com.indextest.transferApi.model;


import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
public class APICall {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID requestId;

    private String method;
    private String requestBody;
    private String requestStatus;
    private String responseBody;
    private LocalDateTime requestTime;
    private LocalDateTime responseTime;

}
