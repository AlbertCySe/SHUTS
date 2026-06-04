import { useState, useEffect } from 'react';
import { getRequest } from '../services/api';
import { getSession } from '../services/auth';
import DashboardSummaryCards from '../components/dashboard/DashboardSummaryCards';
import DashboardMyVehicles from '../components/dashboard/DashboardMyVehicles';
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
        lastHighwayUsed: 'No data available',
        totalHighwayDistance: 0
    });
    const [myVehicles, setMyVehicles] = useState([]);
    const [activeSimIds, setActiveSimIds] = useState([]);
    const [apiStatus, setApiStatus] = useState({
        userProfileLoaded: false,
        walletAvailable: true,
        billsAvailable: true,
        vehiclesLoaded: false
    });

    useEffect(() => {
        let isMounted = true;

        const initialLoad = async () => {
            setLoading(true);
            await fetchDashboardData();
            if (isMounted) setLoading(false);
        };

        initialLoad();

        // Check standalone simulator active simulation IDs every 5 seconds
        const fetchSimStatus = async () => {
            try {
                const res = await fetch('http://localhost:8082/api/simulation/status');
                if (res.ok && isMounted) {
                    const data = await res.json();
                    setActiveSimIds(data.activeVehicleIds || []);
                }
            } catch {
                if (isMounted) setActiveSimIds([]);
            }
        };
        fetchSimStatus();

        // Real-time polling every 5 seconds
        const pollInterval = setInterval(() => {
            if (isMounted) {
                fetchDashboardData();
                fetchSimStatus();
            }
        }, 5000);

        return () => {
            isMounted = false;
            clearInterval(pollInterval);
        };
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [userId]);


    const fetchDashboardData = async () => {
        await Promise.all([
            fetchUserProfile(),
            fetchVehicles(),
            fetchWallet(),
            fetchBills(),
        ]);
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

            // Fetch highway usage summaries to find the last highway used and total distance
            let foundHighway = null;
            let totalDist = 0;
            for (const v of userVehicles) {
                try {
                    const summary = await getRequest(`/highway-usage/summary/${v.vehicleId}`);
                    if (summary) {
                        totalDist += (summary.totalDistance || 0);
                        if (summary.distanceByHighway) {
                            const highways = Object.keys(summary.distanceByHighway);
                            if (highways.length > 0) {
                                foundHighway = highways[highways.length - 1]; // Get latest/any
                            }
                        }
                    }
                } catch (e) {
                    // Ignore individual summary fetch errors
                }
            }
            
            if (foundHighway) {
                setUserData(prev => ({ ...prev, lastHighwayUsed: foundHighway, totalHighwayDistance: totalDist }));
            } else {
                setUserData(prev => ({ ...prev, lastHighwayUsed: 'None yet', totalHighwayDistance: 0 }));
            }

        } catch {
            setMyVehicles([]);
            setUserData(prev => ({ ...prev, totalVehicles: 0, lastHighwayUsed: 'N/A' }));
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
                    <DashboardMyVehicles vehicles={myVehicles} activeSimIds={activeSimIds} />
                </>
            )}
        </div>

    );
}

export default UserDashboard;
