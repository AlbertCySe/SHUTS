import { useState, useEffect } from 'react';
import { getRequest } from '../services/api';
import { getSession } from '../services/auth';
import DashboardSummaryCards from '../components/dashboard/DashboardSummaryCards';
import DashboardMyVehicles from '../components/dashboard/DashboardMyVehicles';
import DashboardQuickActions from '../components/dashboard/DashboardQuickActions';
import './UserDashboardStyles.css';

function UserDashboard() {
    const session = getSession();
    const userId = session?.userId || null;

    const [loading, setLoading] = useState(true);
    const [userData, setUserData] = useState({
        name: session?.name || 'User',
        totalVehicles: 0,
        walletBalance: 0,
        tollUsedThisMonth: 0,
        lastHighwayUsed: 'No data available'
    });
    const [myVehicles, setMyVehicles] = useState([]);
    const [apiStatus, setApiStatus] = useState({
        userProfileLoaded: false,
        walletAvailable: true,
        billsAvailable: true,
        vehiclesLoaded: false
    });

    useEffect(() => {
        fetchDashboardData();
    }, []);

    const fetchDashboardData = async () => {
        setLoading(true);
        await Promise.all([
            fetchUserProfile(),
            fetchVehicles(),
            fetchWallet(),
            fetchBills(),
        ]);
        setLoading(false);
    };

    const fetchUserProfile = async () => {
        try {
            const user = await getRequest(`/users/${userId}`);
            setUserData(prev => ({ ...prev, name: user?.name || 'User' }));
            setApiStatus(prev => ({ ...prev, userProfileLoaded: true }));
        } catch {
            setApiStatus(prev => ({ ...prev, userProfileLoaded: false }));
        }
    };

    const fetchVehicles = async () => {
        try {
            const userVehicles = await getRequest(`/users/${userId}/vehicles`);
            setMyVehicles(userVehicles.map(v => ({
                id: v.vehicleId,
                number: v.vehicleNumber || 'Not available',
                type: v.vehicleType || 'Not available',
                status: v.status || 'ACTIVE',
                statusAssumed: !v.status
            })));
            setUserData(prev => ({ ...prev, totalVehicles: userVehicles.length }));
            setApiStatus(prev => ({ ...prev, vehiclesLoaded: true }));
        } catch {
            setMyVehicles([]);
            setUserData(prev => ({ ...prev, totalVehicles: 0 }));
            setApiStatus(prev => ({ ...prev, vehiclesLoaded: true }));
        }
    };

    const fetchWallet = async () => {
        try {
            const wallet = await getRequest(`/wallets/user/${userId}`);
            setUserData(prev => ({ ...prev, walletBalance: wallet?.balance || 0 }));
            setApiStatus(prev => ({ ...prev, walletAvailable: true }));
        } catch (error) {
            if (error.response?.status === 404) {
                setApiStatus(prev => ({ ...prev, walletAvailable: false }));
            }
            setUserData(prev => ({ ...prev, walletBalance: 0 }));
        }
    };

    const fetchBills = async () => {
        try {
            const bills = await getRequest(`/bills/user/${userId}`);
            const now = new Date();
            const billMonth = `${now.toLocaleString('default', { month: 'long' })} ${now.getFullYear()}`;
            const currentMonthBill = bills.find(b => b.billMonth === billMonth);
            setUserData(prev => ({ ...prev, tollUsedThisMonth: currentMonthBill?.totalAmount || 0 }));
            setApiStatus(prev => ({ ...prev, billsAvailable: true }));
        } catch (error) {
            if (error.response?.status === 404) {
                setApiStatus(prev => ({ ...prev, billsAvailable: false }));
            }
            setUserData(prev => ({ ...prev, tollUsedThisMonth: 0 }));
        }
    };

    return (
        <div className="page user-dashboard">
            {/* Welcome */}
            <div className="welcome-section">
                <h2>👋 Welcome, {userData.name}</h2>
                <p>Manage your vehicles, toll usage, and payments</p>
            </div>

            {loading ? (
                <div className="card">
                    <p className="info-text">Loading your dashboard data...</p>
                </div>
            ) : (
                <>
                    {/* Summary Stats */}
                    <DashboardSummaryCards userData={userData} apiStatus={apiStatus} />

                    {/* Info Notice */}
                    {(!apiStatus.walletAvailable || !apiStatus.billsAvailable) && (
                        <div className="info-notice">
                            <span className="info-icon">ℹ️</span>
                            <span>Wallet and billing data will be available once backend services are initialized.</span>
                        </div>
                    )}

                    {/* My Vehicles */}
                    <DashboardMyVehicles vehicles={myVehicles} />

                    {/* Quick Actions */}
                    <DashboardQuickActions />
                </>
            )}
        </div>
    );
}

export default UserDashboard;
