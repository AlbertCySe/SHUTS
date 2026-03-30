import React from 'react';

function VehicleFilters({ searchTerm, onSearchChange, typeFilter, onTypeChange }) {
    return (
        <div className="vehicles-controls">
            <div className="search-filter-row">
                <input
                    type="text"
                    className="search-input"
                    placeholder="Search by vehicle number..."
                    value={searchTerm}
                    onChange={(e) => onSearchChange(e.target.value)}
                />
                <select
                    className="filter-select"
                    value={typeFilter}
                    onChange={(e) => onTypeChange(e.target.value)}
                >
                    <option value="All">All Types</option>
                    <option value="CAR">Car</option>
                    <option value="BIKE">Bike</option>
                    <option value="BUS">Bus</option>
                    <option value="TRUCK">Truck</option>
                    <option value="COMMERCIAL">Commercial</option>
                </select>
            </div>
        </div>
    );
}

export default VehicleFilters;
