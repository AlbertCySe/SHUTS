function Home() {
    return (
        <div className="page">
            <h2>Welcome to Smart Highway Tolling System</h2>
            <div className="card">
                <h3>📌 About the System</h3>
                <p>
                    This system calculates highway toll charges based on the actual distance
                    traveled by vehicles on national highways, rather than fixed toll booth charges.
                </p>
            </div>

            <div className="card">
                <h3>🎯 Key Features</h3>
                <ul>
                    <li>Distance-based toll calculation</li>
                    <li>GPS location tracking</li>
                    <li>Digital wallet management</li>
                    <li>Automated monthly billing</li>
                    <li>Admin monitoring dashboard</li>
                </ul>
            </div>

            <div className="card">
                <h3>🚀 Getting Started</h3>
                <p>Use the navigation menu above to:</p>
                <ul>
                    <li><strong>Users:</strong> Register and manage users</li>
                    <li><strong>Vehicles:</strong> Add and view vehicles</li>
                    <li><strong>Highways:</strong> Configure highway details</li>
                    <li><strong>Admin:</strong> View system statistics</li>
                </ul>
            </div>
        </div>
    )
}

export default Home
