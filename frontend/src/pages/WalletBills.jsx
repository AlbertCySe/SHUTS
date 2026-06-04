import React, { useState, useEffect } from 'react';
import { getRequest, postRequest } from '../services/api';
import { getSession } from '../services/auth';
import WalletCard from '../components/wallet/WalletCard';
import BillGenerator from '../components/wallet/BillGenerator';
import BillsTable from '../components/wallet/BillsTable';
import './AdminUsersStyles.css';

function WalletBills() {
    const session = getSession();
    const userId = session?.userId;

    // Wallet state
    const [wallet, setWallet] = useState(null);
    const [walletLoading, setWalletLoading] = useState(true);
    const [addAmount, setAddAmount] = useState('');
    const [addLoading, setAddLoading] = useState(false);
    const [addMessage, setAddMessage] = useState(null);

    // Data state
    const [bills, setBills] = useState([]);
    const [vehicles, setVehicles] = useState([]);
    const [billsLoading, setBillsLoading] = useState(true);

    // Bill generation state
    const [genMode, setGenMode] = useState('monthly');
    const [genVehicle, setGenVehicle] = useState('');
    const [genCustomFrom, setGenCustomFrom] = useState('');
    const [genCustomTo, setGenCustomTo] = useState('');
    const [genLoading, setGenLoading] = useState(false);
    const [genMessage, setGenMessage] = useState(null);

    useEffect(() => { fetchAll(); }, []);

    const fetchAll = async () => {
        setWalletLoading(true);
        setBillsLoading(true);
        const [walletData, billData, vehicleData] = await Promise.all([
            getRequest(`/wallets/user/${userId}`).catch(e => e.response?.status === 404 ? { balance: 0, notInitialized: true } : null),
            getRequest(`/bills/user/${userId}`).catch(() => []),
            getRequest(`/users/${userId}/vehicles`).catch(() => []),
        ]);
        setWallet(walletData);
        setBills(Array.isArray(billData) ? billData : []);
        setVehicles(Array.isArray(vehicleData) ? vehicleData : []);
        setWalletLoading(false);
        setBillsLoading(false);
    };

    const handleRechargeRequest = async (amount) => {
        const val = parseFloat(amount || addAmount);
        if (!val || val <= 0) { setAddMessage({ type: 'error', text: 'Enter a valid amount.' }); return; }
        setAddLoading(true);
        try {
            const res = await postRequest(`/wallets/user/${userId}/recharge-request`, { amount: val });
            if (res.success) {
                setAddMessage({ type: 'success', text: `✅ Recharge request sent! Admin will verify UPI: ${res.upiReference}` });
            } else {
                setAddMessage({ type: 'error', text: res.message || 'Failed to submit request.' });
            }
            setAddAmount('');
        } catch { setAddMessage({ type: 'error', text: 'Failed to request recharge. Please try again.' }); }
        finally {
            setAddLoading(false);
            setTimeout(() => setAddMessage(null), 6000);
        }
    };

    const handleGenerateBill = async () => {
        setGenLoading(true);
        setGenMessage(null);
        try {
            const now = new Date();
            let from, to;
            if (genMode === 'daily') { from = to = now.toISOString().split('T')[0]; }
            else if (genMode === 'weekly') {
                const s = new Date(now); s.setDate(now.getDate() - now.getDay());
                from = s.toISOString().split('T')[0]; to = now.toISOString().split('T')[0];
            } else if (genMode === 'monthly') {
                from = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-01`;
                to = now.toISOString().split('T')[0];
            } else { from = genCustomFrom; to = genCustomTo; }

            await postRequest(`/bills/generate`, { userId, vehicleId: genVehicle || null, fromDate: from, toDate: to }).catch(() => null);
            setGenMessage({ type: 'success', text: `✅ Bill generated for ${from} to ${to}.` });
            await fetchAll();
        } catch { setGenMessage({ type: 'error', text: 'Check the table below for any updated bills.' }); }
        finally { setGenLoading(false); setTimeout(() => setGenMessage(null), 5000); }
    };

    const exportCSV = (filteredBills) => {
        const header = ['Bill ID', 'Month', 'Vehicle', 'Distance(km)', 'Amount(₹)', 'Status', 'Due Date'];
        const rows = filteredBills.map(b => [
            b.billId, b.billMonth || 'N/A', b.vehicle?.vehicleNumber || 'N/A',
            parseFloat(b.totalDistance || 0).toFixed(2), parseFloat(b.totalAmount || 0).toFixed(2),
            b.isPaid ? 'PAID' : 'PENDING',
            b.dueDate ? new Date(b.dueDate).toLocaleDateString('en-IN') : 'N/A',
        ]);
        const csv = [header, ...rows].map(r => r.join(',')).join('\n');
        const link = document.createElement('a');
        link.href = 'data:text/csv;charset=utf-8,' + encodeURIComponent(csv);
        link.download = `toll_bills_${userId}.csv`;
        link.click();
    };

    return (
        <div className="page admin-users-page">
            <h2>💳 Wallet &amp; Bills</h2>
            <p style={{ color: '#7f8c8d', marginTop: '-10px', marginBottom: '20px' }}>
                Manage your balance, top up, and view or generate your toll bills.
            </p>

            <WalletCard
                wallet={wallet} loading={walletLoading}
                addAmount={addAmount} setAddAmount={setAddAmount}
                addLoading={addLoading} addMessage={addMessage}
                onRechargeRequested={handleRechargeRequest}
            />

            <BillGenerator
                vehicles={vehicles}
                genMode={genMode} setGenMode={setGenMode}
                genVehicle={genVehicle} setGenVehicle={setGenVehicle}
                genCustomFrom={genCustomFrom} setGenCustomFrom={setGenCustomFrom}
                genCustomTo={genCustomTo} setGenCustomTo={setGenCustomTo}
                genLoading={genLoading} genMessage={genMessage}
                onGenerate={handleGenerateBill}
            />

            <BillsTable
                bills={bills} vehicles={vehicles}
                loading={billsLoading}
                onExportCSV={exportCSV}
                onPayBill={async (billId) => {
                    const confirm = window.confirm('Pay this bill using your wallet balance?');
                    if (!confirm) return;
                    try {
                        const res = await postRequest(`/bills/${billId}/pay`, {});
                        if (res.success) {
                            alert('Bill paid successfully!');
                            fetchAll();
                        } else {
                            alert(res.message || 'Failed to pay bill.');
                        }
                    } catch (e) {
                        alert(e.response?.data?.message || 'Error occurred while paying bill.');
                    }
                }}
            />
        </div>
    );
}

export default WalletBills;
