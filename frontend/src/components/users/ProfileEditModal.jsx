import React, { useState, useEffect } from 'react';
import { postRequest } from '../../services/api';

function ProfileEditModal({ show, initialData, onClose, onSuccess }) {
    const [formData, setFormData] = useState({
        newName: '',
        newEmail: '',
        newPhoneNumber: ''
    });

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    // Reset form when modal opens
    useEffect(() => {
        if (show) {
            setFormData({
                newName: initialData?.name || '',
                newEmail: initialData?.email || '',
                newPhoneNumber: initialData?.phoneNumber || ''
            });
            setError(null);
        }
    }, [show, initialData]);

    if (!show) return null;

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);
        setLoading(true);

        // Check if anything actually changed
        if (
            formData.newName === initialData?.name &&
            formData.newEmail === initialData?.email &&
            formData.newPhoneNumber === initialData?.phoneNumber
        ) {
            setError('No changes detected.');
            setLoading(false);
            return;
        }

        try {
            const payload = {
                userId: initialData?.userId,
                newName: formData.newName !== initialData?.name ? formData.newName : '',
                newEmail: formData.newEmail !== initialData?.email ? formData.newEmail : '',
                newPhoneNumber: formData.newPhoneNumber !== initialData?.phoneNumber ? formData.newPhoneNumber : ''
            };

            await postRequest('/profile-requests', payload);
            
            setLoading(false);
            onSuccess(); // Close and show success message in parent
        } catch (err) {
            console.error('Failed to submit profile request:', err);
            setError(err.response?.data || 'Failed to submit the request. Please try again.');
            setLoading(false);
        }
    };

    return (
        <div style={{ position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.55)', zIndex: 2000, display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '20px' }}>
            <div style={{ background: 'white', borderRadius: '16px', padding: '28px', width: '100%', maxWidth: '450px', boxShadow: '0 20px 60px rgba(0,0,0,0.2)', maxHeight: '90vh', overflowY: 'auto' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                    <h2 style={{ margin: 0, fontSize: '20px' }}>✏️ Request Profile Update</h2>
                    <button type="button" onClick={onClose} style={{ background: 'none', border: 'none', fontSize: '20px', cursor: 'pointer', color: '#7f8c8d' }}>✕</button>
                </div>

                <div>
                    <p style={{ fontSize: '13px', color: '#7f8c8d', marginBottom: '20px', lineHeight: '1.5' }}>
                        For security reasons, profile updates require admin approval. 
                        Submit your requested changes below, and you will be notified once reviewed.
                    </p>

                    {error && (
                        <div style={{ padding: '10px 14px', borderRadius: '8px', marginBottom: '14px', fontSize: '13px', background: '#fef0f0', color: '#e74c3c', border: '1px solid #f5c6cb' }}>
                            <p style={{ margin: 0 }}>⚠️ {error}</p>
                        </div>
                    )}

                    <form onSubmit={handleSubmit}>
                        <div style={{ marginBottom: '16px' }}>
                            <label style={{ display: 'block', fontWeight: '600', fontSize: '13px', marginBottom: '6px' }}>Full Name</label>
                            <input 
                                type="text" 
                                value={formData.newName} 
                                onChange={e => setFormData({ ...formData, newName: e.target.value })} 
                                placeholder="Enter your full name"
                                disabled={loading}
                                style={{ width: '100%', padding: '10px', borderRadius: '8px', border: '1px solid #ddd', fontSize: '13px', boxSizing: 'border-box' }}
                            />
                        </div>

                        <div style={{ marginBottom: '16px' }}>
                            <label style={{ display: 'block', fontWeight: '600', fontSize: '13px', marginBottom: '6px' }}>Email Address</label>
                            <input 
                                type="email" 
                                value={formData.newEmail} 
                                onChange={e => setFormData({ ...formData, newEmail: e.target.value })} 
                                placeholder="Enter new email"
                                disabled={loading}
                                style={{ width: '100%', padding: '10px', borderRadius: '8px', border: '1px solid #ddd', fontSize: '13px', boxSizing: 'border-box' }}
                            />
                        </div>

                        <div style={{ marginBottom: '20px' }}>
                            <label style={{ display: 'block', fontWeight: '600', fontSize: '13px', marginBottom: '6px' }}>Phone Number</label>
                            <input 
                                type="tel" 
                                value={formData.newPhoneNumber} 
                                onChange={e => setFormData({ ...formData, newPhoneNumber: e.target.value })} 
                                placeholder="Enter new phone number"
                                disabled={loading}
                                style={{ width: '100%', padding: '10px', borderRadius: '8px', border: '1px solid #ddd', fontSize: '13px', boxSizing: 'border-box' }}
                            />
                        </div>

                        <div style={{ display: 'flex', gap: '10px', justifyContent: 'flex-end', marginTop: '24px' }}>
                            <button type="button" className="btn btn-secondary" onClick={onClose} disabled={loading} style={{ flex: 1, padding: '12px', borderRadius: '8px', border: '1px solid #ddd', background: 'white', cursor: 'pointer', fontWeight: '600' }}>
                                Cancel
                            </button>
                            <button type="submit" className="btn btn-primary" disabled={loading} style={{ flex: 1, padding: '12px', background: '#3498db', color: 'white', border: 'none', borderRadius: '8px', fontWeight: '600', cursor: 'pointer' }}>
                                {loading ? 'Submitting...' : 'Submit Request'}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
}

export default ProfileEditModal;
