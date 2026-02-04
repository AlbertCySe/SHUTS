package com.highway.tolling.model;

/**
 * Review Status Enum
 * Status of anomaly review by administrators
 */
public enum ReviewStatus {
    PENDING, // Awaiting review
    REVIEWED, // Reviewed by admin
    RESOLVED, // Issue resolved or false positive
    ESCALATED // Needs further investigation
}
