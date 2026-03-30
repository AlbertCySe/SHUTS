package com.highway.tolling.repository;

import com.highway.tolling.model.ProfileUpdateRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileUpdateRequestRepository extends JpaRepository<ProfileUpdateRequest, Long> {
    List<ProfileUpdateRequest> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<ProfileUpdateRequest> findAllByOrderByCreatedAtDesc();
    long countByStatus(ProfileUpdateRequest.RequestStatus status);
    List<ProfileUpdateRequest> findByStatus(ProfileUpdateRequest.RequestStatus status);
}
