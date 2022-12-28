package com.indextest.transferApi.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


@Data
@Entity
public class NIPBanks {

    @Id
    @GeneratedValue()
    private long id;
    private String bankName;
    private String nipCode;

}
