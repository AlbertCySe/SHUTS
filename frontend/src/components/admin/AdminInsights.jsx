import { memo } from 'react';

const AdminInsights = memo(function AdminInsights({ insights }) {
    return (
        <div className="dashboard-section">
            <h3 className="section-title">📈 Operational Insights</h3>

            <div className="insights-grid">
                {/* Revenue Growth Indicator */}
                <div className="insight-card">
                    <div className="insight-header">
                        <span className="insight-icon">💰</span>
                        <span className="insight-title">Revenue Growth</span>
                    </div>
                    <div className="insight-body">
                        <div className={`insight-value ${insights.revenueGrowth.direction}`}>
                            {insights.revenueGrowth.direction === 'up' && '↑'}
                            {insights.revenueGrowth.direction === 'down' && '↓'}
                            {Math.abs(insights.revenueGrowth.percentage).toFixed(1)}%
                        </div>
                        <div className="insight-label">
                            {insights.revenueGrowth.direction === 'up' ? 'Increase' :
                                insights.revenueGrowth.direction === 'down' ? 'Decrease' : 'No Change'}
                        </div>
                        <div className="insight-subtext">
                            Current: ₹{insights.revenueGrowth.currentMonth.toLocaleString('en-IN')}
                        </div>
                    </div>
                </div>

                {/* Active Vehicle Ratio */}
                <div className="insight-card">
                    <div className="insight-header">
                        <span className="insight-icon">✅</span>
                        <span className="insight-title">Active Vehicle Ratio</span>
                    </div>
                    <div className="insight-body">
                        <div className="insight-value">
                            {insights.activeRatio.percentage.toFixed(1)}%
                        </div>
                        <div className="insight-progress">
                            <div
                                className="insight-progress-bar"
                                style={{ width: `${insights.activeRatio.percentage}%` }}
                            ></div>
                        </div>
                        <div className="insight-subtext">
                            {insights.activeRatio.active} of {insights.activeRatio.total} vehicles active
                        </div>
                    </div>
                </div>

                {/* Suspension Rate */}
                <div className={`insight-card ${insights.suspensionRate.isHigh ? 'insight-warning' : ''}`}>
                    <div className="insight-header">
                        <span className="insight-icon">🚫</span>
                        <span className="insight-title">Suspension Rate</span>
                    </div>
                    <div className="insight-body">
                        <div className={`insight-value ${insights.suspensionRate.isHigh ? 'danger' : ''}`}>
                            {insights.suspensionRate.percentage.toFixed(1)}%
                        </div>
                        <div className="insight-label">
                            {insights.suspensionRate.isHigh ? 'Above Threshold' : 'Normal'}
                        </div>
                        <div className="insight-subtext">
                            {insights.suspensionRate.suspended} suspended vehicles
                        </div>
                    </div>
                </div>

                {/* Average Toll Per Vehicle */}
                <div className="insight-card">
                    <div className="insight-header">
                        <span className="insight-icon">📊</span>
                        <span className="insight-title">Avg Toll Per Vehicle</span>
                    </div>
                    <div className="insight-body">
                        <div className="insight-value">
                            ₹{insights.avgTollPerVehicle.amount.toLocaleString('en-IN', {
                                minimumFractionDigits: 2,
                                maximumFractionDigits: 2
                            })}
                        </div>
                        <div className="insight-label">Per Vehicle</div>
                        <div className="insight-subtext">
                            Total: ₹{insights.avgTollPerVehicle.totalToll.toLocaleString('en-IN')}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
});

export default AdminInsights;
