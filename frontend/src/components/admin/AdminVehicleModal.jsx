import React, { useState, useEffect } from 'react';
import { postRequest, putRequest } from '../../services/api';

function AdminVehicleModal({ show, mode, initialData, onClose, onSuccess }) {
    const [formData, setFormData] = useState({ vehicleId: null, vehicleNumber: '', vehicleType: 'CAR', userId: '' });
    const [actionLoading, setActionLoading] = useState(false);

    useEffect(() => {
        if (show) {
            setFormData(initialData || { vehicleId: null, vehicleNumber: '', vehicleType: 'CAR', userId: '' });
        }
    }, [show, initialData]);

    if (!show) return null;

    const handleSubmit = async (e) => {
        e.preventDefault();
        setActionLoading(true);
        try {
            if (mode === 'add') {
                const vehicleData = {
                    vehicleNumber: formData.vehicleNumber,
                    vehicleType: formData.vehicleType
                };
                await postRequest(`/users/${formData.userId}/vehicles`, vehicleData);
            } else {
                const vehicleData = {
                    vehicleNumber: formData.vehicleNumber,
                    vehicleType: formData.vehicleType,
                    user: { userId: formData.userId }
                };
                await putRequest(`/vehicles/${formData.vehicleId}`, vehicleData);
            }
            onSuccess();
            onClose();
        } catch (err) {
            alert(`Failed to ${mode} vehicle. Please verify the Owner ID exists and try again.`);
        } finally {
            setActionLoading(false);
        }
    };

    return (
        <div style={{
            position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
            backgroundColor: 'rgba(0,0,0,0.6)', zIndex: 1000,
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            padding: '20px'
        }}>
            <div className="card" style={{ width: '450px', maxWidth: '100%', margin: 0, maxHeight: '90vh', overflowY: 'auto' }}>
                <h3 style={{ marginTop: 0 }}>{mode === 'add' ? '➕ Register New Vehicle' : '✏️ Edit Vehicle Details'}</h3>
                
                <form onSubmit={handleSubmit} className="form" style={{ marginTop: '20px' }}>
                    <div className="form-group" style={{ marginBottom: '16px' }}>
                        <label style={{ display: 'block', marginBottom: '6px', fontWeight: 'bold' }}>Registration Number: *</label>
                        <input 
                            type="text" 
                            value={formData.vehicleNumber} 
                            onChange={e => setFormData({...formData, vehicleNumber: e.target.value.toUpperCase()})} 
                            style={{ width: '100%', padding: '12px', borderRadius: '6px', border: '1px solid #ddd', textTransform: 'uppercase' }}
                            placeholder="e.g. MH01AB1234"
                            required
                            disabled={actionLoading}
                        />
                    </div>

                    <div className="form-group" style={{ marginBottom: '16px' }}>
                        <label style={{ display: 'block', marginBottom: '6px', fontWeight: 'bold' }}>Vehicle Type: *</label>
                        <select
                            value={formData.vehicleType}
                            onChange={e => setFormData({...formData, vehicleType: e.target.value})}
                            style={{ width: '100%', padding: '12px', borderRadius: '6px', border: '1px solid #ddd', backgroundColor: 'white' }}
                            required
                            disabled={actionLoading}
                        >
                            <option value="CAR">Car</option>
                            <option value="BIKE">Bike</option>
                            <option value="BUS">Bus</option>
                            <option value="TRUCK">Truck</option>
                            <option value="COMMERCIAL">Commercial</option>
                        </select>
                    </div>

                    <div className="form-group" style={{ marginBottom: '24px' }}>
                        <label style={{ display: 'block', marginBottom: '6px', fontWeight: 'bold' }}>Owner ID (User ID): *</label>
                        <input 
                            type="number" 
                            value={formData.userId} 
                            onChange={e => setFormData({...formData, userId: e.target.value})} 
                            style={{ width: '100%', padding: '12px', borderRadius: '6px', border: '1px solid #ddd' }}
                            placeholder="Enter numeric User ID"
                            required
                            disabled={actionLoading || mode === 'edit'}
                        />
                        {mode === 'edit' && (
                            <small style={{ color: '#7f8c8d', display: 'block', marginTop: '4px' }}>
                                Vehicle ownership transfer is currently disabled.
                            </small>
                        )}
                    </div>

                    <div style={{ display: 'flex', gap: '12px' }}>
                        <button type="submit" className="btn btn-primary" style={{ flex: 1, padding: '12px' }} disabled={actionLoading}>
                            {actionLoading ? 'Saving...' : '💾 Save Details'}
                        </button>
                        <button type="button" className="btn btn-secondary" style={{ flex: 1, padding: '12px' }} onClick={onClose} disabled={actionLoading}>
                            ✕ Cancel
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default AdminVehicleModal;
