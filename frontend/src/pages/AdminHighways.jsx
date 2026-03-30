import { useState, useEffect } from 'react';
import { getRequest, postRequest, putRequest, deleteRequest } from '../services/api';
import LoadingFallback from '../components/LoadingFallback';
import { usePagination } from '../hooks/usePagination';
import Paginator from '../components/Paginator';
import './AdminUsersStyles.css';

function AdminHighways() {
    const [highways, setHighways] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    // Modal State
    const [showModal, setShowModal] = useState(false);
    const [modalMode, setModalMode] = useState('add');
    const [formData, setFormData] = useState({
        highwayId: null,
        highwayName: '',
        startLatitude: '',
        startLongitude: '',
        endLatitude: '',
        endLongitude: '',
        ratePerKmForCar: '',
        ratePerKmForBike: '',
        ratePerKmForTruck: ''
    });
    const [actionLoading, setActionLoading] = useState(false);

    useEffect(() => {
        fetchHighways();
    }, []);

    const fetchHighways = async () => {
        try {
            setLoading(true);
            const data = await getRequest('/highways');
            setHighways(Array.isArray(data) ? data : []);
        } catch (err) {
            setError('Failed to load highways from server.');
        } finally {
            setLoading(false);
        }
    };

    const handleAddClick = () => {
        setModalMode('add');
        setFormData({
            highwayId: null,
            highwayName: '',
            startLatitude: '',
            startLongitude: '',
            endLatitude: '',
            endLongitude: '',
            ratePerKmForCar: '',
            ratePerKmForBike: '',
            ratePerKmForTruck: ''
        });
        setShowModal(true);
    };

    const handleEditClick = (highway) => {
        setModalMode('edit');
        setFormData({ ...highway });
        setShowModal(true);
    };

    const handleModalSubmit = async (e) => {
        e.preventDefault();
        setActionLoading(true);
        try {
            const payload = {
                highwayName: formData.highwayName,
                startLatitude: parseFloat(formData.startLatitude),
                startLongitude: parseFloat(formData.startLongitude),
                endLatitude: parseFloat(formData.endLatitude),
                endLongitude: parseFloat(formData.endLongitude),
                ratePerKmForCar: parseFloat(formData.ratePerKmForCar),
                ratePerKmForBike: parseFloat(formData.ratePerKmForBike),
                ratePerKmForTruck: parseFloat(formData.ratePerKmForTruck)
            };

            if (modalMode === 'add') {
                await postRequest('/highways', payload);
            } else {
                await putRequest(`/highways/${formData.highwayId}`, payload);
            }
            setShowModal(false);
            fetchHighways();
        } catch (err) {
            alert(`Failed to ${modalMode} highway. Please verify coordinates and try again.`);
        } finally {
            setActionLoading(false);
        }
    };

    const handleDelete = async (id, name) => {
        if (!window.confirm(`Are you sure you want to delete highway "${name}"? This action cannot be undone.`)) {
            return;
        }

        try {
            await deleteRequest(`/highways/${id}`);
            setHighways(highways.filter(h => h.highwayId !== id));
        } catch (err) {
            alert('Failed to delete highway. Active vehicle logs might still be referencing it.');
        }
    };

    const { page, setPage, totalPages, paged: pagedHighways, rangeLabel } = usePagination(highways, 10);

    if (loading) return <LoadingFallback />;

    return (
        <div className="page admin-users-page" style={{ maxWidth: '1400px' }}>
            <h2>🛣️ Highway Management</h2>
            
            {error && (
                <div className="error-message" style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <p>⚠️ {error}</p>
                    <button className="btn btn-secondary btn-sm" onClick={fetchHighways}>Retry</button>
                </div>
            )}

            <div className="card">
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
                    <h3 style={{ margin: 0 }}>National Highways ({highways.length})</h3>
                    <div style={{ display: 'flex', gap: '10px' }}>
                        <button className="admin-btn-add" onClick={handleAddClick}>➕ Add Highway</button>
                        <button className="admin-btn-refresh" onClick={fetchHighways}>🔄 Refresh</button>
                    </div>
                </div>

                <div className="table-responsive">
                    <table className="custom-data-table">
                        <thead>
                            <tr>
                                <th>#</th>
                                <th>Highway Name</th>
                                <th>Start Params (Lat, Lng)</th>
                                <th>End Params (Lat, Lng)</th>
                                <th>CAR Toll</th>
                                <th>BIKE Toll</th>
                                <th>TRUCK Toll</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {highways.length === 0 ? (
                                <tr>
                                    <td colSpan="8" style={{ textAlign: 'center', padding: '30px', color: '#7f8c8d' }}>
                                        No highways mapped. Start configuring toll zones.
                                    </td>
                                </tr>
                            ) : (
                                pagedHighways.map((highway, i) => (
                                    <tr key={highway.highwayId}>
                                        <td><span className="admin-id-pill">#{(page - 1) * 10 + i + 1}</span></td>
                                        <td style={{ fontWeight: '600' }}>{highway.highwayName}</td>
                                        <td style={{ color: '#7f8c8d', fontSize: '13px' }}>
                                            {highway.startLatitude.toFixed(4)}, {highway.startLongitude.toFixed(4)}
                                        </td>
                                        <td style={{ color: '#7f8c8d', fontSize: '13px' }}>
                                            {highway.endLatitude.toFixed(4)}, {highway.endLongitude.toFixed(4)}
                                        </td>
                                        <td style={{ fontWeight: '500', color: '#27ae60' }}>₹{highway.ratePerKmForCar}/km</td>
                                        <td style={{ fontWeight: '500', color: '#f39c12' }}>₹{highway.ratePerKmForBike}/km</td>
                                        <td style={{ fontWeight: '500', color: '#c0392b' }}>₹{highway.ratePerKmForTruck}/km</td>
                                        <td>
                                            <div className="admin-action-group">
                                                <button 
                                                    className="admin-btn-sm admin-btn-edit" 
                                                    onClick={() => handleEditClick(highway)}
                                                    title="Edit Highway"
                                                >
                                                    ✏️ Edit
                                                </button>
                                                <button 
                                                    className="admin-btn-sm admin-btn-delete" 
                                                    onClick={() => handleDelete(highway.highwayId, highway.highwayName)}
                                                    title="Delete Highway"
                                                >
                                                    🗑️ Delete
                                                </button>
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

            {/* ── Highway Add/Edit Modal (Overlay) ── */}
            {showModal && (
                <div style={{
                    position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
                    backgroundColor: 'rgba(0,0,0,0.6)', zIndex: 1000,
                    display: 'flex', alignItems: 'center', justifyContent: 'center',
                    padding: '20px'
                }}>
                    <div className="card" style={{ width: '600px', maxWidth: '100%', margin: 0, maxHeight: '90vh', overflowY: 'auto' }}>
                        <h3 style={{ marginTop: 0 }}>{modalMode === 'add' ? '➕ Add New Highway' : '✏️ Edit Highway Configuration'}</h3>
                        
                        <form onSubmit={handleModalSubmit} className="form" style={{ marginTop: '20px' }}>
                            <div className="form-group" style={{ marginBottom: '16px' }}>
                                <label style={{ display: 'block', marginBottom: '6px', fontWeight: 'bold' }}>Highway Name & Number: *</label>
                                <input 
                                    type="text" 
                                    value={formData.highwayName} 
                                    onChange={e => setFormData({...formData, highwayName: e.target.value.toUpperCase()})} 
                                    style={{ width: '100%', padding: '12px', borderRadius: '6px', border: '1px solid #ddd', textTransform: 'uppercase' }}
                                    placeholder="e.g. NH-44 CHENNAI-BANGALORE"
                                    required
                                    disabled={actionLoading}
                                />
                            </div>

                            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '15px', marginBottom: '15px' }}>
                                <div className="form-group">
                                    <label style={{ display: 'block', marginBottom: '6px', fontWeight: 'bold' }}>Start Latitude: *</label>
                                    <input type="number" step="0.000000001" value={formData.startLatitude} onChange={e => setFormData({...formData, startLatitude: e.target.value})} style={{ width: '100%', padding: '12px', borderRadius: '6px', border: '1px solid #ddd' }} required disabled={actionLoading} />
                                </div>
                                <div className="form-group">
                                    <label style={{ display: 'block', marginBottom: '6px', fontWeight: 'bold' }}>Start Longitude: *</label>
                                    <input type="number" step="0.000000001" value={formData.startLongitude} onChange={e => setFormData({...formData, startLongitude: e.target.value})} style={{ width: '100%', padding: '12px', borderRadius: '6px', border: '1px solid #ddd' }} required disabled={actionLoading} />
                                </div>
                            </div>

                            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '15px', marginBottom: '20px' }}>
                                <div className="form-group">
                                    <label style={{ display: 'block', marginBottom: '6px', fontWeight: 'bold' }}>End Latitude: *</label>
                                    <input type="number" step="0.000000001" value={formData.endLatitude} onChange={e => setFormData({...formData, endLatitude: e.target.value})} style={{ width: '100%', padding: '12px', borderRadius: '6px', border: '1px solid #ddd' }} required disabled={actionLoading} />
                                </div>
                                <div className="form-group">
                                    <label style={{ display: 'block', marginBottom: '6px', fontWeight: 'bold' }}>End Longitude: *</label>
                                    <input type="number" step="0.000000001" value={formData.endLongitude} onChange={e => setFormData({...formData, endLongitude: e.target.value})} style={{ width: '100%', padding: '12px', borderRadius: '6px', border: '1px solid #ddd' }} required disabled={actionLoading} />
                                </div>
                            </div>

                            <div style={{ borderTop: '1px solid #eee', margin: '20px 0', paddingTop: '15px' }}>
                                <h4 style={{ margin: '0 0 15px 0', color: '#34495e' }}>Toll Pricing per Kilometer (₹)</h4>
                                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '15px', marginBottom: '24px' }}>
                                    <div className="form-group">
                                        <label style={{ display: 'block', marginBottom: '6px', fontWeight: 'bold', color: '#27ae60' }}>CAR Rate: *</label>
                                        <input type="number" step="0.01" min="0" value={formData.ratePerKmForCar} onChange={e => setFormData({...formData, ratePerKmForCar: e.target.value})} style={{ width: '100%', padding: '12px', borderRadius: '6px', border: '1px solid #ddd' }} required disabled={actionLoading} />
                                    </div>
                                    <div className="form-group">
                                        <label style={{ display: 'block', marginBottom: '6px', fontWeight: 'bold', color: '#f39c12' }}>BIKE Rate: *</label>
                                        <input type="number" step="0.01" min="0" value={formData.ratePerKmForBike} onChange={e => setFormData({...formData, ratePerKmForBike: e.target.value})} style={{ width: '100%', padding: '12px', borderRadius: '6px', border: '1px solid #ddd' }} required disabled={actionLoading} />
                                    </div>
                                    <div className="form-group">
                                        <label style={{ display: 'block', marginBottom: '6px', fontWeight: 'bold', color: '#c0392b' }}>TRUCK Rate: *</label>
                                        <input type="number" step="0.01" min="0" value={formData.ratePerKmForTruck} onChange={e => setFormData({...formData, ratePerKmForTruck: e.target.value})} style={{ width: '100%', padding: '12px', borderRadius: '6px', border: '1px solid #ddd' }} required disabled={actionLoading} />
                                    </div>
                                </div>
                            </div>

                            <div style={{ display: 'flex', gap: '12px' }}>
                                <button type="submit" className="btn btn-primary" style={{ flex: 1, padding: '12px' }} disabled={actionLoading}>
                                    {actionLoading ? 'Saving Highway Info...' : '💾 Save Highway'}
                                </button>
                                <button type="button" className="btn btn-secondary" style={{ flex: 1, padding: '12px' }} onClick={() => setShowModal(false)} disabled={actionLoading}>
                                    ✕ Cancel
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
}

export default AdminHighways;
