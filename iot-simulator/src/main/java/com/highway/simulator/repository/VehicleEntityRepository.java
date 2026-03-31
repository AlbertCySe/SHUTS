package com.highway.simulator.repository;

import com.highway.simulator.entity.VehicleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleEntityRepository extends JpaRepository<VehicleEntity, Long> {
    Optional<VehicleEntity> findByCoreVehicleId(Long coreVehicleId);
    List<VehicleEntity> findAllByCurrentStatus(String currentStatus);
}
