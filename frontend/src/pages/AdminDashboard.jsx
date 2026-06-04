import { useState, useEffect } from 'react';
import { getRequest } from '../services/api';

function AdminDashboard() {
    // Statistics state
    const [stats, setStats] = useState(null);
    const [statsLoading, setStatsLoading] = useState(true);
    const [statsError, setStatsError] = useState(null);

    // Negative balance wallets state
    const [negativeWallets, setNegativeWallets] = useState([]);
    const [walletsLoading, setWalletsLoading] = useState(true);
    const [walletsError, setWalletsError] = useState(null);

    // All vehicles state
    const [vehicles, setVehicles] = useState([]);
    const [vehiclesLoading, setVehiclesLoading] = useState(true);
    const [vehiclesError, setVehiclesError] = useState(null);

    // IoT Simulator Connection Status State
    const [iotConnected, setIotConnected] = useState(null); // null = checking, true = connected, false = disconnected
    const [activeIotCount, setActiveIotCount] = useState(0);

    // Fetch all data on component mount
    useEffect(() => {
        fetchStatistics();
        fetchNegativeBalanceWallets();
        fetchAllVehicles();
    }, []);

    // Check IoT Simulator connection on mount and every 5 seconds
    useEffect(() => {
        const checkIotConnection = async () => {
            try {
                const res = await fetch('http://localhost:8082/api/simulation/status');
                if (res.ok) {
                    const data = await res.json();
                    setIotConnected(true);
                    setActiveIotCount(data.activeCount || 0);
                } else {
                    setIotConnected(false);
                }
            } catch (err) {
                setIotConnected(false);
            }
        };
        checkIotConnection();
        const interval = setInterval(checkIotConnection, 5000);
        return () => clearInterval(interval);
    }, []);

    const fetchStatistics = async () => {
        try {
            setStatsLoading(true);
            setStatsError(null);
            const data = await getRequest('/admin/stats');
            setStats(data);
        } catch (err) {
            setStatsError('Failed to fetch statistics');
            console.error('Error fetching stats:', err);
        } finally {
            setStatsLoading(false);
        }
    };

    const fetchNegativeBalanceWallets = async () => {
        try {
            setWalletsLoading(true);
            setWalletsError(null);
            const data = await getRequest('/admin/wallets/negative');
            setNegativeWallets(data);
        } catch (err) {
            setWalletsError('Failed to fetch negative balance wallets');
            console.error('Error fetching wallets:', err);
        } finally {
            setWalletsLoading(false);
        }
    };

    const fetchAllVehicles = async () => {
        try {
            setVehiclesLoading(true);
            setVehiclesError(null);
            const data = await getRequest('/admin/vehicles');
            setVehicles(data);
        } catch (err) {
            setVehiclesError('Failed to fetch vehicles');
            console.error('Error fetching vehicles:', err);
        } finally {
            setVehiclesLoading(false);
        }
    };

    return (
        <div className="page">
            <h2>Admin Dashboard</h2>
            <p style={{ color: '#666', marginBottom: '20px' }}>
                System monitoring and overview
            </p>

            {/* 🛰️ IoT Simulator Connection Status Indicator */}
            <div style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                background: iotConnected === null 
                    ? 'rgba(102,126,234,0.05)' 
                    : iotConnected 
                        ? 'linear-gradient(135deg, rgba(39,174,96,0.08), rgba(46,204,113,0.08))' 
                        : 'linear-gradient(135deg, rgba(231,76,60,0.08), rgba(192,57,43,0.08))',
                border: `1px solid ${
                    iotConnected === null 
                        ? 'rgba(102,126,234,0.2)' 
                        : iotConnected 
                            ? 'rgba(46,204,113,0.3)' 
                            : 'rgba(231,76,60,0.3)'
                }`,
                borderRadius: '12px',
                padding: '16px 24px',
                marginBottom: '25px',
                boxShadow: iotConnected ? '0 4px 15px rgba(46,204,113,0.05)' : 'none',
                transition: 'all 0.3s ease'
            }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '15px' }}>
                    <div style={{ fontSize: '24px' }}>🛰️</div>
                    <div>
                        <h4 style={{ margin: 0, fontSize: '15px', color: '#2c3e50', fontWeight: '700' }}>
                            External IoT Simulator
                        </h4>
                        <p style={{ margin: '3px 0 0 0', fontSize: '12px', color: '#7f8c8d' }}>
                            Standalone device broadcaster running on Port 8082
                        </p>
                    </div>
                </div>

                <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                    <span style={{
                        width: '10px', height: '10px', borderRadius: '50%',
                        background: iotConnected === null ? '#bdc3c7' : iotConnected ? '#2ecc71' : '#e74c3c',
                        boxShadow: iotConnected ? '0 0 8px #2ecc71' : 'none',
                        animation: iotConnected ? 'pulse-glow 1.5s infinite' : 'none',
                        display: 'inline-block'
                    }} />
                    <span style={{
                        fontWeight: '800',
                        fontSize: '13px',
                        letterSpacing: '0.5px',
                        color: iotConnected === null ? '#7f8c8d' : iotConnected ? '#27ae60' : '#e74c3c'
                    }}>
                        {iotConnected === null 
                            ? 'CHECKING CONNECTION...' 
                            : iotConnected 
                                ? `CONNECTED (${activeIotCount} ACTIVE DEVICES)` 
                                : 'DISCONNECTED (OFFLINE)'
                        }
                    </span>
                </div>
            </div>


            {/* Statistics Cards */}
            <div className="stats-grid">
                {statsLoading && (
                    <div className="stat-card">
                        <p className="info-text">Loading statistics...</p>
                    </div>
                )}

                {statsError && (
                    <div className="error-message">
                        <p>{statsError}</p>
                    </div>
                )}

                {!statsLoading && !statsError && stats && (
                    <>
                        <div className="stat-card">
                            <h3>Total Vehicles</h3>
                            <p className="stat-value">{stats.totalVehicles}</p>
                            <p className="stat-label">Registered</p>
                        </div>

                        <div className="stat-card">
                            <h3>Total Toll Collected</h3>
                            <p className="stat-value">₹{stats.totalTollCollected?.toFixed(2) || '0.00'}</p>
                            <p className="stat-label">All Time</p>
                        </div>

                        <div className="stat-card">
                            <h3>Negative Balances</h3>
                            <p className="stat-value">{stats.walletsInDeficit || 0}</p>
                            <p className="stat-label">Wallets in Deficit</p>
                        </div>
                    </>
                )}
            </div>


            {/* Negative Balance Wallets */}
            <div className="card">
                <h3>Wallets with Negative Balance</h3>

                    {walletsLoading && <p className="info-text">Loading wallets...</p>}

                    {walletsError && (
                        <div className="error-message">
                            <p>{walletsError}</p>
                        </div>
                    )}

                    {!walletsLoading && !walletsError && negativeWallets.length > 0 && (
                        <div className="table-container">
                            <table className="table">
                                <thead>
                                    <tr>
                                        <th>Wallet ID</th>
                                        <th>User ID</th>
                                        <th>Balance</th>
                                        <th>Minimum Balance</th>
                                        <th>Deficit Amount</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {negativeWallets.map((wallet) => (
                                        <tr key={wallet.walletId}>
                                            <td>{wallet.walletId}</td>
                                            <td>{wallet.user?.userId || 'N/A'}</td>
                                            <td className="text-danger">₹{wallet.balance.toFixed(2)}</td>
                                            <td>₹{wallet.minimumBalance.toFixed(2)}</td>
                                            <td className="text-danger">
                                                ₹{(wallet.minimumBalance - wallet.balance).toFixed(2)}
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    )}

                    {!walletsLoading && !walletsError && negativeWallets.length === 0 && (
                        <p className="info-text">✓ No wallets with negative balance!</p>
                    )}
                </div>

        </div>
    );
}

export default AdminDashboard;
