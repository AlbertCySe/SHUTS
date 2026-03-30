import React, { useState } from 'react';
import { postRequest } from '../../services/api';
import { getSession } from '../../services/auth';

function ProfileRequestModal({ onClose }) {
    const session = getSession();
    const userId = session?.userId;

    const [form, setForm] = useState({ requestedName: '', requestedEmail: '', requestedPhone: '' });
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState(null);

    const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!form.requestedName && !form.requestedEmail && !form.requestedPhone) {
            setMessage({ type: 'error', text: 'Please fill in at least one field to update.' });
            return;
        }
        setLoading(true);
        try {
            await postRequest('/profile-requests', { userId: String(userId), ...form });
            setMessage({ type: 'success', text: '✅ Request submitted! Admin will review it shortly.' });
            setTimeout(onClose, 2500);
        } catch {
            setMessage({ type: 'error', text: '❌ Failed to submit. Please try again.' });
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={{
            position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.55)',
            zIndex: 2000, display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '20px'
        }}>
            <div style={{
                background: 'white', borderRadius: '16px', padding: '32px',
                width: '100%', maxWidth: '480px', boxShadow: '0 20px 60px rgba(0,0,0,0.2)'
            }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                    <h3 style={{ margin: 0 }}>👤 Request Profile Update</h3>
                    <button onClick={onClose} style={{ background: 'none', border: 'none', fontSize: '20px', cursor: 'pointer', color: '#7f8c8d' }}>✕</button>
                </div>

                <p style={{ color: '#7f8c8d', fontSize: '14px', marginBottom: '20px' }}>
                    Fill in only the fields you want to change. Leave others blank to keep your current values.
                </p>

                <form onSubmit={handleSubmit}>
                    {[
                        { label: '📛 New Name', name: 'requestedName', placeholder: 'Enter new name' },
                        { label: '📧 New Email', name: 'requestedEmail', placeholder: 'Enter new email', type: 'email' },
                        { label: '📱 New Phone', name: 'requestedPhone', placeholder: 'Enter new phone number' },
                    ].map(f => (
                        <div key={f.name} style={{ marginBottom: '16px' }}>
                            <label style={{ display: 'block', fontWeight: '600', fontSize: '13px', marginBottom: '6px' }}>{f.label}</label>
                            <input
                                type={f.type || 'text'} name={f.name}
                                placeholder={f.placeholder} value={form[f.name]}
                                onChange={handleChange}
                                style={{ width: '100%', padding: '10px 14px', borderRadius: '8px', border: '1px solid #ddd', fontSize: '14px', boxSizing: 'border-box' }}
                            />
                        </div>
                    ))}

                    {message && (
                        <div style={{
                            padding: '10px 14px', borderRadius: '8px', marginBottom: '16px', fontSize: '13px',
                            background: message.type === 'success' ? '#eafaf1' : '#fef0f0',
                            color: message.type === 'success' ? '#27ae60' : '#e74c3c',
                            border: `1px solid ${message.type === 'success' ? '#a9dfbf' : '#f5c6cb'}`
                        }}>
                            {message.text}
                        </div>
                    )}

                    <div style={{ display: 'flex', gap: '10px' }}>
                        <button type="submit" disabled={loading} className="btn btn-primary" style={{ flex: 1, padding: '12px' }}>
                            {loading ? 'Submitting...' : '📤 Submit Request'}
                        </button>
                        <button type="button" onClick={onClose} style={{
                            flex: 1, padding: '12px', borderRadius: '8px', border: '1px solid #ddd',
                            background: 'white', cursor: 'pointer', fontWeight: '600'
                        }}>Cancel</button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default ProfileRequestModal;
