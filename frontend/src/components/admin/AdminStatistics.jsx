import { memo } from 'react';

const AdminStatistics = memo(function AdminStatistics({ vehicleDistribution, monthlyTollSummary, totalVehicles }) {
    return (
        <div className="dashboard-section">
            <h3 className="section-title">📊 Monitoring Statistics</h3>

            <div className="stats-row">
                {/* Vehicle Distribution */}
                <div className="stat-block">
                    <h4>Vehicle Distribution by Type</h4>
                    <div className="stat-items">
                        {Object.entries(vehicleDistribution).length > 0 ? (
                            Object.entries(vehicleDistribution).map(([type, count]) => (
                                <div key={type} className="stat-item">
                                    <div className="stat-item-label">{type}</div>
                                    <div className="stat-item-bar">
                                        <div
                                            className="stat-item-fill"
                                            style={{ width: `${(count / totalVehicles) * 100}%` }}
                                        ></div>
                                    </div>
                                    <div className="stat-item-value">
                                        {count} ({((count / totalVehicles) * 100).toFixed(1)}%)
                                    </div>
                                </div>
                            ))
                        ) : (
                            <p className="info-text">No vehicle data available</p>
                        )}
                    </div>
                </div>

                {/* Monthly Toll Summary */}
                <div className="stat-block">
                    <h4>Recent Monthly Toll Summary</h4>
                    <div className="stat-items">
                        {monthlyTollSummary.length > 0 ? (
                            monthlyTollSummary.map(([month, amount]) => (
                                <div key={month} className="stat-item">
                                    <div className="stat-item-label">{month}</div>
                                    <div className="stat-item-bar">
                                        <div
                                            className="stat-item-fill stat-fill-primary"
                                            style={{
                                                width: `${(amount / Math.max(...monthlyTollSummary.map(m => m[1]))) * 100}%`
                                            }}
                                        ></div>
                                    </div>
                                    <div className="stat-item-value">₹{amount.toLocaleString('en-IN')}</div>
                                </div>
                            ))
                        ) : (
                            <p className="info-text">No billing data available</p>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
});

export default AdminStatistics;

