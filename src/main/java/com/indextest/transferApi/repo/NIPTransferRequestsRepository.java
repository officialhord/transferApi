package com.indextest.transferApi.repo;

import com.indextest.transferApi.model.NIPTransferRequests;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NIPTransferRequestsRepository extends JpaRepository<NIPTransferRequests, UUID> {
}
