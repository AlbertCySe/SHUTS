package com.highway.tolling.repository;

import com.highway.tolling.model.RechargeStatus;
import com.highway.tolling.model.WalletRechargeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletRechargeRequestRepository extends JpaRepository<WalletRechargeRequest, Long> {

    List<WalletRechargeRequest> findByUser_UserIdOrderByRequestDateDesc(Long userId);

    List<WalletRechargeRequest> findByStatusOrderByRequestDateAsc(RechargeStatus status);
}
