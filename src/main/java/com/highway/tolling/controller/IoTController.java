package com.highway.tolling.controller;

import com.highway.tolling.dto.IoTDataRequest;
import com.highway.tolling.dto.IoTDataResponse;
import com.highway.tolling.model.LocationTracking;
import com.highway.tolling.service.IoTIdentificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * IoT Controller
 * Secure entry point for IoT GPS data submission.
 */
@RestController
@RequestMapping("/api/iot")
public class IoTController {

    private final IoTIdentificationService iotIdentificationService;

    @Autowired
    public IoTController(IoTIdentificationService iotIdentificationService) {
        this.iotIdentificationService = iotIdentificationService;
    }

    /**
     * Receive IoT GPS Data
     * POST /api/iot/data
     * Body: { "vehicleId": 1, "latitude": 12.34, "longitude": 56.78, "timestamp":
     * "2026-02-04T13:18:00" }
     */
    @PostMapping("/data")
    public ResponseEntity<IoTDataResponse> receiveIoTData(@Valid @RequestBody IoTDataRequest request) {
        try {
            LocationTracking savedLocation = iotIdentificationService.processIoTData(request);
            IoTDataResponse response = new IoTDataResponse(
                    true,
                    "GPS data received and processed successfully",
                    savedLocation.getId());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // Return 400 Bad Request with the validation error message
            IoTDataResponse response = new IoTDataResponse(false, e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Return 500 Internal Server Error for unexpected issues
            IoTDataResponse response = new IoTDataResponse(false, "Internal Server Error: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Handle validation errors from @Valid annotation
     * Returns detailed field-level validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        response.put("success", false);
        response.put("message", "Validation failed");
        response.put("errors", errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
