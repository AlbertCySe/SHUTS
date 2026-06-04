import React from 'react';
import { useNavigate } from 'react-router-dom';

function DashboardMyVehicles({ vehicles, activeSimIds = [] }) {
    const navigate = useNavigate();

    return (
        <div className="card vehicles-section">
            <h3>🚙 My Vehicles</h3>
            {vehicles.length > 0 ? (
                <div className="table-container">
                    <table className="table">
                        <thead>
                            <tr>
                                <th>Vehicle Number</th>
                                <th>Type</th>
                                <th>Status</th>
                                <th>IoT Device / GPS Link</th>
                            </tr>
                        </thead>
                        <tbody>
                            {vehicles.map(vehicle => {
                                const isConnected = activeSimIds.includes(vehicle.id);
                                return (
                                    <tr key={vehicle.id}>
                                        <td><strong>{vehicle.number}</strong></td>
                                        <td>
                                            {vehicle.type !== 'Not available' ? (
                                                <span className="badge badge-type">{vehicle.type}</span>
                                            ) : (
                                                <span className="text-muted">{vehicle.type}</span>
                                            )}
                                        </td>
                                        <td>
                                            {vehicle.status === 'ACTIVE' || vehicle.status === 'Active' ? (
                                                <div className="status-container">
                                                    <span className="status-badge status-active">ACTIVE</span>
                                                    {vehicle.statusAssumed && (
                                                        <span className="status-demo-label">Status assumed active (demo)</span>
                                                    )}
                                                </div>
                                            ) : vehicle.status === 'INACTIVE' || vehicle.status === 'Inactive' ? (
                                                <span className="status-badge status-inactive">INACTIVE</span>
                                            ) : (
                                                <span className="text-muted">{vehicle.status}</span>
                                            )}
                                        </td>
                                        <td>
                                            <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                                                <span style={{
                                                    width: '8px', height: '8px', borderRadius: '50%',
                                                    background: isConnected ? '#2ecc71' : '#bdc3c7',
                                                    boxShadow: isConnected ? '0 0 6px #2ecc71' : 'none',
                                                    display: 'inline-block'
                                                }} />
                                                <span style={{
                                                    fontSize: '11px',
                                                    fontWeight: '700',
                                                    color: isConnected ? '#27ae60' : '#7f8c8d',
                                                    background: isConnected ? 'rgba(46,204,113,0.1)' : 'rgba(189,195,199,0.1)',
                                                    padding: '2px 8px',
                                                    borderRadius: '12px',
                                                    border: isConnected ? '1px solid rgba(46,204,113,0.25)' : '1px solid rgba(189,195,199,0.25)',
                                                    letterSpacing: '0.3px'
                                                }}>
                                                    {isConnected ? '📡 CONNECTED (LIVE)' : '💤 PARKED'}
                                                </span>
                                            </div>
                                        </td>
                                    </tr>
                                );
                            })}
                        </tbody>
                    </table>
                </div>
            ) : (
                <div>
                    <p className="info-text">No vehicles registered yet.</p>
                    <button className="btn btn-primary" onClick={() => navigate('/vehicles')}>
                        ➕ Register a Vehicle
                    </button>
                </div>
            )}
        </div>
    );
}

export default DashboardMyVehicles;

