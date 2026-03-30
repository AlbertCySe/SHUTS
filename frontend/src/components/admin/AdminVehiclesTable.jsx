import React, { useState, useEffect } from 'react';
import { getRequest, deleteRequest, patchRequest } from '../../services/api';
import { usePagination } from '../../hooks/usePagination';
import Paginator from '../Paginator';
import LoadingFallback from '../LoadingFallback';

function AdminVehiclesTable({ refreshTrigger, onEditTrigger, onAddTrigger }) {
    const [vehicles, setVehicles] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [togglingId, setTogglingId] = useState(null);

    const { page, setPage, totalPages, paged: pagedVehicles, rangeLabel } = usePagination(vehicles, 10);

    useEffect(() => {
        fetchVehicles();
    }, [refreshTrigger]);

    const fetchVehicles = async () => {
        try {
            setLoading(true);
            const data = await getRequest('/vehicles');
            setVehicles(Array.isArray(data) ? data : (data.content || []));
        } catch (err) {
            setError('Failed to load vehicles from server.');
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (id, number) => {
        if (!window.confirm(`Are you sure you want to delete vehicle "${number}"? This action cannot be undone.`)) return;
        try {
            await deleteRequest(`/vehicles/${id}`);
            setVehicles(vehicles.filter(v => v.vehicleId !== id));
        } catch (err) {
            alert('Failed to delete vehicle. It may be linked to active location or billing data.');
        }
    };

    const handleToggleStatus = async (vehicle) => {
        setTogglingId(vehicle.vehicleId);
        try {
            const updatedVehicle = await patchRequest(`/vehicles/${vehicle.vehicleId}/toggle-status`);
            setVehicles(vehicles.map(v => v.vehicleId === vehicle.vehicleId ? { ...v, status: updatedVehicle.status } : v));
        } catch (err) { alert('Failed to update vehicle status.'); } 
        finally { setTogglingId(null); }
    };

    if (loading) return <LoadingFallback />;

    return (
        <>
            {error && (
                <div className="error-message" style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <p>⚠️ {error}</p>
                    <button className="btn btn-secondary btn-sm" onClick={fetchVehicles}>Retry</button>
                </div>
            )}
            
            <div className="card">
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
                    <h3 style={{ margin: 0 }}>Registered Vehicles ({vehicles.length})</h3>
                    <div style={{ display: 'flex', gap: '10px' }}>
                        <button className="admin-btn-add" onClick={onAddTrigger}>➕ Add Vehicle</button>
                        <button className="admin-btn-refresh" onClick={fetchVehicles}>🔄 Refresh</button>
                    </div>
                </div>

                <div className="table-responsive">
                    <table className="custom-data-table">
                        <thead>
                            <tr>
                                <th>#</th>
                                <th>Reg. Number</th>
                                <th>Type</th>
                                <th>Owner ID</th>
                                <th>Status</th>
                                <th>Registered Date</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {vehicles.length === 0 ? (
                                <tr>
                                    <td colSpan="7" style={{ textAlign: 'center', padding: '30px', color: '#7f8c8d' }}>
                                        No vehicles found in the system.
                                    </td>
                                </tr>
                            ) : (
                                pagedVehicles.map((vehicle, i) => (
                                    <tr key={vehicle.vehicleId}>
                                        <td><span className="admin-id-pill">#{(page - 1) * 10 + i + 1}</span></td>
                                        <td style={{ fontWeight: '600' }}>{vehicle.vehicleNumber}</td>
                                        <td>
                                            <span style={{ background: '#edf2f7', color: '#2c3e50', padding: '3px 8px', borderRadius: '4px', fontSize: '12px', fontWeight: 'bold' }}>
                                                {vehicle.vehicleType}
                                            </span>
                                        </td>
                                        <td>
                                            {vehicle.ownerId ? (
                                                <span style={{ fontWeight: 'bold', color: '#16a085' }}>👤 User {vehicle.ownerId}</span>
                                            ) : (
                                                <span style={{ color: '#e74c3c' }}>Unassigned</span>
                                            )}
                                        </td>
                                        <td>
                                            <button 
                                                className={`status-toggle-btn ${vehicle.status?.toUpperCase() === 'INACTIVE' ? 'status-inactive' : 'status-active'}`}
                                                onClick={() => handleToggleStatus(vehicle)}
                                                disabled={togglingId === vehicle.vehicleId}
                                                title={vehicle.status?.toUpperCase() === 'INACTIVE' ? 'Click to Activate' : 'Click to Deactivate'}
                                            >
                                                {togglingId === vehicle.vehicleId ? '...' : (
                                                    <><span className="status-dot">●</span> {vehicle.status || 'ACTIVE'}</>
                                                )}
                                            </button>
                                        </td>
                                        <td><span style={{ color: '#7f8c8d', fontSize: '13px' }}>{vehicle.registeredAt ? new Date(vehicle.registeredAt).toLocaleDateString() : 'N/A'}</span></td>
                                        <td>
                                            <div className="admin-action-group">
                                                <button className="admin-btn-sm admin-btn-edit" onClick={() => onEditTrigger(vehicle)} title="Edit Vehicle">✏️ Edit</button>
                                                <button className="admin-btn-sm admin-btn-delete" onClick={() => handleDelete(vehicle.vehicleId, vehicle.vehicleNumber)} title="Delete Vehicle">🗑️ Delete</button>
                                            </div>
                                        </td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>
                <Paginator page={page} totalPages={totalPages} rangeLabel={rangeLabel} onPageChange={setPage} />
            </div>
        </>
    );
}

export default AdminVehiclesTable;
