import React, { useState, useEffect } from 'react';
import { getRequest } from '../services/api';
import { getSession } from '../services/auth';
import './AdminUsersStyles.css';

function TollHistory() {
    const session = getSession();
    const userId = session?.userId;

    const [bills, setBills] = useState([]);
    const [vehicles, setVehicles] = useState([]);
    const [usageSummaries, setUsageSummaries] = useState({});
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        fetchAll();
    }, []);

    const fetchAll = async () => {
        setLoading(true);
        setError(null);
        try {
            const [billData, vehicleData] = await Promise.all([
                getRequest(`/bills/user/${userId}`).catch(() => []),
                getRequest(`/users/${userId}/vehicles`).catch(() => [])
            ]);

            setBills(Array.isArray(billData) ? billData : []);
            setVehicles(Array.isArray(vehicleData) ? vehicleData : []);

            // Fetch usage summary per vehicle
            const summaries = {};
            for (const v of vehicleData) {
                try {
                    const summary = await getRequest(`/highway-usage/summary/${v.vehicleId}`);
                    summaries[v.vehicleId] = summary;
                } catch (e) {
                    summaries[v.vehicleId] = null;
                }
            }
            setUsageSummaries(summaries);
        } catch (err) {
            setError('Failed to load toll history. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    const totalSpent = bills.reduce((sum, b) => sum + (b.totalAmount || 0), 0);

    return (
        <div className="page admin-users-page">
            <h2>📊 Toll History</h2>
            <p style={{ color: '#7f8c8d', marginTop: '-10px', marginBottom: '20px' }}>
                View your past highway usage and toll deductions.
            </p>

            {loading && <p className="info-text">Loading your toll history...</p>}
            {error && <div className="error-message"><p>{error}</p><button onClick={fetchAll} className="btn btn-primary">Retry</button></div>}

            {!loading && !error && (
                <>
                    {/* Summary Cards */}
                    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '16px', marginBottom: '24px' }}>
                        <div className="card" style={{ textAlign: 'center', padding: '20px', background: 'linear-gradient(135deg, #667eea, #764ba2)', color: 'white' }}>
                            <div style={{ fontSize: '28px' }}>💳</div>
                            <h3 style={{ margin: '8px 0 4px', color: 'white' }}>₹{totalSpent.toFixed(2)}</h3>
                            <p style={{ margin: 0, opacity: 0.85, fontSize: '13px' }}>Total Toll Charges</p>
                        </div>
                        <div className="card" style={{ textAlign: 'center', padding: '20px', background: 'linear-gradient(135deg, #43e97b, #38f9d7)', color: 'white' }}>
                            <div style={{ fontSize: '28px' }}>🧾</div>
                            <h3 style={{ margin: '8px 0 4px', color: 'white' }}>{bills.length}</h3>
                            <p style={{ margin: 0, opacity: 0.85, fontSize: '13px' }}>Total Bills Generated</p>
                        </div>
                        <div className="card" style={{ textAlign: 'center', padding: '20px', background: 'linear-gradient(135deg, #f7971e, #ffd200)', color: 'white' }}>
                            <div style={{ fontSize: '28px' }}>🚗</div>
                            <h3 style={{ margin: '8px 0 4px', color: 'white' }}>{vehicles.length}</h3>
                            <p style={{ margin: 0, opacity: 0.85, fontSize: '13px' }}>Registered Vehicles</p>
                        </div>
                    </div>

                    {/* Vehicle Highway Usage */}
                    {vehicles.length > 0 && (
                        <div className="card" style={{ marginBottom: '24px' }}>
                            <h3>🛣️ Highway Usage by Vehicle</h3>
                            <div className="table-responsive">
                                <table className="custom-data-table">
                                    <thead>
                                        <tr>
                                            <th>Vehicle Number</th>
                                            <th>Type</th>
                                            <th>Total Distance (km)</th>
                                            <th>Sessions</th>
                                            <th>Top Highway</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {vehicles.map(v => {
                                            const s = usageSummaries[v.vehicleId];
                                            const breakdown = s?.distanceByHighway || {};
                                            const topHighway = Object.entries(breakdown).sort((a,b) => b[1]-a[1])[0];
                                            return (
                                                <tr key={v.vehicleId}>
                                                    <td><strong>{v.vehicleNumber}</strong></td>
                                                    <td><span style={{ background: '#edf2f7', padding: '2px 8px', borderRadius: '4px', fontSize: '12px', fontWeight: 'bold' }}>{v.vehicleType}</span></td>
                                                    <td style={{ fontWeight: 'bold', color: '#2ecc71' }}>{s ? `${parseFloat(s.totalDistance || 0).toFixed(2)} km` : 'N/A'}</td>
                                                    <td>{s?.totalSessions ?? 'N/A'}</td>
                                                    <td>{topHighway ? `${topHighway[0]} (${topHighway[1].toFixed(1)} km)` : 'N/A'}</td>
                                                </tr>
                                            );
                                        })}
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    )}

                    {/* Bills History */}
                    <div className="card">
                        <h3>🧾 Bill History</h3>
                        {bills.length === 0 ? (
                            <p className="info-text">No bills generated yet. Bills appear after highway usage is recorded.</p>
                        ) : (
                            <div className="table-responsive">
                                <table className="custom-data-table">
                                    <thead>
                                        <tr>
                                            <th>#</th>
                                            <th>Bill Month</th>
                                            <th>Vehicle</th>
                                            <th>Distance (km)</th>
                                            <th>Amount</th>
                                            <th>Status</th>
                                            <th>Due Date</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {bills.map((bill, i) => (
                                            <tr key={bill.billId || i}>
                                                <td><span className="admin-id-pill">#{i + 1}</span></td>
                                                <td>{bill.billMonth || 'N/A'}</td>
                                                <td>{bill.vehicle?.vehicleNumber || bill.vehicleNumber || 'N/A'}</td>
                                                <td>{bill.totalDistance ? `${parseFloat(bill.totalDistance).toFixed(2)} km` : 'N/A'}</td>
                                                <td style={{ fontWeight: 'bold', color: '#8e44ad' }}>
                                                    ₹{parseFloat(bill.totalAmount || 0).toFixed(2)}
                                                </td>
                                                <td>
                                                    <span style={{
                                                        padding: '3px 10px', borderRadius: '12px', fontSize: '12px', fontWeight: 'bold',
                                                        background: bill.isPaid ? 'rgba(39,174,96,0.1)' : 'rgba(231,76,60,0.1)',
                                                        color: bill.isPaid ? '#27ae60' : '#e74c3c'
                                                    }}>
                                                        {bill.isPaid ? '✅ Paid' : '⏳ Pending'}
                                                    </span>
                                                </td>
                                                <td style={{ color: '#7f8c8d', fontSize: '13px' }}>
                                                    {bill.dueDate ? new Date(bill.dueDate).toLocaleDateString('en-IN') : 'N/A'}
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        )}
                    </div>
                </>
            )}
        </div>
    );
}

export default TollHistory;
