import { useState } from 'react';
import { getRequest } from '../services/api';
import './AdminUsersStyles.css';

function AdminReports() {
    const [searchId, setSearchId] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    
    const [reportData, setReportData] = useState(null);
    const [locationLogs, setLocationLogs] = useState([]);

    const fetchReport = async (e) => {
        e.preventDefault();
        if (!searchId) return;

        setLoading(true);
        setError('');
        setReportData(null);
        setLocationLogs([]);

        try {
            // Fetch usage summary (Total DB distance, sessions, and breakdown)
            const summary = await getRequest(`/highway-usage/summary/${searchId}`);
            setReportData(summary);

            // Fetch raw location pings
            const locations = await getRequest(`/locations/vehicle/${searchId}`);
            setLocationLogs(Array.isArray(locations) ? locations : []);

        } catch (err) {
            console.error(err);
            setError(`Could not find report data for Vehicle ID: ${searchId}. Ensure the vehicle exists and has traveled.`);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="page admin-users-page" style={{ maxWidth: '1200px' }}>
            <h2>📋 Vehicle IoT Usage Reports</h2>

            <div className="card" style={{ marginBottom: '24px' }}>
                <h3 style={{ marginTop: 0, marginBottom: '16px' }}>Generate Report</h3>
                <form onSubmit={fetchReport} style={{ display: 'flex', gap: '15px' }}>
                    <input 
                        type="number" 
                        value={searchId}
                        onChange={(e) => setSearchId(e.target.value)}
                        placeholder="Enter Vehicle ID (e.g. 1)"
                        style={{ flex: 1, padding: '12px', borderRadius: '6px', border: '1px solid #ddd', fontSize: '15px' }}
                        required
                        disabled={loading}
                    />
                    <button type="submit" className="admin-btn-add" style={{ padding: '0 30px' }} disabled={loading}>
                        {loading ? 'Generating...' : '📊 Fetch Report'}
                    </button>
                </form>
                {error && <p style={{ color: '#e74c3c', marginTop: '15px', fontWeight: '500' }}>⚠️ {error}</p>}
            </div>

            {reportData && (
                <>
                    {/* Aggregated Summary Dashboard */}
                    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '20px', marginBottom: '24px' }}>
                        <div className="card" style={{ borderLeft: '5px solid #3498db', padding: '20px' }}>
                            <p style={{ margin: 0, color: '#7f8c8d', fontSize: '14px', fontWeight: '600', textTransform: 'uppercase' }}>Total Distance Traveled</p>
                            <h2 style={{ margin: '10px 0 0 0', color: '#2c3e50', fontSize: '32px' }}>
                                {reportData.totalDistance.toFixed(2)} <span style={{ fontSize: '16px', color: '#95a5a6' }}>km</span>
                            </h2>
                        </div>
                        
                        <div className="card" style={{ borderLeft: '5px solid #2ecc71', padding: '20px' }}>
                            <p style={{ margin: 0, color: '#7f8c8d', fontSize: '14px', fontWeight: '600', textTransform: 'uppercase' }}>Highway Sessions</p>
                            <h2 style={{ margin: '10px 0 0 0', color: '#2c3e50', fontSize: '32px' }}>
                                {reportData.totalSessions} <span style={{ fontSize: '16px', color: '#95a5a6' }}>sessions</span>
                            </h2>
                        </div>

                        <div className="card" style={{ borderLeft: '5px solid #f39c12', padding: '20px' }}>
                            <p style={{ margin: 0, color: '#7f8c8d', fontSize: '14px', fontWeight: '600', textTransform: 'uppercase' }}>Highways Explored</p>
                            <h2 style={{ margin: '10px 0 0 0', color: '#2c3e50', fontSize: '32px' }}>
                                {reportData.distanceByHighway ? Object.keys(reportData.distanceByHighway).length : 0} <span style={{ fontSize: '16px', color: '#95a5a6' }}>mapped</span>
                            </h2>
                        </div>
                    </div>

                    {/* Breakdown by Highway */}
                    {reportData.distanceByHighway && Object.keys(reportData.distanceByHighway).length > 0 && (
                        <div className="card" style={{ marginBottom: '24px' }}>
                            <h3 style={{ marginTop: 0 }}>Distance Breakdown by Highway</h3>
                            <div className="table-responsive">
                                <table className="custom-data-table">
                                    <thead>
                                        <tr>
                                            <th>Highway Name</th>
                                            <th>Distance Traveled (km)</th>
                                            <th>Percentage of Total</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {Object.entries(reportData.distanceByHighway).map(([highway, distance]) => (
                                            <tr key={highway}>
                                                <td style={{ fontWeight: '600', color: '#2c3e50' }}>{highway}</td>
                                                <td>{distance.toFixed(2)} km</td>
                                                <td>
                                                    <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                                                        <div style={{ flex: 1, backgroundColor: '#eee', height: '8px', borderRadius: '4px', overflow: 'hidden' }}>
                                                            <div style={{ width: `${(distance / reportData.totalDistance) * 100}%`, backgroundColor: '#3498db', height: '100%' }}></div>
                                                        </div>
                                                        <span style={{ fontSize: '12px', color: '#7f8c8d' }}>{((distance / reportData.totalDistance) * 100).toFixed(1)}%</span>
                                                    </div>
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    )}

                    {/* Raw Location Tracking Logs */}
                    <div className="card">
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
                            <h3 style={{ margin: 0 }}>Raw GPS Tracking Logs</h3>
                            <span className="admin-id-pill">{locationLogs.length} pings</span>
                        </div>
                        
                        <div className="table-responsive">
                            <table className="custom-data-table">
                                <thead>
                                    <tr>
                                        <th>Timestamp</th>
                                        <th>Coordinates (Lat, Lng)</th>
                                        <th>Status</th>
                                        <th>Highway ID</th>
                                        <th>Distance from Prev</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {locationLogs.length === 0 ? (
                                        <tr>
                                            <td colSpan="5" style={{ textAlign: 'center', padding: '20px', color: '#7f8c8d' }}>No GPS pings recorded for this vehicle.</td>
                                        </tr>
                                    ) : (
                                        locationLogs.map((log) => (
                                            <tr key={log.id}>
                                                <td>
                                                    <span style={{ fontWeight: '500' }}>{new Date(log.timestamp).toLocaleDateString()}</span>
                                                    <span style={{ color: '#7f8c8d', marginLeft: '6px', fontSize: '13px' }}>{new Date(log.timestamp).toLocaleTimeString()}</span>
                                                </td>
                                                <td style={{ color: '#2980b9', fontFamily: 'monospace' }}>
                                                    {log.latitude.toFixed(6)}, {log.longitude.toFixed(6)}
                                                </td>
                                                <td>
                                                    {log.isOnHighway ? (
                                                        <span style={{ background: '#e8f8f5', color: '#16a085', padding: '3px 8px', borderRadius: '4px', fontSize: '11px', fontWeight: 'bold' }}>ON HIGHWAY</span>
                                                    ) : (
                                                        <span style={{ background: '#fdf2e9', color: '#e67e22', padding: '3px 8px', borderRadius: '4px', fontSize: '11px', fontWeight: 'bold' }}>LOCAL ROAD</span>
                                                    )}
                                                </td>
                                                <td>{log.highwayId ? <span className="admin-id-pill">#{log.highwayId}</span> : '-'}</td>
                                                <td style={{ color: '#7f8c8d' }}>{log.distanceFromPrevious ? `${log.distanceFromPrevious.toFixed(3)} km` : '-'}</td>
                                            </tr>
                                        ))
                                    )}
                                </tbody>
                            </table>
                        </div>
                    </div>
                </>
            )}
        </div>
    );
}

export default AdminReports;
