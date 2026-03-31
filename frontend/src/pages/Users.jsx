import React, { useState, useEffect } from 'react';
import { getRequest } from '../services/api';
import { getSession } from '../services/auth';
import ProfileEditModal from '../components/users/ProfileEditModal';
import './AdminUsersStyles.css'; // Reuse existing table styles

function Users() {
    const [user, setUser] = useState(null);
    const [requests, setRequests] = useState([]);
    
    const [loading, setLoading] = useState(true);
    const [reqLoading, setReqLoading] = useState(true);
    const [error, setError] = useState(null);

    const [showEditModal, setShowEditModal] = useState(false);
    const [successMessage, setSuccessMessage] = useState('');

    const session = getSession();
    const userId = session?.userId;

    useEffect(() => {
        if (userId) {
            fetchUserProfile();
            fetchProfileRequests();
        } else {
            setError('No active user session found.');
            setLoading(false);
            setReqLoading(false);
        }
    }, [userId]);

    const fetchUserProfile = async () => {
        setLoading(true);
        try {
            const data = await getRequest(`/users/${userId}`);
            setUser(data);
        } catch (err) {
            console.error('Failed to fetch user profile:', err);
            setError('Failed to load profile details.');
        } finally {
            setLoading(false);
        }
    };

    const fetchProfileRequests = async () => {
        setReqLoading(true);
        try {
            const data = await getRequest(`/profile-requests/user/${userId}`);
            setRequests(Array.isArray(data) ? data : []);
        } catch (err) {
            console.error('Failed to fetch profile requests:', err);
            setRequests([]);
        } finally {
            setReqLoading(false);
        }
    };

    const handleModalSuccess = () => {
        setShowEditModal(false);
        setSuccessMessage('✅ Profile update request submitted successfully. It is now pending admin review.');
        fetchProfileRequests(); // Refresh requests table
        
        setTimeout(() => setSuccessMessage(''), 6000);
    };

    if (loading) {
        return (
            <div className="page admin-users-page">
                <div className="card" style={{ textAlign: 'center', padding: '40px' }}>
                    <p style={{ color: '#7f8c8d' }}>Loading profile...</p>
                </div>
            </div>
        );
    }

    if (error || !user) {
        return (
            <div className="page admin-users-page">
                <div className="card" style={{ textAlign: 'center', padding: '40px' }}>
                    <p style={{ color: '#e74c3c' }}>{error || 'Profile not found.'}</p>
                </div>
            </div>
        );
    }

    return (
        <div className="page admin-users-page">
            <h2>👤 My Profile Dashboard</h2>

            {successMessage && (
                <div className="success-message" style={{ marginBottom: '20px' }}>
                    <p>{successMessage}</p>
                </div>
            )}

            {/* Profile Overview Card */}
            <div className="card" style={{ marginBottom: '20px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '15px' }}>
                    <div>
                        <h3 style={{ marginBottom: '15px', color: '#2c3e50', fontSize: '20px' }}>Profile Details</h3>
                        <p style={{ marginBottom: '8px' }}><strong>Name:</strong> {user.name}</p>
                        <p style={{ marginBottom: '8px' }}><strong>Email:</strong> {user.email}</p>
                        <p style={{ marginBottom: '8px' }}><strong>Phone:</strong> {user.phoneNumber}</p>
                        <p style={{ marginBottom: '0', color: '#7f8c8d', fontSize: '13px' }}>
                            <strong>Member Since:</strong> {new Date(user.createdAt).toLocaleDateString()}
                        </p>
                    </div>

                    <div style={{ alignSelf: 'flex-start' }}>
                        <button 
                            className="btn btn-primary" 
                            style={{ display: 'flex', alignItems: 'center', gap: '8px', padding: '10px 16px' }}
                            onClick={() => setShowEditModal(true)}
                        >
                            <span>✏️</span> Request Profile Update
                        </button>
                    </div>
                </div>
            </div>

            {/* Requests History Card */}
            <div className="card">
                <h3 style={{ marginBottom: '15px', color: '#2c3e50', fontSize: '18px' }}>📋 My Update Requests</h3>
                
                {reqLoading ? (
                    <p style={{ color: '#7f8c8d', fontSize: '14px', textAlign: 'center', padding: '20px' }}>Loading requests...</p>
                ) : requests.length === 0 ? (
                    <p style={{ color: '#7f8c8d', fontSize: '14px', textAlign: 'center', padding: '20px', backgroundColor: '#f9f9f9', borderRadius: '8px' }}>
                        You have not submitted any profile update requests yet.
                    </p>
                ) : (
                    <div className="table-container">
                        <table className="data-table">
                            <thead>
                                <tr>
                                    <th>Date</th>
                                    <th>Requested Changes</th>
                                    <th>Status</th>
                                    <th>Admin Notes</th>
                                </tr>
                            </thead>
                            <tbody>
                                {requests.map(req => {
                                    // Identify what was actually requested
                                    const changes = [];
                                    if (req.newName) changes.push(`Name → ${req.newName}`);
                                    if (req.newEmail) changes.push(`Email → ${req.newEmail}`);
                                    if (req.newPhoneNumber) changes.push(`Phone → ${req.newPhoneNumber}`);

                                    return (
                                        <tr key={req.id}>
                                            <td style={{ fontSize: '13px' }}>
                                                {new Date(req.createdAt).toLocaleDateString()}<br/>
                                                <span style={{ color: '#7f8c8d' }}>
                                                    {new Date(req.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                                                </span>
                                            </td>
                                            <td>
                                                {changes.length > 0 ? (
                                                    <ul style={{ margin: '0', paddingLeft: '16px', fontSize: '13px' }}>
                                                        {changes.map((c, i) => <li key={i}>{c}</li>)}
                                                    </ul>
                                                ) : <span style={{ color: '#95a5a6', fontStyle: 'italic' }}>No changes</span>}
                                            </td>
                                            <td>
                                                <span className={`status-badge status-${req.status.toLowerCase()}`}>
                                                    {req.status}
                                                </span>
                                            </td>
                                            <td style={{ fontSize: '13px', color: req.status === 'REJECTED' ? '#c0392b' : '#34495e', maxWidth: '300px' }}>
                                                {req.adminNotes || <span style={{ color: '#bdc3c7', fontStyle: 'italic' }}>None</span>}
                                            </td>
                                        </tr>
                                    );
                                })}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>

            <ProfileEditModal 
                show={showEditModal} 
                initialData={user}
                onClose={() => setShowEditModal(false)}
                onSuccess={handleModalSuccess}
            />
        </div>
    );
}

export default Users;
