import { memo } from 'react';
import { formatTime } from './AdminUtils';

const AdminActivityFeed = memo(function AdminActivityFeed({ activities }) {
    return (
        <div className="dashboard-section">
            <h3 className="section-title">📡 Live Activity Feed</h3>
            <div className="activity-feed">
                {activities.length > 0 ? (
                    activities.map(activity => (
                        <div key={activity.id} className="activity-item">
                            <div className="activity-time">{formatTime(activity.time)}</div>
                            <div className="activity-content">
                                <div className="activity-header">
                                    <span className={`activity-badge activity-${activity.color}`}>
                                        {activity.icon} {activity.type}
                                    </span>
                                    <span className="activity-vehicle">{activity.vehicleNumber}</span>
                                </div>
                                <div className="activity-description">{activity.description}</div>
                            </div>
                        </div>
                    ))
                ) : (
                    <p className="info-text">No recent activity</p>
                )}
            </div>
        </div>
    );
});

export default AdminActivityFeed;

