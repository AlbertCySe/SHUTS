package com.highway.tolling.repository;

import com.highway.tolling.model.VehicleRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRequestRepository extends JpaRepository<VehicleRequest, Long> {
    List<VehicleRequest> findAllByOrderByCreatedAtDesc();
    List<VehicleRequest> findByUserIdOrderByCreatedAtDesc(Long userId);
    long countByStatus(VehicleRequest.RequestStatus status);
}
