import React, { useState, useEffect } from 'react';
import { getRequest } from '../services/api';
import { getSession } from '../services/auth';
import VehicleFilters from '../components/vehicles/VehicleFilters';
import VehicleTable from '../components/vehicles/VehicleTable';
import './AdminUsersStyles.css';

// Standalone IoT Simulator runs on port 8082 — used only for status polling
const SIM_STATUS_URL = 'http://localhost:8082/api/simulation/status';

function Vehicles() {
    const session = getSession();
    const currentUserId = session?.userId;
    const userRole = session?.role;

    const [vehicles, setVehicles] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [typeFilter, setTypeFilter] = useState('All');

    // IoT status — purely read-only indicator, no controls here
    const [iotConnected, setIotConnected] = useState(null); // null=checking, true=online, false=offline
    const [simStatus, setSimStatus] = useState({ activeCount: 0, activeVehicleIds: [] });

    useEffect(() => {
        fetchVehicles();
    }, []);

    // Poll IoT Simulator status every 5 seconds — indicator only, no control
    useEffect(() => {
        const check = async () => {
            try {
                const res = await fetch(SIM_STATUS_URL);
                if (res.ok) {
                    const data = await res.json();
                    setIotConnected(true);
                    setSimStatus(data);
                } else {
                    setIotConnected(false);
                }
            } catch {
                setIotConnected(false);
                setSimStatus({ activeCount: 0, activeVehicleIds: [] });
            }
        };
        check();
        const interval = setInterval(check, 5000);
        return () => clearInterval(interval);
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

            {/* ── IoT Device Connection Indicator (read-only) ── */}
            <div style={{
                display: 'flex',
                alignItems: 'center',
                gap: '14px',
                background: iotConnected === null
                    ? 'rgba(102,126,234,0.05)'
                    : iotConnected
                        ? 'linear-gradient(135deg, rgba(39,174,96,0.07), rgba(46,204,113,0.07))'
                        : 'linear-gradient(135deg, rgba(231,76,60,0.07), rgba(192,57,43,0.07))',
                border: `1px solid ${
                    iotConnected === null
                        ? 'rgba(102,126,234,0.2)'
                        : iotConnected
                            ? 'rgba(46,204,113,0.3)'
                            : 'rgba(231,76,60,0.3)'
                }`,
                borderRadius: '12px',
                padding: '13px 20px',
                marginBottom: '20px',
                transition: 'all 0.3s ease',
            }}>
                {/* Pulsing dot */}
                <span style={{
                    width: '10px', height: '10px', borderRadius: '50%',
                    background: iotConnected === null ? '#bdc3c7' : iotConnected ? '#2ecc71' : '#e74c3c',
                    boxShadow: iotConnected ? '0 0 0 3px rgba(46,204,113,0.25)' : 'none',
                    animation: iotConnected ? 'pulse-glow 1.5s infinite' : 'none',
                    display: 'inline-block', flexShrink: 0,
                }} />

                {/* Label */}
                <div style={{ flex: 1 }}>
                    <span style={{ fontWeight: '700', fontSize: '14px', color: '#2c3e50' }}>
                        🛰️ IoT Device Link —&nbsp;
                    </span>
                    <span style={{
                        fontWeight: '800', fontSize: '13px',
                        color: iotConnected === null ? '#7f8c8d' : iotConnected ? '#27ae60' : '#e74c3c',
                    }}>
                        {iotConnected === null
                            ? 'Checking connection…'
                            : iotConnected
                                ? `CONNECTED  ·  ${simStatus.activeCount} device${simStatus.activeCount !== 1 ? 's' : ''} broadcasting live GPS`
                                : 'DISCONNECTED  ·  Start the IoT Simulator to receive live data'}
                    </span>
                </div>

                <span style={{ fontSize: '11px', color: '#bdc3c7' }}>🔄 auto-refresh 5s</span>
            </div>

            <div className="card" style={{ marginTop: '0' }}>
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
                    activeSimIds={simStatus.activeVehicleIds}
                />
            </div>

            <style>{`
                @keyframes pulse-glow {
                    0%   { box-shadow: 0 0 0 0 rgba(46,204,113,0.5); }
                    70%  { box-shadow: 0 0 0 8px rgba(46,204,113,0); }
                    100% { box-shadow: 0 0 0 0 rgba(46,204,113,0); }
                }
            `}</style>
        </div>
    );
}

export default Vehicles;
