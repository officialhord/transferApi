package com.indextest.transferApi.repo;

import com.indextest.transferApi.model.APICall;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface APICallRepository extends JpaRepository<APICall, UUID> {

}
