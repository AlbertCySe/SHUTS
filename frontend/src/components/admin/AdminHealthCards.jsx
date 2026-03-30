import { memo } from 'react';

const AdminHealthCards = memo(function AdminHealthCards({ systemHealth }) {
    return (
        <div className="health-overview">
            <h3 className="section-title">System Health Overview</h3>
            <div className="health-grid">
                <div className="health-card">
                    <div className={`status-dot status-${systemHealth.backendAPI.status}`}></div>
                    <div className="health-content">
                        <div className="health-label">Backend API</div>
                        <div className="health-status">{systemHealth.backendAPI.label}</div>
                    </div>
                </div>

                <div className="health-card">
                    <div className={`status-dot status-${systemHealth.database.status}`}></div>
                    <div className="health-content">
                        <div className="health-label">Database Connection</div>
                        <div className="health-status">{systemHealth.database.label}</div>
                    </div>
                </div>

                <div className="health-card">
                    <div className={`status-dot status-${systemHealth.gpsFeed.status}`}></div>
                    <div className="health-content">
                        <div className="health-label">GPS Feed</div>
                        <div className="health-status">{systemHealth.gpsFeed.label}</div>
                    </div>
                </div>

                <div className="health-card">
                    <div className={`status-dot status-${systemHealth.walletService.status}`}></div>
                    <div className="health-content">
                        <div className="health-label">Wallet Service</div>
                        <div className="health-status">{systemHealth.walletService.label}</div>
                    </div>
                </div>
            </div>
        </div>
    );
});

export default AdminHealthCards;

