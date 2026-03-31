package com.highway.tolling.iot.simulator.model;

/**
 * Vehicle Movement State
 */
public enum VehicleState {
    STOPPED, // Vehicle at rest
    ACCELERATING, // Speeding up
    CRUISING, // Constant speed
    DECELERATING // Slowing down
}
