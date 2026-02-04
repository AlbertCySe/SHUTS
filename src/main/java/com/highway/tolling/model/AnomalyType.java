package com.highway.tolling.model;

/**
 * Anomaly Type Enum
 * Types of anomalies that can be detected in IoT data
 */
public enum AnomalyType {
    MISSING_DATA, // No GPS data received for extended period
    INACTIVITY_ON_HIGHWAY, // Vehicle stationary on highway for too long
    SUDDEN_DISCONNECTION, // Abrupt stop in GPS transmission
    REPEATED_PATTERN, // Same suspicious pattern multiple times
    SUSPICIOUS_DISTANCE, // Unrealistic distance traveled
    GPS_TAMPERING // Potential GPS manipulation
}
