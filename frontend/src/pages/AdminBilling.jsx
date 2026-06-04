import { useState, useEffect } from 'react';
import { getRequest, postRequest } from '../services/api';
import './AdminBillingStyles.css';

function AdminBilling() {
    const [users, setUsers] = useState([]);
    const [vehicles, setVehicles] = useState([]);
    const [recentBills, setRecentBills] = useState([]);
    const [loading, setLoading] = useState(true);

    // Selection State
    const [selectedUser, setSelectedUser] = useState(null);
    const [selectedVehicle, setSelectedVehicle] = useState(null);
    const [userSearch, setUserSearch] = useState('');
    const [vehicleSearch, setVehicleSearch] = useState('');

    // Action States
    const [status, setStatus] = useState({ type: '', message: '' });
    const [actionLoading, setActionLoading] = useState(false);

    useEffect(() => {
        fetchInitialData();
    }, []);

    const fetchInitialData = async () => {
        setLoading(true);
        setStatus({ type: '', message: '' });
        
        try {
            // Try fetching users
            try {
                const userData = await getRequest('/users');
                setUsers(Array.isArray(userData) ? userData : []);
            } catch (e) {
                console.error('Failed to fetch users:', e);
            }

            // Try fetching vehicles
            try {
                const vehicleData = await getRequest('/admin/vehicles');
                setVehicles(Array.isArray(vehicleData) ? vehicleData : []);
            } catch (e) {
                console.error('Failed to fetch vehicles:', e);
            }

            // Try fetching recent bills
            try {
                const billData = await getRequest('/admin/bills/recent');
                setRecentBills(Array.isArray(billData) ? billData.sort((a, b) => a.billId - b.billId) : []);
            } catch (e) {
                console.error('Failed to fetch recent bills:', e);
                // If this is the only one failing, we can still show the page
            }

        } catch (err) {
            console.error('Unexpected error in fetchInitialData:', err);
            setStatus({ type: 'error', message: 'Failed to load initial billing data' });
        } finally {
            setLoading(false);
        }
    };

    const fetchRecentBills = async () => {
        try {
            const data = await getRequest('/admin/bills/recent');
            setRecentBills(Array.isArray(data) ? data.sort((a, b) => a.billId - b.billId) : []);
        } catch (err) {
            console.error('Error refreshing bills:', err);
        }
    };

    const handleAction = async (endpoint, label) => {
        setActionLoading(true);
        setStatus({ type: '', message: '' });
        try {
            const response = await postRequest(endpoint);
            setStatus({ 
                type: 'success', 
                message: response.message || `Successfully triggered ${label}` 
            });
            fetchRecentBills(); // Refresh history
        } catch (err) {
            setStatus({ 
                type: 'error', 
                message: err.response?.data?.message || `Failed to trigger ${label}` 
            });
        } finally {
            setActionLoading(false);
        }
    };

    const filteredUsers = users.filter(u => 
        u.name?.toLowerCase().includes(userSearch.toLowerCase()) || 
        u.email?.toLowerCase().includes(userSearch.toLowerCase())
    ).slice(0, 5);

    const filteredVehicles = vehicles.filter(v => 
        v.vehicleNumber?.toLowerCase().includes(vehicleSearch.toLowerCase())
    ).slice(0, 5);

    return (
        <div className="page admin-billing-page">
            <h2>💰 Granular Admin Billing</h2>
            <p className="subtitle">Trigger bill generation for specific users, vehicles, or bulk groups with highway-specific tolling.</p>

            {status.message && (
                <div className={`status-indicator status-${status.type}`}>
                    {status.type === 'success' ? '✓ ' : '⚠️ '}
                    {status.message}
                </div>
            )}

            <div className="billing-grid">
                {/* Bulk Actions */}
                <div className="billing-card bulk-actions">
                    <div style={{ flex: 1 }}>
                        <h3>📦 Bulk Operations</h3>
                        <p>Generate monthly bills using highway-specific rates defined for each section.</p>
                    </div>
                    <div style={{ display: 'flex', gap: '10px' }}>
                        <button 
                            className="action-btn btn-primary"
                            onClick={() => handleAction('/admin/generate-bills', 'Bulk Consolidated Billing')}
                            disabled={actionLoading}
                        >
                            Generate All (Consolidated)
                        </button>
                        <button 
                            className="action-btn btn-success"
                            onClick={() => handleAction('/admin/generate-all-vehicle-bills', 'Bulk Vehicle Billing')}
                            disabled={actionLoading}
                        >
                            Generate All (Individual Vehicles)
                        </button>
                    </div>
                </div>

                {/* User Specific Billing */}
                <div className="billing-card">
                    <h3>👤 User Billing</h3>
                    <p>Aggregate distance across all user vehicles for highway-accurate billing.</p>
                    
                    {!selectedUser ? (
                        <div className="search-box">
                            <input 
                                type="text" 
                                className="search-input" 
                                placeholder="Search by name or email..."
                                value={userSearch}
                                onChange={(e) => setUserSearch(e.target.value)}
                            />
                            {userSearch.length > 0 && (
                                <div className="dropdown-list">
                                    {filteredUsers.map(user => (
                                        <div 
                                            key={user.userId} 
                                            className="dropdown-item"
                                            onClick={() => setSelectedUser(user)}
                                        >
                                            <strong>{user.name}</strong> ({user.email})
                                        </div>
                                    ))}
                                </div>
                            )}
                        </div>
                    ) : (
                        <div className="selected-item">
                            <span>{selectedUser.name}</span>
                            <button className="clear-btn" onClick={() => setSelectedUser(null)}>×</button>
                        </div>
                    )}

                    <button 
                        className="action-btn btn-primary"
                        disabled={!selectedUser || actionLoading}
                        onClick={() => handleAction(`/admin/generate-bill/user/${selectedUser.userId}`, 'User Bill')}
                    >
                        {actionLoading ? 'Processing...' : 'Generate User Bill'}
                    </button>
                </div>

                {/* Vehicle Specific Billing */}
                <div className="billing-card">
                    <h3>🚗 Vehicle Billing</h3>
                    <p>Trigger bill generation for a specific vehicle's highway usage.</p>

                    {!selectedVehicle ? (
                        <div className="search-box">
                            <input 
                                type="text" 
                                className="search-input" 
                                placeholder="Search by vehicle number..."
                                value={vehicleSearch}
                                onChange={(e) => setVehicleSearch(e.target.value)}
                            />
                            {vehicleSearch.length > 0 && (
                                <div className="dropdown-list">
                                    {filteredVehicles.map(v => (
                                        <div 
                                            key={v.vehicleId} 
                                            className="dropdown-item"
                                            onClick={() => setSelectedVehicle(v)}
                                        >
                                            <strong>{v.vehicleNumber}</strong>
                                        </div>
                                    ))}
                                </div>
                            )}
                        </div>
                    ) : (
                        <div className="selected-item">
                            <span>{selectedVehicle.vehicleNumber}</span>
                            <button className="clear-btn" onClick={() => setSelectedVehicle(null)}>×</button>
                        </div>
                    )}

                    <button 
                        className="action-btn btn-success"
                        disabled={!selectedVehicle || actionLoading}
                        onClick={() => handleAction(`/admin/generate-bill/vehicle/${selectedVehicle.vehicleId}`, 'Vehicle Bill')}
                    >
                        {actionLoading ? 'Processing...' : 'Generate Vehicle Bill'}
                    </button>
                </div>

                {/* Recent Billing Activity Table */}
                <div className="billing-card recent-activity">
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <h3>🕒 Recent Billing Activity</h3>
                        <button className="btn-small" onClick={fetchRecentBills}>Refresh</button>
                    </div>
                    <div className="table-container" style={{ maxHeight: '300px', overflowY: 'auto' }}>
                        <table className="table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>User ID</th>
                                    <th>Vehicle</th>
                                    <th>Month</th>
                                    <th>Amount</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                {recentBills.length > 0 ? recentBills.map(bill => (
                                    <tr key={bill.billId}>
                                        <td>#{bill.billId}</td>
                                        <td>User #{bill.userId}</td>
                                        <td>{bill.vehicleId ? (vehicles.find(v => v.vehicleId === bill.vehicleId)?.vehicleNumber || `ID: ${bill.vehicleId}`) : <span style={{color: '#7f8c8d'}}>(Consolidated)</span>}</td>
                                        <td>{bill.billMonth}</td>
                                        <td style={{ fontWeight: 600 }}>₹{bill.totalAmount.toFixed(2)}</td>
                                        <td>
                                            <span className={`badge ${bill.status === 'PAID' ? 'bg-success' : 'bg-warning'}`}>
                                                {bill.status}
                                            </span>
                                        </td>
                                    </tr>
                                )) : (
                                    <tr>
                                        <td colSpan="6" style={{ textAlign: 'center', padding: '20px' }}>No recent bills found.</td>
                                    </tr>
                                )}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default AdminBilling;
