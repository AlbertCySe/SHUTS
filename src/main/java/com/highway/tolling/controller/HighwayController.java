package com.highway.tolling.controller;

import com.highway.tolling.model.Highway;
import com.highway.tolling.service.HighwayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Highway Controller
 * REST API endpoints for highway management
 */
@RestController
@RequestMapping("/api/highways")
public class HighwayController {

    private final HighwayService highwayService;

    @Autowired
    public HighwayController(HighwayService highwayService) {
        this.highwayService = highwayService;
    }

    /**
     * Create a new highway
     * POST /api/highways
     */
    @PostMapping
    public ResponseEntity<Highway> createHighway(@RequestBody Highway highway) {
        try {
            Highway createdHighway = highwayService.createHighway(highway);
            return new ResponseEntity<>(createdHighway, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get all highways
     * GET /api/highways
     */
    @GetMapping
    public ResponseEntity<List<Highway>> getAllHighways() {
        List<Highway> highways = highwayService.getAllHighways();
        return new ResponseEntity<>(highways, HttpStatus.OK);
    }

    /**
     * Get highway by ID
     * GET /api/highways/{highwayId}
     */
    @GetMapping("/{highwayId}")
    public ResponseEntity<Highway> getHighwayById(@PathVariable Long highwayId) {
        return highwayService.getHighwayById(highwayId)
                .map(highway -> new ResponseEntity<>(highway, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Get highway by name
     * GET /api/highways/search?name={highwayName}
     */
    @GetMapping("/search")
    public ResponseEntity<Highway> getHighwayByName(@RequestParam String name) {
        return highwayService.getHighwayByName(name)
                .map(highway -> new ResponseEntity<>(highway, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Update highway information
     * PUT /api/highways/{highwayId}
     */
    @PutMapping("/{highwayId}")
    public ResponseEntity<Highway> updateHighway(@PathVariable Long highwayId, @RequestBody Highway highway) {
        try {
            Highway updatedHighway = highwayService.updateHighway(highwayId, highway);
            return new ResponseEntity<>(updatedHighway, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Delete a highway
     * DELETE /api/highways/{highwayId}
     */
    @DeleteMapping("/{highwayId}")
    public ResponseEntity<Void> deleteHighway(@PathVariable Long highwayId) {
        try {
            highwayService.deleteHighway(highwayId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
