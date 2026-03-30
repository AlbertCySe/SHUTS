import React, { useState, useEffect } from 'react';
import { getRequest } from '../services/api';
import { getSession } from '../services/auth';
import VehicleFilters from '../components/vehicles/VehicleFilters';
import VehicleTable from '../components/vehicles/VehicleTable';
import './AdminUsersStyles.css';

function Vehicles() {
    const session = getSession();
    const currentUserId = session?.userId;
    const userRole = session?.role;

    const [vehicles, setVehicles] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [typeFilter, setTypeFilter] = useState('All');

    useEffect(() => {
        fetchVehicles();
    }, []);

    const fetchVehicles = async () => {
        try {
            setLoading(true);
            setError(null);
            const url = userRole === 'admin' ? '/vehicles' : `/users/${currentUserId}/vehicles`;
            const data = await getRequest(url);
            const vehicleList = Array.isArray(data) ? data : (data.content || []);
            setVehicles(vehicleList);
        } catch (err) {
            setError('Failed to fetch vehicles. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    const filteredVehicles = vehicles.filter(v => {
        const matchesSearch = (v.vehicleNumber || '').toLowerCase().includes(searchTerm.toLowerCase());
        const matchesType = typeFilter === 'All' || v.vehicleType === typeFilter;
        return matchesSearch && matchesType;
    });

    return (
        <div className="page">
            <h2 className="page-header">🚗 My Vehicles</h2>

            <div className="card" style={{ marginTop: '20px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                    <h3 style={{ margin: 0 }}>Registered Vehicles</h3>
                    <button onClick={fetchVehicles} className="btn-refresh" title="Refresh List">🔄</button>
                </div>
                
                <VehicleFilters 
                    searchTerm={searchTerm} 
                    onSearchChange={setSearchTerm}
                    typeFilter={typeFilter} 
                    onTypeChange={setTypeFilter}
                />

                <VehicleTable 
                    vehicles={filteredVehicles}
                    loading={loading}
                    error={error}
                    onRefresh={fetchVehicles}
                />
            </div>
        </div>
    );
}

export default Vehicles;
