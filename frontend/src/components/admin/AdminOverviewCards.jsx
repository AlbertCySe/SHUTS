import { memo } from 'react';

const AdminOverviewCards = memo(function AdminOverviewCards({ stats }) {
    return (
        <div className="dashboard-overview">
            <h3 className="section-title">System Overview</h3>
            <div className="overview-grid">
                <div className="overview-card">
                    <div className="card-icon">👥</div>
                    <div className="card-content">
                        <div className="card-value">{stats.totalUsers}</div>
                        <div className="card-label">Total Users</div>
                    </div>
                </div>

                <div className="overview-card">
                    <div className="card-icon">🚗</div>
                    <div className="card-content">
                        <div className="card-value">{stats.totalVehicles}</div>
                        <div className="card-label">Total Vehicles</div>
                    </div>
                </div>

                <div className="overview-card card-success">
                    <div className="card-icon">✅</div>
                    <div className="card-content">
                        <div className="card-value">{stats.activeVehicles}</div>
                        <div className="card-label">Active Vehicles</div>
                    </div>
                </div>

                <div className="overview-card card-danger">
                    <div className="card-icon">⚠️</div>
                    <div className="card-content">
                        <div className="card-value">{stats.vehiclesWithNegativeBalance}</div>
                        <div className="card-label">Negative Balance</div>
                    </div>
                </div>

                <div className="overview-card card-primary">
                    <div className="card-icon">💰</div>
                    <div className="card-content">
                        <div className="card-value">₹{stats.totalTollCollected.toLocaleString('en-IN')}</div>
                        <div className="card-label">Total Toll Collected</div>
                    </div>
                </div>

                <div className="overview-card">
                    <div className="card-icon">🛣️</div>
                    <div className="card-content">
                        <div className="card-value">{stats.totalHighways}</div>
                        <div className="card-label">Total Highways</div>
                    </div>
                </div>
            </div>
        </div>
    );
});

export default AdminOverviewCards;

