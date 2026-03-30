import React, { useState, useEffect } from 'react';
import { getRequest, putRequest } from '../../services/api';

const TYPE_LABELS = { ADD: '➕ Add', DEACTIVATE: '🔴 Deactivate', SELL: '🤝 Sell', SCRAP: '🗑️ Scrap', MODIFY: '✏️ Modify' };

function AdminVehicleRequests({ refreshTrigger, onActionCompleted }) {
    const [vRequests, setVRequests] = useState([]);
    const [vReqLoading, setVReqLoading] = useState(true);
    const [showRequests, setShowRequests] = useState(true);
    const [reviewingReqId, setReviewingReqId] = useState(null);
    const [reqNotes, setReqNotes] = useState('');
    const [reqActionLoading, setReqActionLoading] = useState(false);
    const [error, setError] = useState(null);

    useEffect(() => {
        fetchVehicleRequests();
    }, [refreshTrigger]);

    const fetchVehicleRequests = async () => {
        setVReqLoading(true);
        setError(null);
        try {
            const data = await getRequest('/vehicle-requests');
            setVRequests(Array.isArray(data) ? data : []);
        } catch (err) { 
            setVRequests([]);
            setError('Could not fetch requests. Is the backend rebuilt?');
        }
        finally { setVReqLoading(false); }
    };

    const handleAction = async (id, action) => {
        setReqActionLoading(true);
        try {
            await putRequest(`/vehicle-requests/${id}/${action}`, { adminNotes: reqNotes });
            setReviewingReqId(null);
            setReqNotes('');
            if (onActionCompleted) onActionCompleted();
        } catch { alert(`Failed to ${action} request.`); }
        finally { setReqActionLoading(false); }
    };

    const pendingVReqs = vRequests.filter(r => r.status === 'PENDING');

    return (
        <div className="card" style={{ marginBottom: '20px' }}>
            <div onClick={() => setShowRequests(!showRequests)}
                style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', cursor: 'pointer', marginBottom: showRequests ? '14px' : 0 }}>
                <h3 style={{ margin: 0 }}>
                    📋 Pending Vehicle Requests
                    {pendingVReqs.length > 0 && (
                        <span style={{ marginLeft: '8px', background: '#e74c3c', color: 'white', borderRadius: '10px', fontSize: '12px', padding: '2px 8px', fontWeight: '800' }}>
                            {pendingVReqs.length}
                        </span>
                    )}
                </h3>
                <span style={{ color: '#7f8c8d', fontSize: '13px' }}>{showRequests ? '▲ Collapse' : '▼ Expand'}</span>
            </div>

            {showRequests && (
                vReqLoading ? <p className="info-text">Loading requests...</p>
                : error ? <p style={{ color: '#e74c3c', fontSize: '13px', textAlign: 'center', padding: '16px' }}>❌ {error}</p>
                : pendingVReqs.length === 0 ? (
                    <p style={{ color: '#7f8c8d', fontSize: '13px', textAlign: 'center', padding: '16px' }}>✅ No pending vehicle requests.</p>
                ) : (
                    <div className="table-responsive">
                        <table className="custom-data-table">
                            <thead>
                                <tr><th>#</th><th>User ID</th><th>Type</th><th>Details</th><th>Reason</th><th>Date</th><th>Actions</th></tr>
                            </thead>
                            <tbody>
                                {pendingVReqs.map((req, i) => (
                                    <React.Fragment key={req.id}>
                                        <tr>
                                            <td><span className="admin-id-pill">#{i + 1}</span></td>
                                            <td><strong>User #{req.userId}</strong></td>
                                            <td><span style={{ fontWeight: '700', color: '#8e44ad' }}>{TYPE_LABELS[req.requestType]}</span></td>
                                            <td style={{ fontSize: '12px' }}>
                                                {req.requestType === 'ADD' && <span>🚗 {req.requestedVehicleNumber} ({req.requestedVehicleType})</span>}
                                                {req.requestType === 'SELL' && <span>Vehicle #{req.vehicleId} → User #{req.newOwnerUserId}</span>}
                                                {req.requestType === 'MODIFY' && <span>Vehicle #{req.vehicleId} → {req.requestedVehicleNumber} ({req.requestedVehicleType})</span>}
                                                {['DEACTIVATE','SCRAP'].includes(req.requestType) && <span>Vehicle #{req.vehicleId}</span>}
                                            </td>
                                            <td style={{ color: '#7f8c8d', fontSize: '12px' }}>{req.reason || '—'}</td>
                                            <td style={{ color: '#7f8c8d', fontSize: '12px' }}>{new Date(req.createdAt).toLocaleDateString('en-IN')}</td>
                                            <td>
                                                <button className="admin-btn-sm admin-btn-edit"
                                                    onClick={() => { setReviewingReqId(reviewingReqId === req.id ? null : req.id); setReqNotes(''); }}>
                                                    {reviewingReqId === req.id ? '✕ Cancel' : '📝 Review'}
                                                </button>
                                            </td>
                                        </tr>
                                        {reviewingReqId === req.id && (
                                            <tr key={`review-${req.id}`}>
                                                <td colSpan="7" style={{ background: '#f8f9fa', padding: '12px 16px' }}>
                                                    <div style={{ display: 'flex', gap: '10px', flexWrap: 'wrap', alignItems: 'center' }}>
                                                        <input type="text" placeholder="Admin notes (optional)"
                                                            value={reqNotes} onChange={e => setReqNotes(e.target.value)}
                                                            style={{ flex: 1, padding: '8px 12px', borderRadius: '8px', border: '1px solid #ddd', minWidth: '180px' }} />
                                                        <button onClick={() => handleAction(req.id, 'approve')} disabled={reqActionLoading}
                                                            style={{ padding: '8px 18px', borderRadius: '8px', border: 'none', background: '#27ae60', color: 'white', fontWeight: '700', cursor: 'pointer' }}>
                                                            {reqActionLoading ? '...' : '✅ Approve'}
                                                        </button>
                                                        <button onClick={() => handleAction(req.id, 'reject')} disabled={reqActionLoading}
                                                            style={{ padding: '8px 18px', borderRadius: '8px', border: 'none', background: '#e74c3c', color: 'white', fontWeight: '700', cursor: 'pointer' }}>
                                                            {reqActionLoading ? '...' : '❌ Reject'}
                                                        </button>
                                                    </div>
                                                </td>
                                            </tr>
                                        )}
                                    </React.Fragment>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )
            )}
        </div>
    );
}

export default AdminVehicleRequests;
