import { useState, useEffect } from 'react';
import { getRequest } from '../../services/api';

export default function AdminUserProfileModal({ viewingUser, setViewingUser }) {
    const [wallet, setWallet] = useState(null);
    const [bills, setBills] = useState([]);
    const [vehicleStats, setVehicleStats] = useState({});
    const [loadingStats, setLoadingStats] = useState(false);

    useEffect(() => {
        if (!viewingUser) {
            // reset stats when closing
            setWallet(null);
            setBills([]);
            setVehicleStats({});
            return;
        }

        const fetchComprehensiveDetails = async () => {
            setLoadingStats(true);
            try {
                // 1. Fetch Wallet
                let fetchedWallet = null;
                try {
                    fetchedWallet = await getRequest(`/wallets/user/${viewingUser.userId}`);
                    setWallet(fetchedWallet);
                } catch (e) {
                    setWallet({ notInitialized: true, balance: 0 });
                }

                // 2. Fetch Bills (to get total tolls paid)
                let fetchedBills = [];
                try {
                    fetchedBills = await getRequest(`/bills/user/${viewingUser.userId}`);
                    setBills(fetchedBills || []);
                } catch (e) {
                    setBills([]);
                }

                // 3. Fetch Real-time Vehicle Stats
                const statsMap = {};
                if (viewingUser.vehicles && viewingUser.vehicles.length > 0) {
                    await Promise.all(viewingUser.vehicles.map(async (v) => {
                        try {
                            const summary = await getRequest(`/highway-usage/summary/${v.vehicleId}`);
                            statsMap[v.vehicleId] = summary;
                        } catch (e) {
                            statsMap[v.vehicleId] = null;
                        }
                    }));
                }
                setVehicleStats(statsMap);

            } catch (err) {
                console.error("Failed to fetch user comprehensive details", err);
            } finally {
                setLoadingStats(false);
            }
        };

        fetchComprehensiveDetails();
    }, [viewingUser]);

    if (!viewingUser) return null;

    const totalTollsPaid = bills.reduce((sum, bill) => sum + (bill.totalAmount || 0), 0);
    const totalRealtimeDistance = Object.values(vehicleStats)
        .reduce((sum, stat) => sum + (stat?.totalDistance || 0), 0);

    return (
        <div style={{
            position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
            backgroundColor: 'rgba(0,0,0,0.6)', zIndex: 1000,
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            padding: '20px'
        }}>
            <div className="card" style={{ width: '520px', maxWidth: '100%', margin: 0, maxHeight: '90vh', overflowY: 'auto' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid #eee', paddingBottom: '16px', marginBottom: '16px' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '15px' }}>
                        <div style={{ width: '50px', height: '50px', borderRadius: '25px', backgroundColor: '#3498db', color: 'white', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '24px', fontWeight: 'bold' }}>
                            {viewingUser.name.charAt(0).toUpperCase()}
                        </div>
                        <div>
                            <h3 style={{ margin: 0, fontSize: '20px' }}>{viewingUser.name}</h3>
                            <p style={{ margin: 0, color: '#7f8c8d', fontSize: '13px' }}>User ID: #{viewingUser.userId}</p>
                        </div>
                    </div>
                    <button onClick={() => setViewingUser(null)} style={{ background: 'transparent', border: 'none', fontSize: '20px', cursor: 'pointer', color: '#95a5a6' }}>✕</button>
                </div>
                
                <div style={{ marginBottom: '24px' }}>
                    <div style={{ display: 'grid', gridTemplateColumns: '120px 1fr', gap: '10px', marginBottom: '6px' }}>
                        <strong style={{ color: '#2c3e50' }}>Email:</strong>
                        <a href={`mailto:${viewingUser.email}`} style={{ color: '#3498db', textDecoration: 'none' }}>{viewingUser.email}</a>
                    </div>
                    <div style={{ display: 'grid', gridTemplateColumns: '120px 1fr', gap: '10px', marginBottom: '6px' }}>
                        <strong style={{ color: '#2c3e50' }}>Phone:</strong>
                        <span>{viewingUser.phoneNumber}</span>
                    </div>
                    <div style={{ display: 'grid', gridTemplateColumns: '120px 1fr', gap: '10px', marginBottom: '6px' }}>
                        <strong style={{ color: '#2c3e50' }}>Member Since:</strong>
                        <span>{new Date(viewingUser.createdAt).toLocaleDateString()}</span>
                    </div>

                    {/* Analytics Summary Row */}
                    <div style={{ display: 'flex', gap: '10px', marginTop: '20px', flexWrap: 'wrap', opacity: loadingStats ? 0.5 : 1, transition: 'opacity 0.3s' }}>
                        <div style={{ flex: '1', backgroundColor: '#eef2f5', padding: '12px', borderRadius: '8px', textAlign: 'center' }}>
                            <div style={{ fontSize: '11px', color: '#7f8c8d', textTransform: 'uppercase', letterSpacing: '0.5px' }}>Wallet Balance</div>
                            <div style={{ fontSize: '18px', fontWeight: 'bold', color: wallet?.balance < 0 ? '#e74c3c' : '#2ecc71', marginTop: '4px' }}>
                                {loadingStats ? '...' : (wallet?.notInitialized ? 'Not Init' : `₹${(wallet?.balance || 0).toFixed(2)}`)}
                            </div>
                        </div>
                        <div style={{ flex: '1', backgroundColor: '#eef2f5', padding: '12px', borderRadius: '8px', textAlign: 'center' }}>
                            <div style={{ fontSize: '11px', color: '#7f8c8d', textTransform: 'uppercase', letterSpacing: '0.5px' }}>Total Tolls Billed</div>
                            <div style={{ fontSize: '18px', fontWeight: 'bold', color: '#34495e', marginTop: '4px' }}>
                                {loadingStats ? '...' : `₹${totalTollsPaid.toFixed(2)}`}
                            </div>
                        </div>
                    </div>
                </div>

                <div style={{ backgroundColor: '#f8f9fa', borderRadius: '8px', padding: '16px', border: '1px solid #e9ecef', opacity: loadingStats ? 0.5 : 1 }}>
                    <h4 style={{ margin: '0 0 12px 0', display: 'flex', alignItems: 'center', gap: '8px' }}>
                        🚗 Registered Vehicles & Individual Usage
                        <span className="admin-id-pill" style={{ marginLeft: 'auto' }}>{viewingUser.vehicles ? viewingUser.vehicles.length : 0}</span>
                    </h4>
                    
                    {viewingUser.vehicles && viewingUser.vehicles.length > 0 ? (
                        <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                            {viewingUser.vehicles.map(v => {
                                const stat = vehicleStats[v.vehicleId];
                                return (
                                    <div key={v.vehicleId} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', backgroundColor: 'white', padding: '10px 14px', borderRadius: '6px', border: '1px solid #ddd' }}>
                                        <div style={{ display: 'flex', flexDirection: 'column', flex: 1 }}>
                                            <strong style={{ fontSize: '16px', letterSpacing: '1px' }}>{v.vehicleNumber}</strong>
                                            <span style={{ fontSize: '12px', color: '#7f8c8d', marginTop: '2px' }}>
                                                ID: #{v.vehicleId}
                                            </span>
                                        </div>
                                        <div style={{ flex: 1, display: 'flex', justifyContent: 'center' }}>
                                            <span style={{ 
                                                backgroundColor: v.vehicleType === 'CAR' ? '#e8ecef' : '#fff3cd', 
                                                color: v.vehicleType === 'CAR' ? '#495057' : '#856404', 
                                                padding: '4px 8px', borderRadius: '4px', fontSize: '11px', fontWeight: 'bold' 
                                            }}>
                                                {v.vehicleType}
                                            </span>
                                        </div>
                                        <div style={{ flex: 1, display: 'flex', justifyContent: 'flex-end', alignItems: 'center' }}>
                                            <span style={{ fontSize: '14px', color: '#2c3e50', fontWeight: '600' }}>
                                                {stat ? stat.totalDistance.toFixed(1) : '0.0'} <span style={{fontSize: '12px', color: '#7f8c8d', fontWeight: 'normal'}}>km</span>
                                            </span>
                                        </div>
                                    </div>
                                );
                            })}
                        </div>
                    ) : (
                        <p style={{ color: '#7f8c8d', fontStyle: 'italic', margin: 0 }}>No vehicles are currently registered to this user.</p>
                    )}
                </div>

                <button className="btn btn-primary" style={{ width: '100%', marginTop: '24px', padding: '12px' }} onClick={() => setViewingUser(null)}>
                    Close Profile Overview
                </button>
            </div>
        </div>
    );
}
