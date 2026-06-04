import React, { useState, useEffect } from 'react';
import { getRequest, postRequest } from '../services/api';
import './AdminUsersStyles.css';

function AdminWallets() {
    const [requests, setRequests] = useState([]);
    const [loading, setLoading] = useState(true);
    const [actionLoading, setActionLoading] = useState(null);

    // Manual Top Up State
    const [topupUserId, setTopupUserId] = useState('');
    const [topupAmount, setTopupAmount] = useState('');
    const [topupLoading, setTopupLoading] = useState(false);

    useEffect(() => {
        fetchRequests();
    }, []);

    const fetchRequests = async () => {
        setLoading(true);
        try {
            const data = await getRequest('/admin/wallets/recharge-requests');
            setRequests(data || []);
        } catch (e) {
            console.error(e);
        } finally {
            setLoading(false);
        }
    };

    const handleAction = async (id, action) => {
        if (!window.confirm(`Are you sure you want to ${action} this request?`)) return;
        setActionLoading(id);
        try {
            const res = await postRequest(`/admin/wallets/recharge-requests/${id}/${action}`, {});
            if (res.success) {
                alert(`Request ${action}d successfully`);
                fetchRequests();
            } else {
                alert(res.message || 'Failed to process request');
            }
        } catch (e) {
            alert('Error processing request.');
        } finally {
            setActionLoading(null);
        }
    };

    const handleManualTopup = async (e) => {
        e.preventDefault();
        if (!topupUserId || !topupAmount || topupAmount <= 0) {
            alert('Please enter valid User ID and Amount');
            return;
        }
        setTopupLoading(true);
        try {
            const res = await postRequest(`/admin/wallets/user/${topupUserId}/topup`, { amount: topupAmount });
            if (res.success) {
                alert(res.message);
                setTopupUserId('');
                setTopupAmount('');
            } else {
                alert(res.message || 'Failed to top up wallet');
            }
        } catch (e) {
            alert(e.response?.data?.message || 'Error executing manual top up.');
        } finally {
            setTopupLoading(false);
        }
    };

    return (
        <div className="page admin-users-page">
            <h2>💳 Wallet Management</h2>
            <p style={{ color: '#7f8c8d', marginTop: '-10px', marginBottom: '20px' }}>
                Approve simulated UPI payments and perform manual wallet top-ups.
            </p>

            {/* Manual Top Up Form */}
            <div className="card" style={{ marginBottom: '24px', background: '#f8f9fa' }}>
                <h3 style={{ margin: '0 0 16px 0', fontSize: '16px' }}>⚡ Manual Wallet Top-Up</h3>
                <form onSubmit={handleManualTopup} style={{ display: 'flex', gap: '12px', alignItems: 'center', flexWrap: 'wrap' }}>
                    <div className="form-group" style={{ marginBottom: 0 }}>
                        <input type="number" placeholder="User ID" value={topupUserId} onChange={e => setTopupUserId(e.target.value)} required style={{ padding: '8px', width: '120px' }} />
                    </div>
                    <div className="form-group" style={{ marginBottom: 0 }}>
                        <input type="number" placeholder="Amount (₹)" value={topupAmount} onChange={e => setTopupAmount(e.target.value)} required style={{ padding: '8px', width: '150px' }} />
                    </div>
                    <button type="submit" className="btn btn-primary" disabled={topupLoading}>
                        {topupLoading ? 'Processing...' : 'Add Funds'}
                    </button>
                </form>
            </div>

            {/* Pending Requests Table */}
            <div className="card">
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
                    <h3 style={{ margin: 0 }}>📥 Pending Recharge Requests</h3>
                    <button className="btn btn-secondary" onClick={fetchRequests} style={{ padding: '6px 12px', fontSize: '13px' }}>🔄 Refresh</button>
                </div>

                {loading ? <p className="info-text">Loading requests...</p> : 
                    requests.length === 0 ? (
                        <div style={{ textAlign: 'center', padding: '30px', color: '#7f8c8d' }}>
                            <div style={{ fontSize: '40px' }}>🙌</div>
                            <p>No pending recharge requests!</p>
                        </div>
                    ) : (
                    <div className="table-responsive">
                        <table className="custom-data-table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>User</th>
                                    <th>Amount</th>
                                    <th>UPI Ref</th>
                                    <th>Date</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                {requests.map(req => (
                                    <tr key={req.requestId}>
                                        <td><span className="admin-id-pill">#{req.requestId}</span></td>
                                        <td>{req.user?.name} (ID: {req.user?.userId})</td>
                                        <td style={{ fontWeight: 'bold', color: '#27ae60' }}>₹{parseFloat(req.amount).toFixed(2)}</td>
                                        <td><span style={{ background: '#eee', padding: '2px 6px', borderRadius: '4px', fontSize: '12px' }}>{req.upiReference}</span></td>
                                        <td style={{ fontSize: '13px', color: '#555' }}>
                                            {new Date(req.requestDate).toLocaleString('en-IN')}
                                        </td>
                                        <td>
                                            <div style={{ display: 'flex', gap: '8px' }}>
                                                <button 
                                                    onClick={() => handleAction(req.requestId, 'approve')}
                                                    disabled={actionLoading === req.requestId}
                                                    style={{ background: '#2ecc71', color: 'white', border: 'none', padding: '6px 12px', borderRadius: '4px', cursor: 'pointer', fontSize: '12px', fontWeight: 'bold' }}>
                                                    ✅ Approve
                                                </button>
                                                <button 
                                                    onClick={() => handleAction(req.requestId, 'decline')}
                                                    disabled={actionLoading === req.requestId}
                                                    style={{ background: '#e74c3c', color: 'white', border: 'none', padding: '6px 12px', borderRadius: '4px', cursor: 'pointer', fontSize: '12px', fontWeight: 'bold' }}>
                                                    ❌ Decline
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>
        </div>
    );
}

export default AdminWallets;
