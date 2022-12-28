package com.indextest.transferApi.repo;

import com.indextest.transferApi.model.NIPBanks;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NIPBanksRepo extends JpaRepository<NIPBanks, UUID> {
}
