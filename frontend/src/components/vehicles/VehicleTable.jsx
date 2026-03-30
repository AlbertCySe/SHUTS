import React, { useState } from 'react';
import { usePagination } from '../../hooks/usePagination';
import Paginator from '../Paginator';
import VehicleRequestModal from './VehicleRequestModal';

const STATUS_STYLE = {
    ACTIVE:   { background: 'rgba(39,174,96,0.12)',  color: '#27ae60' },
    INACTIVE: { background: 'rgba(231,76,60,0.1)',   color: '#e74c3c' },
    SCRAPED:  { background: 'rgba(127,140,141,0.15)', color: '#7f8c8d' },
};

function VehicleTable({ vehicles, loading, error, onRefresh }) {
    // Hooks always at top — before any early returns
    const { page, setPage, totalPages, paged: pagedVehicles, rangeLabel } = usePagination(vehicles, 10);
    const [showModal, setShowModal] = useState(false);
    const [preSelected, setPreSelected] = useState(null);

    if (loading) return <p className="info-text">Loading vehicles...</p>;
    if (error) return (
        <div className="error-message">
            <p>{error}</p>
            <button onClick={onRefresh} className="btn btn-primary">Retry</button>
        </div>
    );

    const openRequest = (vehicle = null) => {
        setPreSelected(vehicle);
        setShowModal(true);
    };

    return (
        <>
            {/* ── Add New Vehicle Request button ── */}
            <div style={{ display: 'flex', justifyContent: 'flex-end', marginBottom: '12px' }}>
                <button
                    onClick={() => openRequest(null)}
                    style={{
                        padding: '9px 18px', borderRadius: '8px',
                        background: 'linear-gradient(135deg, #667eea, #764ba2)',
                        color: 'white', border: 'none', fontWeight: '700',
                        cursor: 'pointer', fontSize: '14px', display: 'flex', alignItems: 'center', gap: '6px'
                    }}>
                    ➕ Request New Vehicle
                </button>
            </div>

            <div className="table-container">
                <table className="table vehicles-table">
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Vehicle Number</th>
                            <th>Type</th>
                            <th>Status</th>
                            <th>Registered</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        {vehicles.length === 0 ? (
                            <tr><td colSpan="6" style={{ textAlign: 'center', padding: '30px', color: '#7f8c8d' }}>
                                No vehicles yet. Request to add one above.
                            </td></tr>
                        ) : (
                            pagedVehicles.map((vehicle, i) => {
                                const s = STATUS_STYLE[vehicle.status] || STATUS_STYLE.ACTIVE;
                                return (
                                    <tr key={vehicle.vehicleId}>
                                        <td style={{ color: '#7f8c8d', fontSize: '13px' }}>
                                            {(page - 1) * 10 + i + 1}
                                        </td>
                                        <td><strong>{vehicle.vehicleNumber}</strong></td>
                                        <td>
                                            <span className="badge badge-type">{vehicle.vehicleType}</span>
                                        </td>
                                        <td>
                                            <span style={{
                                                padding: '3px 10px', borderRadius: '12px',
                                                fontSize: '12px', fontWeight: '700',
                                                background: s.background, color: s.color
                                            }}>
                                                {vehicle.status || 'ACTIVE'}
                                            </span>
                                        </td>
                                        <td style={{ color: '#7f8c8d', fontSize: '12px' }}>
                                            {vehicle.registeredAt
                                                ? new Date(vehicle.registeredAt).toLocaleDateString('en-IN')
                                                : '—'}
                                        </td>
                                        <td>
                                            {vehicle.status === 'SCRAPED' ? (
                                                <span style={{ fontSize: '12px', color: '#7f8c8d', fontStyle: 'italic' }}>Retired</span>
                                            ) : (
                                                <button
                                                    onClick={() => openRequest(vehicle)}
                                                    style={{
                                                        padding: '5px 12px', borderRadius: '6px',
                                                        border: '1px solid #667eea', background: 'white',
                                                        color: '#667eea', fontWeight: '600', fontSize: '12px',
                                                        cursor: 'pointer'
                                                    }}>
                                                    🔔 Request
                                                </button>
                                            )}
                                        </td>
                                    </tr>
                                );
                            })
                        )}
                    </tbody>
                </table>
            </div>

            <Paginator page={page} totalPages={totalPages} rangeLabel={rangeLabel} onPageChange={setPage} />

            {showModal && (
                <VehicleRequestModal
                    vehicles={vehicles}
                    preSelectedVehicle={preSelected}
                    onClose={() => { setShowModal(false); setPreSelected(null); }}
                    onSuccess={onRefresh}
                />
            )}
        </>
    );
}

export default VehicleTable;
