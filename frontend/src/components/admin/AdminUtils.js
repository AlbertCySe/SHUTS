// Utility functions for Admin Dashboard

// Format relative time for activity feed
export const formatTime = (date) => {
    const now = new Date();
    const diff = Math.floor((now - date) / 1000); // seconds

    if (diff < 60) return 'Just now';
    if (diff < 3600) return `${Math.floor(diff / 60)}m ago`;
    if (diff < 86400) return `${Math.floor(diff / 3600)}h ago`;
    return date.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' });
};

// Generate random status for health monitoring
export const generateRandomStatus = () => {
    const statuses = ['online', 'warning', 'offline'];
    // 80% chance of online, 15% warning, 5% offline
    const random = Math.random() * 10;
    if (random > 8) return statuses[Math.floor(Math.random() * 3)];
    return 'online';
};

// Generate mock activities from vehicle data
export const generateActivities = (vehicles) => {
    const activityTypes = [
        { type: 'GPS Update', icon: '📍', color: 'info' },
        { type: 'Bill Generated', icon: '📄', color: 'success' },
        { type: 'Vehicle Suspended', icon: '🚫', color: 'danger' },
        { type: 'Recharge', icon: '💰', color: 'primary' }
    ];

    const newActivities = [];
    const now = new Date();

    for (let i = 0; i < 10; i++) {
        const type = activityTypes[Math.floor(Math.random() * activityTypes.length)];
        const vehicle = vehicles[Math.floor(Math.random() * Math.max(vehicles.length, 1))];
        const time = new Date(now.getTime() - (i * 2 * 60000)); // 2 minutes apart

        let description = '';
        const vehicleNum = vehicle?.vehicleNumber || 'N/A';

        if (type.type === 'GPS Update') {
            description = `Location updated to coordinates (${(Math.random() * 90).toFixed(4)}, ${(Math.random() * 180).toFixed(4)})`;
        } else if (type.type === 'Bill Generated') {
            description = `Monthly bill of ₹${(Math.random() * 1000 + 100).toFixed(2)} generated`;
        } else if (type.type === 'Vehicle Suspended') {
            description = `Vehicle suspended due to policy violation`;
        } else if (type.type === 'Recharge') {
            description = `Wallet recharged with ₹${(Math.random() * 500 + 100).toFixed(2)}`;
        }

        newActivities.push({
            id: `activity-${i}-${Date.now()}`,
            time,
            type: type.type,
            icon: type.icon,
            color: type.color,
            vehicleNumber: vehicleNum,
            description
        });
    }

    return newActivities;
};

// Calculate statistics from data
export const calculateStats = (users, vehicles, wallets, bills, highways) => {
    return {
        totalUsers: users.length,
        totalVehicles: vehicles.length,
        activeVehicles: vehicles.filter(v => v.status === 'ACTIVE').length,
        vehiclesWithNegativeBalance: wallets.filter(w => w.balance < 0).length,
        totalTollCollected: bills.reduce((sum, bill) => sum + (bill.totalAmount || 0), 0),
        totalHighways: highways.length
    };
};

// Get vehicle distribution by type
export const getVehicleDistribution = (vehicles) => {
    return vehicles.reduce((acc, vehicle) => {
        const type = vehicle.vehicleType || 'UNKNOWN';
        acc[type] = (acc[type] || 0) + 1;
        return acc;
    }, {});
};

// Get monthly toll summary
export const getMonthlyTollSummary = (bills) => {
    const summary = bills.reduce((acc, bill) => {
        const month = bill.billMonth || 'Unknown';
        acc[month] = (acc[month] || 0) + (bill.totalAmount || 0);
        return acc;
    }, {});

    return Object.entries(summary)
        .sort((a, b) => b[0].localeCompare(a[0]))
        .slice(0, 3);
};

// Get enhanced alerts with severity
export const getEnhancedAlerts = (vehicles, wallets) => {
    const alertsList = [];

    // Critical: Negative balance vehicles
    if (vehicles.length > 0) {
        const negativeBalanceVehicles = vehicles.filter(v => {
            const wallet = wallets.find(w => w.userId === v.userId);
            return wallet && wallet.balance < 0;
        });

        if (negativeBalanceVehicles.length > 0) {
            alertsList.push({
                severity: 'critical',
                icon: '🔴',
                title: 'Vehicles with Negative Balance',
                count: negativeBalanceVehicles.length,
                items: negativeBalanceVehicles,
                color: 'danger'
            });
        }
    }

    // Warning: Suspended vehicles
    const suspendedVehicles = vehicles.filter(v => v.status === 'SUSPENDED');
    if (suspendedVehicles.length > 0) {
        alertsList.push({
            severity: 'warning',
            icon: '⚠️',
            title: 'Suspended Vehicles',
            count: suspendedVehicles.length,
            items: suspendedVehicles,
            color: 'warning'
        });
    }

    // Info: Low balance vehicles (< 100)
    const lowBalanceVehicles = vehicles.filter(v => {
        const wallet = wallets.find(w => w.userId === v.userId);
        return wallet && wallet.balance >= 0 && wallet.balance < 100;
    });

    if (lowBalanceVehicles.length > 0) {
        alertsList.push({
            severity: 'info',
            icon: 'ℹ️',
            title: 'Low Balance Vehicles',
            count: lowBalanceVehicles.length,
            items: lowBalanceVehicles,
            color: 'info'
        });
    }

    // Sort by severity: critical > warning > info
    const severityOrder = { critical: 0, warning: 1, info: 2 };
    return alertsList.sort((a, b) => severityOrder[a.severity] - severityOrder[b.severity]);
};

// Calculate operational insights
export const calculateOperationalInsights = (vehicles, bills) => {
    // Get current and previous month totals
    const monthlyTotals = bills.reduce((acc, bill) => {
        const month = bill.billMonth || 'Unknown';
        acc[month] = (acc[month] || 0) + (bill.totalAmount || 0);
        return acc;
    }, {});

    const sortedMonths = Object.entries(monthlyTotals)
        .sort((a, b) => b[0].localeCompare(a[0]));

    const currentMonthTotal = sortedMonths[0]?.[1] || 0;
    const previousMonthTotal = sortedMonths[1]?.[1] || 0;

    // Revenue growth
    let revenueGrowth = 0;
    let revenueDirection = 'neutral';
    if (previousMonthTotal > 0) {
        revenueGrowth = ((currentMonthTotal - previousMonthTotal) / previousMonthTotal) * 100;
        revenueDirection = revenueGrowth > 0 ? 'up' : revenueGrowth < 0 ? 'down' : 'neutral';
    }

    // Active vehicle ratio
    const totalVehicles = vehicles.length || 1;
    const activeVehicles = vehicles.filter(v => v.status === 'ACTIVE').length;
    const activeRatio = (activeVehicles / totalVehicles) * 100;

    // Suspension rate
    const suspendedVehicles = vehicles.filter(v => v.status === 'SUSPENDED').length;
    const suspensionRate = (suspendedVehicles / totalVehicles) * 100;

    // Average toll per vehicle
    const totalToll = bills.reduce((sum, bill) => sum + (bill.totalAmount || 0), 0);
    const avgTollPerVehicle = totalVehicles > 0 ? totalToll / totalVehicles : 0;

    return {
        revenueGrowth: {
            percentage: revenueGrowth,
            direction: revenueDirection,
            currentMonth: currentMonthTotal,
            previousMonth: previousMonthTotal
        },
        activeRatio: {
            percentage: activeRatio,
            active: activeVehicles,
            total: totalVehicles
        },
        suspensionRate: {
            percentage: suspensionRate,
            suspended: suspendedVehicles,
            total: totalVehicles,
            isHigh: suspensionRate > 10
        },
        avgTollPerVehicle: {
            amount: avgTollPerVehicle,
            totalToll,
            totalVehicles
        }
    };
};

