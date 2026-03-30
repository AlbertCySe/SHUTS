import { useState, useEffect, useMemo, useCallback } from 'react';
import { getRequest } from '../services/api';
import AdminHealthCards from '../components/admin/AdminHealthCards';
import AdminOverviewCards from '../components/admin/AdminOverviewCards';
import AdminAlerts from '../components/admin/AdminAlerts';
import AdminActivityFeed from '../components/admin/AdminActivityFeed';
import AdminVehiclesTable from '../components/admin/AdminVehiclesTable';
import AdminStatistics from '../components/admin/AdminStatistics';
import AdminInsights from '../components/admin/AdminInsights';
import {
    generateRandomStatus,
    generateActivities,
    calculateStats,
    getVehicleDistribution,
    getMonthlyTollSummary,
    getEnhancedAlerts,
    calculateOperationalInsights
} from '../components/admin/AdminUtils';


function Admin() {
    // State for all data
    const [users, setUsers] = useState([]);
    const [vehicles, setVehicles] = useState([]);
    const [wallets, setWallets] = useState([]);
    const [highways, setHighways] = useState([]);
    const [bills, setBills] = useState([]);
    const [loading, setLoading] = useState(true);

    // Vehicles pagination state (server-side)
    const [vehiclesPage, setVehiclesPage] = useState([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [totalVehicles, setTotalVehicles] = useState(0);
    const [vehiclesLoading, setVehiclesLoading] = useState(false);
    const pageSize = 20;

    // Search and filter state for vehicles table (removed - now server-side)
    const [vehicleSearch, setVehicleSearch] = useState('');
    const [vehicleStatusFilter, setVehicleStatusFilter] = useState('All');

    // System health state (simulated real-time)
    const [systemHealth, setSystemHealth] = useState({
        backendAPI: { status: 'online', label: 'Online' },
        database: { status: 'online', label: 'Connected' },
        gpsFeed: { status: 'online', label: 'Active' },
        walletService: { status: 'online', label: 'Operational' }
    });

    // Live activity feed state
    const [activities, setActivities] = useState([]);

    // Fetch all data on mount
    useEffect(() => {
        fetchDashboardData();
    }, []);

    // Fetch paginated vehicles when page changes
    useEffect(() => {
        fetchVehiclesPaginated(currentPage);
    }, [currentPage]);

    // Simulate real-time system health updates (every 10 seconds)
    useEffect(() => {
        const healthInterval = setInterval(() => {
            const getRandomLabel = (status) => {
                if (status === 'online') return ['Online', 'Connected', 'Active', 'Operational'][Math.floor(Math.random() * 4)];
                if (status === 'warning') return ['Degraded', 'Slow', 'Delayed', 'High Load'][Math.floor(Math.random() * 4)];
                return ['Offline', 'Disconnected', 'Inactive', 'Down'][Math.floor(Math.random() * 4)];
            };

            const backendStatus = generateRandomStatus();
            const dbStatus = generateRandomStatus();
            const gpsStatus = generateRandomStatus();
            const walletStatus = generateRandomStatus();

            setSystemHealth({
                backendAPI: { status: backendStatus, label: getRandomLabel(backendStatus) },
                database: { status: dbStatus, label: getRandomLabel(dbStatus) },
                gpsFeed: { status: gpsStatus, label: getRandomLabel(gpsStatus) },
                walletService: { status: walletStatus, label: getRandomLabel(walletStatus) }
            });
        }, 10000);

        return () => clearInterval(healthInterval);
    }, []);

    // Generate and refresh activity feed (every 15 seconds)
    useEffect(() => {
        if (vehicles.length > 0) {
            setActivities(generateActivities(vehicles));
        }

        const activityInterval = setInterval(() => {
            if (vehicles.length > 0) {
                setActivities(generateActivities(vehicles));
            }
        }, 15000);

        return () => clearInterval(activityInterval);
    }, [vehicles]);

    const fetchDashboardData = async () => {
        setLoading(true);
        try {
            const [usersData, walletsData, highwaysData, billsData] = await Promise.allSettled([
                getRequest('/users'),
                getRequest('/wallets'),
                getRequest('/highways'),
                getRequest('/bills')
            ]);

            setUsers(usersData.status === 'fulfilled' ? usersData.value : []);
            setWallets(walletsData.status === 'fulfilled' ? walletsData.value : []);
            setHighways(highwaysData.status === 'fulfilled' ? highwaysData.value : []);
            setBills(billsData.status === 'fulfilled' ? billsData.value : []);
        } catch (err) {
            console.error('Error fetching dashboard data:', err);
        } finally {
            setLoading(false);
        }
    };

    // Fetch vehicles with pagination from optimized admin endpoint
    const fetchVehiclesPaginated = async (page) => {
        setVehiclesLoading(true);
        try {
            const response = await getRequest(`/admin/vehicles/paginated?page=${page}&size=${pageSize}`);

            // Response is Page<VehicleAdminDTO> with content and pagination metadata
            setVehiclesPage(response.content || []);
            setTotalPages(response.totalPages || 0);
            setTotalVehicles(response.totalElements || 0);

            // Also store in vehicles for other components that need all vehicles data
            // Note: This is only the current page, not all vehicles
            setVehicles(response.content || []);
        } catch (err) {
            console.error('Error fetching paginated vehicles:', err);
            setVehiclesPage([]);
            setVehicles([]);
        } finally {
            setVehiclesLoading(false);
        }
    };

    // Page change handler
    const handlePageChange = useCallback((newPage) => {
        if (newPage >= 0 && newPage < totalPages) {
            setCurrentPage(newPage);
        }
    }, [totalPages]);

    // Note: Client-side filtering removed - now handled server-side
    // vehiclesPage already contains paginated data from backend

    // Calculate statistics (memoized)
    const stats = useMemo(
        () => calculateStats(users, vehicles, wallets, bills, highways),
        [users, vehicles, wallets, bills, highways]
    );

    // Get enhanced alerts (memoized)
    const enhancedAlerts = useMemo(
        () => getEnhancedAlerts(vehicles, wallets),
        [vehicles, wallets]
    );

    // Get vehicle distribution and toll summary (memoized)
    const vehicleDistribution = useMemo(
        () => getVehicleDistribution(vehicles),
        [vehicles]
    );

    const monthlyTollSummary = useMemo(
        () => getMonthlyTollSummary(bills),
        [bills]
    );

    // Calculate operational insights (memoized)
    const operationalInsights = useMemo(
        () => calculateOperationalInsights(vehicles, bills),
        [vehicles, bills]
    );


    // Use paginated vehicles directly from server
    const displayVehicles = vehiclesPage;

    // Handle vehicle actions (UI-only) - memoized callbacks
    const handleSuspendVehicle = useCallback((vehicleId) => {
        setVehicles(prev => prev.map(v =>
            v.vehicleId === vehicleId ? { ...v, status: 'SUSPENDED' } : v
        ));
    }, []);

    const handleActivateVehicle = useCallback((vehicleId) => {
        setVehicles(prev => prev.map(v =>
            v.vehicleId === vehicleId ? { ...v, status: 'ACTIVE' } : v
        ));
    }, []);

    const handleViewDetails = useCallback((vehicleId) => {
        alert(`Viewing details for vehicle ID: ${vehicleId}`);
    }, []);

    if (loading) {
        return (
            <div className="page">
                <h2>Monitoring Console</h2>
                <p className="info-text">Loading dashboard data...</p>
            </div>
        );
    }

    return (
        <div className="page">
            <h2>🎛️ Operations Monitoring Console</h2>
            <p style={{ color: '#666', marginBottom: '25px' }}>
                Real-time system monitoring and control panel
            </p>

            <AdminHealthCards systemHealth={systemHealth} />
            <AdminOverviewCards stats={stats} />
            <AdminAlerts alerts={enhancedAlerts} />
            <AdminActivityFeed activities={activities} />
            <AdminVehiclesTable
                vehicles={displayVehicles}
                vehicleSearch={vehicleSearch}
                setVehicleSearch={setVehicleSearch}
                vehicleStatusFilter={vehicleStatusFilter}
                setVehicleStatusFilter={setVehicleStatusFilter}
                onSuspend={handleSuspendVehicle}
                onActivate={handleActivateVehicle}
                onViewDetails={handleViewDetails}
                loading={vehiclesLoading}
                currentPage={currentPage}
                totalPages={totalPages}
                totalVehicles={totalVehicles}
                onPageChange={handlePageChange}
            />
            <AdminStatistics
                vehicleDistribution={vehicleDistribution}
                monthlyTollSummary={monthlyTollSummary}
                totalVehicles={vehicles.length}
            />
            <AdminInsights insights={operationalInsights} />
        </div>
    );
}

export default Admin;

