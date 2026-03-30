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

    // Fetch all data on component mount
    useEffect(() => {
        fetchStatistics();
        fetchNegativeBalanceWallets();
        fetchAllVehicles();
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
