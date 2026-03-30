import { memo } from 'react';

const AdminAlerts = memo(function AdminAlerts({ alerts }) {
    return (
        <div className="dashboard-section">
            <h3 className="section-title">
                🚨 System Alerts
                {alerts.length > 0 && (
                    <span className="alerts-total-badge">
                        {alerts.reduce((sum, a) => sum + a.count, 0)}
                    </span>
                )}
            </h3>
            {alerts.length === 0 ? (
                <div className="alert-empty-state">
                    <div className="empty-icon">✅</div>
                    <div className="empty-text">No active alerts. All systems operating normally.</div>
                </div>
            ) : (
                <div className="alerts-grid">
                    {alerts.map((alert, index) => (
                        <div key={index} className={`alert-card alert-${alert.color} alert-severity-${alert.severity}`}>
                            <div className="alert-header">
                                <span className="alert-icon">{alert.icon}</span>
                                <span className="alert-title">{alert.title}</span>
                                <span className={`alert-count badge-${alert.severity}`}>{alert.count}</span>
                            </div>
                            <div className="alert-severity-label">{alert.severity.toUpperCase()}</div>
                            <div className="alert-body">
                                {alert.items.slice(0, 5).map(vehicle => (
                                    <div key={vehicle.vehicleId} className="alert-item">
                                        <span className="alert-vehicle">{vehicle.vehicleNumber}</span>
                                        <span className="alert-type">{vehicle.vehicleType}</span>
                                    </div>
                                ))}
                                {alert.items.length > 5 && (
                                    <div className="alert-more">+{alert.items.length - 5} more</div>
                                )}
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
});

export default AdminAlerts;

