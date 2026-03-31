import React from 'react';

function DashboardSummaryCards({ userData, apiStatus }) {
    return (
        <div className="summary-grid">
            <div className="summary-card card-vehicles">
                <div className="summary-icon">🚗</div>
                <div className="summary-content">
                    <h3>{userData.totalVehicles}</h3>
                    <p>Total Vehicles Registered</p>
                </div>
            </div>

            <div className="summary-card card-wallet">
                <div className="summary-icon">💰</div>
                <div className="summary-content">
                    <h3>
                        ₹{userData.walletBalance.toLocaleString('en-IN', { minimumFractionDigits: 2 })}
                        {!apiStatus.walletAvailable && <span className="status-text"> (Not initialized)</span>}
                    </h3>
                    <p>Current Wallet Balance</p>
                </div>
            </div>

            <div className="summary-card card-toll">
                <div className="summary-icon">💳</div>
                <div className="summary-content">
                    <h3>₹{userData.tollUsedThisMonth.toLocaleString('en-IN', { minimumFractionDigits: 2 })}</h3>
                    <p>Toll Used This Month</p>
                </div>
            </div>

            <div className="summary-card card-highway">
                <div className="summary-icon">🛣️</div>
                <div className="summary-content">
                    <h3 className="highway-text">{userData.lastHighwayUsed}</h3>
                    <p>Last Highway Used</p>
                </div>
            </div>

            <div className="summary-card card-distance">
                <div className="summary-icon">📏</div>
                <div className="summary-content">
                    <h3>{(userData.totalHighwayDistance || 0).toFixed(1)} km</h3>
                    <p>Total Highway Distance</p>
                </div>
            </div>
        </div>
    );
}

export default DashboardSummaryCards;
