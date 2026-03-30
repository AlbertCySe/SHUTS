import React from 'react';
import { useNavigate } from 'react-router-dom';

function DashboardMyVehicles({ vehicles }) {
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
                            </tr>
                        </thead>
                        <tbody>
                            {vehicles.map(vehicle => (
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
                                </tr>
                            ))}
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
