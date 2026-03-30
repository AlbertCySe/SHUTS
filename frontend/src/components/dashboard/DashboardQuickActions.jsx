import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import ProfileRequestModal from './ProfileRequestModal';

function DashboardQuickActions() {
    const navigate = useNavigate();
    const [showRequestModal, setShowRequestModal] = useState(false);

    return (
        <>
            <div className="card quick-actions">
                <h3>⚡ Quick Actions</h3>
                <div className="actions-grid">
                    <button className="action-btn" onClick={() => navigate('/toll-history')}>
                        <span className="action-icon">📊</span>
                        <span className="action-text">View Toll History</span>
                    </button>
                    <button className="action-btn" onClick={() => navigate('/wallet-bills')}>
                        <span className="action-icon">💳</span>
                        <span className="action-text">Wallet &amp; Bills</span>
                    </button>
                    <button className="action-btn" onClick={() => setShowRequestModal(true)}>
                        <span className="action-icon">👤</span>
                        <span className="action-text">Request Profile Update</span>
                    </button>
                    <button className="action-btn" onClick={() => navigate('/vehicles')}>
                        <span className="action-icon">🚗</span>
                        <span className="action-text">Manage Vehicles</span>
                    </button>
                </div>
            </div>

            {showRequestModal && (
                <ProfileRequestModal onClose={() => setShowRequestModal(false)} />
            )}
        </>
    );
}

export default DashboardQuickActions;
