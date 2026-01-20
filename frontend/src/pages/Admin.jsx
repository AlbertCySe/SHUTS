function Admin() {
    return (
        <div className="page">
            <h2>Admin Dashboard</h2>

            <div className="stats-grid">
                <div className="stat-card">
                    <h3>Total Vehicles</h3>
                    <p className="stat-value">--</p>
                    <p className="stat-label">Registered</p>
                </div>

                <div className="stat-card">
                    <h3>Total Users</h3>
                    <p className="stat-value">--</p>
                    <p className="stat-label">Active</p>
                </div>

                <div className="stat-card">
                    <h3>Total Toll Collected</h3>
                    <p className="stat-value">₹ --</p>
                    <p className="stat-label">All Time</p>
                </div>

                <div className="stat-card">
                    <h3>Wallets in Deficit</h3>
                    <p className="stat-value">--</p>
                    <p className="stat-label">Users</p>
                </div>
            </div>

            <div className="card">
                <h3>Recent Activity</h3>
                <p className="info-text">Activity logs will appear here (API integration pending)</p>
            </div>

            <div className="card">
                <h3>System Health</h3>
                <p className="info-text">Backend: <span className="status-badge">Not Connected</span></p>
                <p className="info-text">Database: <span className="status-badge">Not Connected</span></p>
            </div>
        </div>
    )
}

export default Admin
