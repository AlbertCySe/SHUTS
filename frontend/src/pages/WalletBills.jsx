import { useState } from 'react';
import { getRequest } from '../services/api';

function WalletBills() {
    const [userId, setUserId] = useState('');

    // Wallet state
    const [wallet, setWallet] = useState(null);
    const [walletLoading, setWalletLoading] = useState(false);
    const [walletError, setWalletError] = useState(null);

    // Bills state
    const [bills, setBills] = useState([]);
    const [billsLoading, setBillsLoading] = useState(false);
    const [billsError, setBillsError] = useState(null);

    // Fetch wallet and bills for a user
    const fetchUserData = async () => {
        if (!userId) {
            setWalletError('Please enter a User ID');
            return;
        }

        // Fetch wallet
        fetchWallet();
        // Fetch bills
        fetchBills();
    };

    const fetchWallet = async () => {
        try {
            setWalletLoading(true);
            setWalletError(null);
            // Assuming endpoint: /api/wallets/user/{userId} or similar
            // Adjust based on your actual backend endpoint
            const data = await getRequest(`/wallets/user/${userId}`);
            setWallet(data);
        } catch (err) {
            setWalletError('Failed to fetch wallet details. User may not have a wallet.');
            console.error('Error fetching wallet:', err);
            setWallet(null);
        } finally {
            setWalletLoading(false);
        }
    };

    const fetchBills = async () => {
        try {
            setBillsLoading(true);
            setBillsError(null);
            // Assuming endpoint exists to get bills by user
            const data = await getRequest(`/bills/user/${userId}`);
            setBills(data);
        } catch (err) {
            setBillsError('Failed to fetch bills. No bills found for this user.');
            console.error('Error fetching bills:', err);
            setBills([]);
        } finally {
            setBillsLoading(false);
        }
    };

    // Get status badge class
    const getStatusClass = (status) => {
        switch (status) {
            case 'PAID': return 'status-paid';
            case 'PENDING': return 'status-pending';
            case 'OVERDUE': return 'status-overdue';
            default: return '';
        }
    };

    return (
        <div className="page">
            <h2>Wallet & Monthly Bills</h2>
            <p style={{ color: '#666', marginBottom: '20px' }}>
                View wallet balance and billing information for a user.
            </p>

            {/* User ID Input */}
            <div className="card">
                <h3>Enter User ID</h3>
                <div className="form-row">
                    <div className="form-group">
                        <label>User ID:</label>
                        <input
                            type="number"
                            value={userId}
                            onChange={(e) => setUserId(e.target.value)}
                            placeholder="Enter user ID"
                        />
                    </div>
                    <div className="form-group">
                        <label style={{ visibility: 'hidden' }}>Action</label>
                        <button
                            onClick={fetchUserData}
                            className="btn btn-primary"
                        >
                            View Details
                        </button>
                    </div>
                </div>
            </div>

            {/* Wallet Details */}
            {userId && (
                <div className="card">
                    <h3>Wallet Details</h3>

                    {walletLoading && <p className="info-text">Loading wallet...</p>}

                    {walletError && (
                        <div className="error-message">
                            <p>{walletError}</p>
                        </div>
                    )}

                    {!walletLoading && !walletError && wallet && (
                        <div className="wallet-info">
                            <div className="wallet-item">
                                <span className="wallet-label">Wallet ID:</span>
                                <span className="wallet-value">{wallet.walletId}</span>
                            </div>
                            <div className="wallet-item">
                                <span className="wallet-label">Current Balance:</span>
                                <span className={`wallet-value ${wallet.balance < 0 ? 'text-danger' : 'text-success'}`}>
                                    ₹{wallet.balance.toFixed(2)}
                                </span>
                            </div>
                            <div className="wallet-item">
                                <span className="wallet-label">Minimum Balance:</span>
                                <span className="wallet-value">₹{wallet.minimumBalance.toFixed(2)}</span>
                            </div>
                            <div className="wallet-item">
                                <span className="wallet-label">Status:</span>
                                <span className={`wallet-value ${wallet.balance < wallet.minimumBalance ? 'text-danger' : 'text-success'}`}>
                                    {wallet.balance < wallet.minimumBalance ? '⚠️ In Deficit' : '✓ Healthy'}
                                </span>
                            </div>
                            {wallet.balance < 0 && (
                                <div className="wallet-alert">
                                    ⚠️ Wallet has negative balance. Please recharge!
                                </div>
                            )}
                        </div>
                    )}
                </div>
            )}

            {/* Monthly Bills */}
            {userId && (
                <div className="card">
                    <h3>Monthly Bills</h3>

                    {billsLoading && <p className="info-text">Loading bills...</p>}

                    {billsError && (
                        <div className="error-message">
                            <p>{billsError}</p>
                        </div>
                    )}

                    {!billsLoading && !billsError && bills.length > 0 && (
                        <div className="table-container">
                            <table className="table">
                                <thead>
                                    <tr>
                                        <th>Bill ID</th>
                                        <th>Bill Month</th>
                                        <th>Total Distance (km)</th>
                                        <th>Total Amount</th>
                                        <th>Due Date</th>
                                        <th>Status</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {bills.map((bill) => (
                                        <tr key={bill.billId}>
                                            <td>{bill.billId}</td>
                                            <td>{bill.billMonth}</td>
                                            <td>{bill.totalDistance.toFixed(2)}</td>
                                            <td>₹{bill.totalAmount.toFixed(2)}</td>
                                            <td>{new Date(bill.dueDate).toLocaleDateString()}</td>
                                            <td>
                                                <span className={`status-badge ${getStatusClass(bill.status)}`}>
                                                    {bill.status}
                                                </span>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    )}

                    {!billsLoading && !billsError && bills.length === 0 && userId && (
                        <p className="info-text">No bills found for this user.</p>
                    )}
                </div>
            )}
        </div>
    );
}

export default WalletBills;
