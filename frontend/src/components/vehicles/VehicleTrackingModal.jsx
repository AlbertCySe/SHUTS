import React, { useState, useEffect, useRef, useCallback } from 'react';
import { getRequest } from '../../services/api';
import './VehicleTrackingModal.css';

const POLL_INTERVAL_MS = 3000; // poll every 3 seconds for near-real-time feel
const SIM_STATUS_URL = 'http://localhost:8082/api/simulation/status';

function getDataAge(timestamp) {
    if (!timestamp) return null;
    const now = new Date();
    const then = new Date(timestamp);
    const diffSec = Math.floor((now - then) / 1000);
    if (diffSec < 60) return `${diffSec}s ago`;
    if (diffSec < 3600) return `${Math.floor(diffSec / 60)}m ago`;
    return `${Math.floor(diffSec / 3600)}h ago`;
}

function isDataFresh(timestamp) {
    if (!timestamp) return false;
    const diffMs = Date.now() - new Date(timestamp).getTime();
    return diffMs < 30000; // fresh if within last 30 seconds
}

function getSpeedColor(speed) {
    if (speed == null) return '#7f8c8d';
    if (speed < 30) return '#27ae60';
    if (speed < 80) return '#f39c12';
    if (speed < 120) return '#e67e22';
    return '#e74c3c';
}

function getStatusColor(status) {
    const s = (status || '').toLowerCase();
    if (s.includes('moving') || s.includes('driving')) return '#27ae60';
    if (s.includes('idle') || s.includes('stop')) return '#f39c12';
    if (s.includes('enter') || s.includes('highway')) return '#3498db';
    return '#7f8c8d';
}

function SpeedGauge({ speed }) {
    const maxSpeed = 160;
    const pct = Math.min(100, Math.max(0, ((speed || 0) / maxSpeed) * 100));
    const color = getSpeedColor(speed);
    const degrees = (pct / 100) * 180;

    return (
        <div className="vtm-gauge-wrap">
            <svg viewBox="0 0 120 70" className="vtm-gauge-svg">
                {/* Track arc */}
                <path d="M10,65 A55,55 0 0,1 110,65" fill="none" stroke="#e8ecef" strokeWidth="10" strokeLinecap="round" />
                {/* Filled arc */}
                <path
                    d="M10,65 A55,55 0 0,1 110,65"
                    fill="none"
                    stroke={color}
                    strokeWidth="10"
                    strokeLinecap="round"
                    strokeDasharray={`${(pct / 100) * 172.8} 172.8`}
                    style={{ transition: 'stroke-dasharray 0.6s ease, stroke 0.4s ease' }}
                />
                {/* Needle */}
                <line
                    x1="60" y1="65"
                    x2={60 + 40 * Math.cos(((180 - degrees) * Math.PI) / 180)}
                    y2={65 - 40 * Math.sin(((180 - degrees) * Math.PI) / 180)}
                    stroke={color} strokeWidth="2.5" strokeLinecap="round"
                    style={{ transition: 'x2 0.6s ease, y2 0.6s ease' }}
                />
                <circle cx="60" cy="65" r="4" fill={color} />
                <text x="60" y="57" textAnchor="middle" fontSize="11" fontWeight="700" fill={color}>
                    {speed != null ? Math.round(speed) : '—'}
                </text>
                <text x="60" y="67" textAnchor="middle" fontSize="7" fill="#7f8c8d">km/h</text>
            </svg>
        </div>
    );
}

function FreshnessBadge({ timestamp }) {
    const fresh = isDataFresh(timestamp);
    const age = getDataAge(timestamp);

    return (
        <div className={`vtm-freshness ${fresh ? 'vtm-fresh' : 'vtm-stale'}`}>
            <span className={`vtm-pulse-dot ${fresh ? 'vtm-dot-green' : 'vtm-dot-grey'}`} />
            {fresh ? '🟢 Live Data' : `🔴 Last seen: ${age || '—'}`}
        </div>
    );
}

function VehicleTrackingModal({ vehicle, onClose }) {
    const [location, setLocation] = useState(null);
    const [history, setHistory] = useState([]);
    const [usageSummary, setUsageSummary] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [lastFetchedAt, setLastFetchedAt] = useState(null);
    const [countdown, setCountdown] = useState(POLL_INTERVAL_MS / 1000);
    const [simulating, setSimulating] = useState(false);
    const intervalRef = useRef(null);
    const countdownRef = useRef(null);

    const vehicleId = vehicle?.vehicleId;
    const [liveIotData, setLiveIotData] = useState(null); // real-time data from standalone simulator

    const fetchData = useCallback(async () => {
        try {
            setError(null);
            const [locData, histData, summaryData] = await Promise.allSettled([
                getRequest(`/locations/vehicle/${vehicleId}/latest`),
                getRequest(`/locations/vehicle/${vehicleId}/history`),
                getRequest(`/highway-usage/summary/${vehicleId}`),
            ]);

            if (locData.status === 'fulfilled') setLocation(locData.value);
            if (histData.status === 'fulfilled') setHistory(histData.value || []);
            if (summaryData.status === 'fulfilled') setUsageSummary(summaryData.value);
            setLastFetchedAt(new Date());
        } catch (err) {
            setError('Could not reach backend. Is it running?');
        } finally {
            setLoading(false);
            setCountdown(POLL_INTERVAL_MS / 1000);
        }
    }, [vehicleId]);

    // Initial fetch + polling
    useEffect(() => {
        fetchData();
        intervalRef.current = setInterval(fetchData, POLL_INTERVAL_MS);
        countdownRef.current = setInterval(() => {
            setCountdown(c => (c > 1 ? c - 1 : POLL_INTERVAL_MS / 1000));
        }, 1000);
        return () => {
            clearInterval(intervalRef.current);
            clearInterval(countdownRef.current);
        };
    }, [fetchData]);

    // Poll IoT Simulator live-locations every 3 seconds for this vehicle's real-time position
    useEffect(() => {
        const pollLive = async () => {
            try {
                const res = await fetch('http://localhost:8082/api/iot/live-locations');
                if (res.ok) {
                    const all = await res.json();
                    const mine = all.find(v => Number(v.vehicleId) === Number(vehicleId));
                    setLiveIotData(mine || null);
                    if (mine) setSimulating(true);
                } else {
                    setLiveIotData(null);
                }
            } catch {
                setLiveIotData(null);
            }
        };
        pollLive();
        const id = setInterval(pollLive, POLL_INTERVAL_MS);
        return () => clearInterval(id);
    }, [vehicleId]);

    // Check if this vehicle is actively broadcasting via standalone IoT Simulator
    useEffect(() => {
        const checkSim = async () => {
            try {
                const res = await fetch(SIM_STATUS_URL);
                if (res.ok) {
                    const data = await res.json();
                    const activeIds = data.activeVehicleIds || [];
                    setSimulating(activeIds.includes(vehicleId));
                } else {
                    setSimulating(false);
                }
            } catch {
                setSimulating(false);
            }
        };
        checkSim();
    }, [vehicleId, lastFetchedAt]);

    // Merge: if IoT Simulator has fresher live data, overlay it on top of persisted location
    const displayLocation = liveIotData ? {
        ...location,
        latitude: liveIotData.latitude,
        longitude: liveIotData.longitude,
        speedKmH: liveIotData.speedKmH,
        status: liveIotData.status,
        routeName: liveIotData.routeName,
        timestamp: liveIotData.timestamp,
        isOnHighway: location?.isOnHighway ?? false,
        distanceFromPrevious: location?.distanceFromPrevious ?? null,
    } : location;


    const mapUrl = displayLocation
        ? `https://maps.google.com/maps?q=${displayLocation.latitude},${displayLocation.longitude}&z=14&output=embed`
        : null;

    const gmapsLink = displayLocation
        ? `https://www.google.com/maps?q=${displayLocation.latitude},${displayLocation.longitude}`
        : null;

    return (
        <div className="vtm-overlay" onClick={e => e.target === e.currentTarget && onClose()}>
            <div className="vtm-modal">
                {/* ── Header ── */}
                <div className="vtm-header">
                    <div className="vtm-header-left">
                        <span className="vtm-vehicle-icon">📡</span>
                        <div>
                            <div className="vtm-title">Live Tracker</div>
                            <div className="vtm-subtitle">{vehicle?.vehicleNumber} · {vehicle?.vehicleType}</div>
                        </div>
                    </div>
                    <div className="vtm-header-right">
                        <FreshnessBadge timestamp={displayLocation?.timestamp} />
                        <div className="vtm-poll-info">
                            🔄 Next refresh in <strong>{countdown}s</strong>
                        </div>
                        <button className="vtm-refresh-btn" onClick={fetchData} title="Refresh Now">⟳</button>
                        <button className="vtm-close-btn" onClick={onClose}>✕</button>
                    </div>
                </div>

                {/* ── Body ── */}
                <div className="vtm-body">
                    {loading && (
                        <div className="vtm-loading">
                            <div className="vtm-spinner" />
                            <span>Fetching vehicle data…</span>
                        </div>
                    )}
                    {error && !loading && (
                        <div className="vtm-error">
                            ⚠️ {error}
                            <button onClick={fetchData} className="vtm-retry">Retry</button>
                        </div>
                    )}
                    {!loading && !error && !displayLocation && (
                        <div className="vtm-no-data">
                            <div className="vtm-no-data-icon">📭</div>
                            <p>No GPS data found for this vehicle yet.</p>
                            <p className="vtm-hint">Start the IoT Simulator and begin a simulation to receive live data.</p>
                        </div>
                    )}
                    {!loading && displayLocation && (
                        <>
                            {/* ── Simulation badge ── */}
                            {simulating && (
                                <div className="vtm-sim-badge">
                                    ⚡ Simulation Active — IoT data is being streamed for this vehicle
                                </div>
                            )}

                            {/* ── Stats Row ── */}
                            <div className="vtm-stats-row">
                                {/* Speed Gauge */}
                                <div className="vtm-stat-card vtm-gauge-card">
                                    <div className="vtm-stat-label">Speed</div>
                                    <SpeedGauge speed={displayLocation.speedKmH} />
                                </div>

                                {/* Status */}
                                <div className="vtm-stat-card">
                                    <div className="vtm-stat-label">Status</div>
                                    <div
                                        className="vtm-status-pill"
                                        style={{
                                            background: getStatusColor(displayLocation.status) + '22',
                                            color: getStatusColor(displayLocation.status),
                                            borderColor: getStatusColor(displayLocation.status) + '55',
                                        }}
                                    >
                                        <span className="vtm-status-dot" style={{ background: getStatusColor(displayLocation.status) }} />
                                        {displayLocation.status || 'Unknown'}
                                    </div>

                                    <div className="vtm-stat-label" style={{ marginTop: '12px' }}>On Highway</div>
                                    <div className={`vtm-highway-badge ${displayLocation.isOnHighway ? 'vtm-on-hw' : 'vtm-off-hw'}`}>
                                        {displayLocation.isOnHighway ? '🛣️ Yes' : '🏙️ Off-Highway'}
                                    </div>
                                </div>

                                {/* Route & Distance */}
                                <div className="vtm-stat-card">
                                    <div className="vtm-stat-label">Current Route</div>
                                    <div className="vtm-route-name">{displayLocation.routeName || '—'}</div>

                                    <div className="vtm-stat-label" style={{ marginTop: '12px' }}>Step Distance</div>
                                    <div className="vtm-route-name">
                                        {displayLocation.distanceFromPrevious != null
                                            ? `${displayLocation.distanceFromPrevious.toFixed(3)} km`
                                            : '—'}
                                    </div>
                                </div>

                                {/* Coordinates & Time */}
                                <div className="vtm-stat-card">
                                    <div className="vtm-stat-label">Coordinates</div>
                                    <div className="vtm-coords">
                                        <span>📍 {displayLocation.latitude?.toFixed(5)}, {displayLocation.longitude?.toFixed(5)}</span>
                                    </div>
                                    <div className="vtm-stat-label" style={{ marginTop: '12px' }}>Last Update</div>
                                    <div className="vtm-timestamp">
                                        {displayLocation.timestamp
                                            ? new Date(displayLocation.timestamp).toLocaleTimeString('en-IN')
                                            : '—'}
                                    </div>
                                </div>
                            </div>

                            {/* ── Usage Summary ── */}
                            {usageSummary && (
                                <div className="vtm-usage-section">
                                    <div className="vtm-section-title">📊 Highway Usage Summary</div>
                                    <div className="vtm-usage-row">
                                        <div className="vtm-usage-card">
                                            <div className="vtm-usage-val">{usageSummary.totalDistance?.toFixed(2) ?? '0'} km</div>
                                            <div className="vtm-usage-key">Total Distance</div>
                                        </div>
                                        <div className="vtm-usage-card">
                                            <div className="vtm-usage-val">{usageSummary.totalSessions ?? '0'}</div>
                                            <div className="vtm-usage-key">Total Sessions</div>
                                        </div>
                                        {usageSummary.distanceByHighway && Object.entries(usageSummary.distanceByHighway).map(([hw, dist]) => (
                                            <div key={hw} className="vtm-usage-card vtm-usage-hw">
                                                <div className="vtm-usage-val">{Number(dist).toFixed(2)} km</div>
                                                <div className="vtm-usage-key">🛣️ {hw}</div>
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            )}

                            {/* ── Map ── */}
                            <div className="vtm-map-section">
                                <div className="vtm-section-title">
                                    🗺️ Live Map
                                    <a href={gmapsLink} target="_blank" rel="noopener noreferrer" className="vtm-open-maps">
                                        ↗ Open in Google Maps
                                    </a>
                                </div>
                                <div className="vtm-map-frame">
                                    <iframe
                                        title="vehicle-location-map"
                                        src={mapUrl}
                                        allowFullScreen
                                        loading="lazy"
                                    />
                                </div>
                            </div>

                            {/* ── History Table ── */}
                            {history.length > 0 && (
                                <div className="vtm-history-section">
                                    <div className="vtm-section-title">🕒 Recent Location History (last {history.length})</div>
                                    <div className="vtm-history-scroll">
                                        <table className="vtm-history-table">
                                            <thead>
                                                <tr>
                                                    <th>#</th>
                                                    <th>Time</th>
                                                    <th>Latitude</th>
                                                    <th>Longitude</th>
                                                    <th>Speed</th>
                                                    <th>Status</th>
                                                    <th>Highway</th>
                                                    <th>Route</th>
                                                    <th>Step Dist.</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                {history.map((h, idx) => (
                                                    <tr key={h.id} className={idx === 0 ? 'vtm-latest-row' : ''}>
                                                        <td className="vtm-row-idx">{idx === 0 ? '🔴' : idx + 1}</td>
                                                        <td className="vtm-hist-time">
                                                            {new Date(h.timestamp).toLocaleTimeString('en-IN')}
                                                        </td>
                                                        <td>{h.latitude?.toFixed(5)}</td>
                                                        <td>{h.longitude?.toFixed(5)}</td>
                                                        <td>
                                                            {h.speedKmH != null
                                                                ? <span style={{ color: getSpeedColor(h.speedKmH), fontWeight: '700' }}>
                                                                    {Math.round(h.speedKmH)} km/h
                                                                </span>
                                                                : '—'}
                                                        </td>
                                                        <td>
                                                            <span
                                                                className="vtm-hist-status"
                                                                style={{ color: getStatusColor(h.status) }}
                                                            >{h.status || '—'}</span>
                                                        </td>
                                                        <td>
                                                            {h.isOnHighway
                                                                ? <span className="vtm-hist-hw-yes">🛣️ Yes</span>
                                                                : <span className="vtm-hist-hw-no">No</span>}
                                                        </td>
                                                        <td>{h.routeName || '—'}</td>
                                                        <td>{h.distanceFromPrevious != null ? `${h.distanceFromPrevious.toFixed(3)} km` : '—'}</td>
                                                    </tr>
                                                ))}
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            )}
                        </>
                    )}
                </div>
            </div>
        </div>
    );
}

export default VehicleTrackingModal;
