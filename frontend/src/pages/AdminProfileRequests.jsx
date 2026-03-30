import React, { useState, useEffect } from 'react';
import { getRequest, putRequest } from '../services/api';
import { usePagination } from '../hooks/usePagination';
import Paginator from '../components/Paginator';
import './AdminUsersStyles.css';

const STATUS_COLORS = {
    PENDING:  { bg: 'rgba(243,156,18,0.12)', color: '#e67e22' },
    APPROVED: { bg: 'rgba(39,174,96,0.12)',  color: '#27ae60' },
    REJECTED: { bg: 'rgba(231,76,60,0.1)',   color: '#e74c3c' },
};

function AdminProfileRequests({ onCountChange }) {
    const [requests, setRequests] = useState([]);
    const [loading, setLoading] = useState(true);
    const [statusFilter, setStatusFilter] = useState('ALL');
    const [reviewingId, setReviewingId] = useState(null);
    const [adminNotes, setAdminNotes] = useState('');
    const [actionLoading, setActionLoading] = useState(false);

    useEffect(() => { fetchRequests(); }, []);

    const fetchRequests = async () => {
        setLoading(true);
        try {
            const data = await getRequest('/profile-requests');
            setRequests(Array.isArray(data) ? data : []);
            const pending = data.filter(r => r.status === 'PENDING').length;
            if (onCountChange) onCountChange(pending);
        } catch { setRequests([]); }
        finally { setLoading(false); }
    };

    const handleAction = async (id, action) => {
        setActionLoading(true);
        try {
            await putRequest(`/profile-requests/${id}/${action}`, { adminNotes });
            setReviewingId(null);
            setAdminNotes('');
            await fetchRequests();
        } catch { alert(`Failed to ${action} request.`); }
        finally { setActionLoading(false); }
    };

    const filtered = statusFilter === 'ALL' ? requests : requests.filter(r => r.status === statusFilter);
    const { page, setPage, totalPages, paged, rangeLabel } = usePagination(filtered, 10);

    const selectStyle = { padding: '8px 12px', borderRadius: '8px', border: '1px solid #ddd', background: 'white', fontSize: '13px' };

    return (
        <div className="page admin-users-page">
            <h2>🔔 Profile Update Requests</h2>
            <p style={{ color: '#7f8c8d', marginTop: '-10px', marginBottom: '20px' }}>
                Review and approve or reject user-submitted profile change requests.
            </p>

            <div className="card">
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px', flexWrap: 'wrap', gap: '10px' }}>
                    <h3 style={{ margin: 0 }}>All Requests ({filtered.length})</h3>
                    <div style={{ display: 'flex', gap: '10px' }}>
                        <select value={statusFilter} onChange={e => { setStatusFilter(e.target.value); setPage(1); }} style={selectStyle}>
                            <option value="ALL">All</option>
                            <option value="PENDING">⏳ Pending</option>
                            <option value="APPROVED">✅ Approved</option>
                            <option value="REJECTED">❌ Rejected</option>
                        </select>
                        <button className="admin-btn-refresh" onClick={fetchRequests}>🔄 Refresh</button>
                    </div>
                </div>

                {loading ? <p className="info-text">Loading requests...</p>
                    : paged.length === 0 ? (
                        <div style={{ textAlign: 'center', padding: '40px', color: '#7f8c8d' }}>
                            <div style={{ fontSize: '40px' }}>📭</div>
                            <p>No requests found.</p>
                        </div>
                    ) : (
                        <>
                            <div className="table-responsive">
                                <table className="custom-data-table">
                                    <thead>
                                        <tr>
                                            <th>#</th><th>User ID</th>
                                            <th>Current</th><th>Requested</th>
                                            <th>Status</th><th>Submitted</th><th>Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {paged.map((req, i) => {
                                            const s = STATUS_COLORS[req.status] || {};
                                            const isReviewing = reviewingId === req.id;
                                            return (
                                                <React.Fragment key={req.id}>
                                                    <tr>
                                                        <td><span className="admin-id-pill">#{(page-1)*10+i+1}</span></td>
                                                        <td><strong>User #{req.userId}</strong></td>
                                                        <td style={{ fontSize: '12px', color: '#7f8c8d' }}>
                                                            <div>{req.currentName}</div>
                                                            <div>{req.currentEmail}</div>
                                                            <div>{req.currentPhone}</div>
                                                        </td>
                                                        <td style={{ fontSize: '12px', fontWeight: '600' }}>
                                                            {req.requestedName !== req.currentName && <div style={{ color: '#8e44ad' }}>📛 {req.requestedName}</div>}
                                                            {req.requestedEmail !== req.currentEmail && <div style={{ color: '#2980b9' }}>📧 {req.requestedEmail}</div>}
                                                            {req.requestedPhone !== req.currentPhone && <div style={{ color: '#27ae60' }}>📱 {req.requestedPhone}</div>}
                                                        </td>
                                                        <td>
                                                            <span style={{ padding: '3px 10px', borderRadius: '12px', fontSize: '12px', fontWeight: 'bold', background: s.bg, color: s.color }}>
                                                                {req.status}
                                                            </span>
                                                        </td>
                                                        <td style={{ color: '#7f8c8d', fontSize: '12px' }}>
                                                            {new Date(req.createdAt).toLocaleDateString('en-IN')}
                                                        </td>
                                                        <td>
                                                            {req.status === 'PENDING' && (
                                                                <button
                                                                    className="admin-btn-sm admin-btn-edit"
                                                                    onClick={() => { setReviewingId(isReviewing ? null : req.id); setAdminNotes(''); }}>
                                                                    {isReviewing ? '✕ Cancel' : '📝 Review'}
                                                                </button>
                                                            )}
                                                            {req.status !== 'PENDING' && req.adminNotes && (
                                                                <span style={{ fontSize: '12px', color: '#7f8c8d', fontStyle: 'italic' }}>Note: {req.adminNotes}</span>
                                                            )}
                                                        </td>
                                                    </tr>
                                                    {/* Inline review panel */}
                                                    {isReviewing && (
                                                        <tr>
                                                            <td colSpan="7" style={{ background: '#f8f9fa', padding: '16px' }}>
                                                                <div style={{ display: 'flex', gap: '10px', alignItems: 'center', flexWrap: 'wrap' }}>
                                                                    <input
                                                                        type="text" placeholder="Admin notes (optional)"
                                                                        value={adminNotes} onChange={e => setAdminNotes(e.target.value)}
                                                                        style={{ flex: 1, padding: '8px 12px', borderRadius: '8px', border: '1px solid #ddd', minWidth: '200px' }}
                                                                    />
                                                                    <button onClick={() => handleAction(req.id, 'approve')} disabled={actionLoading}
                                                                        style={{ padding: '8px 18px', borderRadius: '8px', border: 'none', background: '#27ae60', color: 'white', fontWeight: '700', cursor: 'pointer' }}>
                                                                        {actionLoading ? '...' : '✅ Approve'}
                                                                    </button>
                                                                    <button onClick={() => handleAction(req.id, 'reject')} disabled={actionLoading}
                                                                        style={{ padding: '8px 18px', borderRadius: '8px', border: 'none', background: '#e74c3c', color: 'white', fontWeight: '700', cursor: 'pointer' }}>
                                                                        {actionLoading ? '...' : '❌ Reject'}
                                                                    </button>
                                                                </div>
                                                            </td>
                                                        </tr>
                                                    )}
                                                </React.Fragment>
                                            );
                                        })}
                                    </tbody>
                                </table>
                            </div>
                            <Paginator page={page} totalPages={totalPages} rangeLabel={rangeLabel} onPageChange={setPage} />
                        </>
                    )}
            </div>
        </div>
    );
}

export default AdminProfileRequests;
