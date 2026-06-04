import React, { useEffect, useState } from 'react';
import { getRequest } from '../../services/api';

function AdminVehicleTrackingModal({ vehicle, onClose }) {
    const [tracking, setTracking] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        if (!vehicle?.vehicleId) return;

        const fetchTracking = async () => {
            setLoading(true);
            setError('');
            setTracking(null);
            try {
                const data = await getRequest(`/locations/vehicle/${vehicle.vehicleId}/latest`);
                setTracking(data);
            } catch (err) {
                setError(err?.response?.status === 404
                    ? 'No tracking data is available for this vehicle yet.'
                    : 'Failed to load latest tracking data.');
            } finally {
                setLoading(false);
            }
        };

        fetchTracking();
    }, [vehicle]);

    if (!vehicle) return null;

    const formatValue = (value, fallback = 'N/A') => (
        value === null || value === undefined || value === '' ? fallback : value
    );
    const formatCoordinate = (value) => (
        typeof value === 'number' ? value.toFixed(6) : formatValue(value)
    );
    const formatTimestamp = (value) => {
        if (!value) return 'N/A';
        const date = new Date(value);
        return Number.isNaN(date.getTime()) ? value : date.toLocaleString();
    };
    const highwayStatus = tracking
        ? tracking.isOnHighway
            ? `On Highway${tracking.highwayId ? ` #${tracking.highwayId}` : ''}`
            : 'Off Highway'
        : 'N/A';
    const hasCoordinates = tracking
        && typeof tracking.latitude === 'number'
        && typeof tracking.longitude === 'number';
    const mapEmbedUrl = hasCoordinates
        ? `https://www.openstreetmap.org/export/embed.html?bbox=${tracking.longitude - 0.01}%2C${tracking.latitude - 0.01}%2C${tracking.longitude + 0.01}%2C${tracking.latitude + 0.01}&layer=mapnik&marker=${tracking.latitude}%2C${tracking.longitude}`
        : '';
    const mapLinkUrl = hasCoordinates
        ? `https://www.openstreetmap.org/?mlat=${tracking.latitude}&mlon=${tracking.longitude}#map=15/${tracking.latitude}/${tracking.longitude}`
        : '';

    return (
        <div style={{
            position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
            backgroundColor: 'rgba(0,0,0,0.6)', zIndex: 1000,
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            padding: '20px'
        }}>
            <div className="card" style={{ width: '560px', maxWidth: '100%', margin: 0, maxHeight: '90vh', overflowY: 'auto' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', gap: '16px', alignItems: 'flex-start', marginBottom: '18px' }}>
                    <div>
                        <h3 style={{ margin: 0 }}>Track Vehicle</h3>
                        <p style={{ margin: '6px 0 0', color: '#7f8c8d' }}>
                            {vehicle.vehicleNumber} - Vehicle ID {vehicle.vehicleId}
                        </p>
                    </div>
                    <button type="button" className="btn btn-secondary" style={{ padding: '8px 12px' }} onClick={onClose}>
                        Close
                    </button>
                </div>

                {loading && <p className="info-text">Loading latest tracking data...</p>}

                {!loading && error && (
                    <div className="error-message">
                        <p>{error}</p>
                    </div>
                )}

                {!loading && tracking && (
                    <>
                        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '12px' }}>
                            <div className="tracking-field">
                                <span>Latitude</span>
                                <strong>{formatCoordinate(tracking.latitude)}</strong>
                            </div>
                            <div className="tracking-field">
                                <span>Longitude</span>
                                <strong>{formatCoordinate(tracking.longitude)}</strong>
                            </div>
                            <div className="tracking-field">
                                <span>Speed</span>
                                <strong>{tracking.speedKmH !== null && tracking.speedKmH !== undefined ? `${tracking.speedKmH} km/h` : 'N/A'}</strong>
                            </div>
                            <div className="tracking-field">
                                <span>Status</span>
                                <strong>{formatValue(tracking.status)}</strong>
                            </div>
                            <div className="tracking-field" style={{ gridColumn: '1 / -1' }}>
                                <span>Route</span>
                                <strong>{formatValue(tracking.routeName)}</strong>
                            </div>
                            <div className="tracking-field">
                                <span>Highway Status</span>
                                <strong>{highwayStatus}</strong>
                            </div>
                            <div className="tracking-field">
                                <span>Last Updated</span>
                                <strong>{formatTimestamp(tracking.timestamp)}</strong>
                            </div>
                        </div>

                        {hasCoordinates && (
                            <div className="tracking-map">
                                <iframe
                                    title={`Vehicle ${vehicle.vehicleId} location map`}
                                    src={mapEmbedUrl}
                                    loading="lazy"
                                    referrerPolicy="no-referrer-when-downgrade"
                                />
                                <a href={mapLinkUrl} target="_blank" rel="noreferrer" className="tracking-map-link">
                                    Open exact location in OpenStreetMap
                                </a>
                            </div>
                        )}
                    </>
                )}
            </div>
        </div>
    );
}

export default AdminVehicleTrackingModal;
