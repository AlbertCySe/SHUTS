import React, { useState } from 'react';
import { postRequest } from '../../services/api';
import { getSession } from '../../services/auth';

const REQUEST_TYPES = [
    { value: 'ADD',        label: '➕ Add New Vehicle',         desc: 'Register a new vehicle under your account' },
    { value: 'DEACTIVATE', label: '🔴 Deactivate Vehicle',      desc: 'Temporarily disable a vehicle from tolling' },
    { value: 'SELL',       label: '🤝 Sell / Transfer Vehicle', desc: 'Transfer ownership to another registered user' },
    { value: 'SCRAP',      label: '🗑️ Scrap Vehicle',          desc: 'Permanently retire a vehicle (irreversible)' },
    { value: 'MODIFY',     label: '✏️ Modify Vehicle Details',  desc: 'Change vehicle number or type' },
];

const VEHICLE_TYPES = ['CAR', 'BIKE', 'TRUCK'];

function VehicleRequestModal({ vehicles, preSelectedVehicle, onClose, onSuccess }) {
    const session = getSession();
    const userId = session?.userId;

    const [requestType, setRequestType] = useState(preSelectedVehicle ? 'DEACTIVATE' : 'ADD');
    const [vehicleId, setVehicleId] = useState(preSelectedVehicle?.vehicleId || '');
    const [form, setForm] = useState({
        requestedVehicleNumber: '', requestedVehicleType: 'CAR',
        newOwnerUserId: '', reason: ''
    });
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState(null);

    const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setMessage(null);

        const payload = {
            userId: String(userId),
            requestType,
            vehicleId: vehicleId ? String(vehicleId) : '',
            reason: form.reason,
            ...(requestType === 'ADD' || requestType === 'MODIFY')
                && { requestedVehicleNumber: form.requestedVehicleNumber, requestedVehicleType: form.requestedVehicleType },
            ...(requestType === 'SELL') && { newOwnerUserId: form.newOwnerUserId },
        };

        // Validation
        if (requestType === 'ADD' && !form.requestedVehicleNumber.trim()) {
            setMessage({ type: 'error', text: 'Please enter a vehicle number.' });
            setLoading(false); return;
        }
        if (['DEACTIVATE', 'SELL', 'SCRAP', 'MODIFY'].includes(requestType) && !vehicleId) {
            setMessage({ type: 'error', text: 'Please select a vehicle.' });
            setLoading(false); return;
        }
        if (requestType === 'SELL' && !form.newOwnerUserId) {
            setMessage({ type: 'error', text: 'Please enter the new owner\'s User ID.' });
            setLoading(false); return;
        }

        try {
            await postRequest('/vehicle-requests', payload);
            setMessage({ type: 'success', text: '✅ Request submitted! Admin will review it shortly.' });
            setTimeout(() => { onSuccess && onSuccess(); onClose(); }, 2000);
        } catch { setMessage({ type: 'error', text: '❌ Failed to submit. Please try again.' }); }
        finally { setLoading(false); }
    };

    const selectedType = REQUEST_TYPES.find(t => t.value === requestType);
    const activeVehicles = vehicles?.filter(v => v.status !== 'SCRAPED') || [];

    return (
        <div style={{ position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.55)', zIndex: 2000, display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '20px' }}>
            <div style={{ background: 'white', borderRadius: '16px', padding: '28px', width: '100%', maxWidth: '500px', boxShadow: '0 20px 60px rgba(0,0,0,0.2)', maxHeight: '90vh', overflowY: 'auto' }}>

                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                    <h3 style={{ margin: 0 }}>🚗 Vehicle Request</h3>
                    <button onClick={onClose} style={{ background: 'none', border: 'none', fontSize: '20px', cursor: 'pointer', color: '#7f8c8d' }}>✕</button>
                </div>

                <form onSubmit={handleSubmit}>
                    {/* Request Type Selector */}
                    <div style={{ marginBottom: '16px' }}>
                        <label style={{ display: 'block', fontWeight: '600', fontSize: '13px', marginBottom: '8px' }}>Request Type *</label>
                        <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                            {REQUEST_TYPES.map(t => (
                                <label key={t.value} style={{
                                    display: 'flex', alignItems: 'flex-start', gap: '10px',
                                    padding: '10px 12px', borderRadius: '8px', cursor: 'pointer',
                                    border: `2px solid ${requestType === t.value ? '#667eea' : '#eee'}`,
                                    background: requestType === t.value ? '#f0f4ff' : 'white',
                                    transition: 'all 0.15s'
                                }}>
                                    <input type="radio" name="requestType" value={t.value}
                                        checked={requestType === t.value}
                                        onChange={() => { setRequestType(t.value); setVehicleId(''); }}
                                        style={{ marginTop: '2px' }} />
                                    <div>
                                        <div style={{ fontWeight: '600', fontSize: '13px' }}>{t.label}</div>
                                        <div style={{ fontSize: '11px', color: '#7f8c8d' }}>{t.desc}</div>
                                    </div>
                                </label>
                            ))}
                        </div>
                    </div>

                    {/* Vehicle Selector (not for ADD) */}
                    {requestType !== 'ADD' && (
                        <div style={{ marginBottom: '14px' }}>
                            <label style={{ display: 'block', fontWeight: '600', fontSize: '13px', marginBottom: '6px' }}>Select Vehicle *</label>
                            <select value={vehicleId} onChange={e => setVehicleId(e.target.value)}
                                style={{ width: '100%', padding: '10px', borderRadius: '8px', border: '1px solid #ddd', fontSize: '14px' }}>
                                <option value="">-- Choose a vehicle --</option>
                                {activeVehicles.map(v => (
                                    <option key={v.vehicleId} value={v.vehicleId}>
                                        {v.vehicleNumber} ({v.vehicleType}) — {v.status}
                                    </option>
                                ))}
                            </select>
                        </div>
                    )}

                    {/* ADD / MODIFY: vehicle number & type */}
                    {(requestType === 'ADD' || requestType === 'MODIFY') && (
                        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px', marginBottom: '14px' }}>
                            <div>
                                <label style={{ display: 'block', fontWeight: '600', fontSize: '13px', marginBottom: '6px' }}>
                                    {requestType === 'ADD' ? 'Vehicle Number *' : 'New Vehicle Number'}
                                </label>
                                <input name="requestedVehicleNumber" value={form.requestedVehicleNumber}
                                    onChange={handleChange} placeholder="e.g. TN01AB1234"
                                    style={{ width: '100%', padding: '10px', borderRadius: '8px', border: '1px solid #ddd', fontSize: '13px', textTransform: 'uppercase', boxSizing: 'border-box' }} />
                            </div>
                            <div>
                                <label style={{ display: 'block', fontWeight: '600', fontSize: '13px', marginBottom: '6px' }}>Vehicle Type</label>
                                <select name="requestedVehicleType" value={form.requestedVehicleType} onChange={handleChange}
                                    style={{ width: '100%', padding: '10px', borderRadius: '8px', border: '1px solid #ddd', fontSize: '13px' }}>
                                    {VEHICLE_TYPES.map(t => <option key={t}>{t}</option>)}
                                </select>
                            </div>
                        </div>
                    )}

                    {/* SELL: new owner */}
                    {requestType === 'SELL' && (
                        <div style={{ marginBottom: '14px' }}>
                            <label style={{ display: 'block', fontWeight: '600', fontSize: '13px', marginBottom: '6px' }}>New Owner's User ID *</label>
                            <input name="newOwnerUserId" value={form.newOwnerUserId} onChange={handleChange}
                                type="number" placeholder="Enter the buyer's User ID"
                                style={{ width: '100%', padding: '10px', borderRadius: '8px', border: '1px solid #ddd', fontSize: '14px', boxSizing: 'border-box' }} />
                        </div>
                    )}

                    {/* Reason (always shown) */}
                    <div style={{ marginBottom: '18px' }}>
                        <label style={{ display: 'block', fontWeight: '600', fontSize: '13px', marginBottom: '6px' }}>Reason / Notes</label>
                        <textarea name="reason" value={form.reason} onChange={handleChange} rows={2}
                            placeholder="Optional but helpful for admin review..."
                            style={{ width: '100%', padding: '10px', borderRadius: '8px', border: '1px solid #ddd', fontSize: '13px', resize: 'vertical', boxSizing: 'border-box' }} />
                    </div>

                    {/* SCRAP warning */}
                    {requestType === 'SCRAP' && (
                        <div style={{ padding: '10px 14px', background: '#fff3cd', border: '1px solid #ffc107', borderRadius: '8px', marginBottom: '14px', fontSize: '13px', color: '#856404' }}>
                            ⚠️ <strong>Irreversible:</strong> Scrapping permanently retires the vehicle. It cannot be reactivated.
                        </div>
                    )}

                    {message && (
                        <div style={{
                            padding: '10px 14px', borderRadius: '8px', marginBottom: '14px', fontSize: '13px',
                            background: message.type === 'success' ? '#eafaf1' : '#fef0f0',
                            color: message.type === 'success' ? '#27ae60' : '#e74c3c',
                            border: `1px solid ${message.type === 'success' ? '#a9dfbf' : '#f5c6cb'}`
                        }}>{message.text}</div>
                    )}

                    <div style={{ display: 'flex', gap: '10px' }}>
                        <button type="submit" disabled={loading} className="btn btn-primary" style={{ flex: 1, padding: '12px' }}>
                            {loading ? 'Submitting...' : '📤 Submit Request'}
                        </button>
                        <button type="button" onClick={onClose} style={{ flex: 1, padding: '12px', borderRadius: '8px', border: '1px solid #ddd', background: 'white', cursor: 'pointer', fontWeight: '600' }}>
                            Cancel
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default VehicleRequestModal;
