import { useState } from 'react';
import { getRequest, postRequest } from '../services/api';

function LocationTracking() {
    // Form state for submitting GPS location
    const [formData, setFormData] = useState({
        vehicleId: '',
        latitude: '',
        longitude: ''
    });
    const [formLoading, setFormLoading] = useState(false);
    const [successMessage, setSuccessMessage] = useState('');
    const [formError, setFormError] = useState('');

    // State for viewing location history
    const [searchVehicleId, setSearchVehicleId] = useState('');
    const [locations, setLocations] = useState([]);
    const [historyLoading, setHistoryLoading] = useState(false);
    const [historyError, setHistoryError] = useState(null);

    // Handle form input changes
    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({
            ...formData,
            [name]: value
        });
    };

    // Handle form submission to save GPS location
    const handleSubmit = async (e) => {
        e.preventDefault();

        // Clear previous messages
        setSuccessMessage('');
        setFormError('');

        // Basic validation
        if (!formData.vehicleId || !formData.latitude || !formData.longitude) {
            setFormError('All fields are required');
            return;
        }

        try {
            setFormLoading(true);

            // Prepare data for API
            const locationData = {
                vehicleId: parseInt(formData.vehicleId),
                latitude: parseFloat(formData.latitude),
                longitude: parseFloat(formData.longitude)
            };

            // Send POST request to save location
            await postRequest('/locations', locationData);

            // Show success message
            setSuccessMessage('GPS location saved successfully!');

            // Clear form
            setFormData({
                vehicleId: '',
                latitude: '',
                longitude: ''
            });

            // Clear success message after 3 seconds
            setTimeout(() => {
                setSuccessMessage('');
            }, 3000);

        } catch (err) {
            setFormError('Failed to save GPS location. Please try again.');
            console.error('Error saving location:', err);
        } finally {
            setFormLoading(false);
        }
    };

    // Fetch location history for a vehicle
    const fetchLocationHistory = async () => {
        if (!searchVehicleId) {
            setHistoryError('Please enter a Vehicle ID');
            return;
        }

        try {
            setHistoryLoading(true);
            setHistoryError(null);
            const data = await getRequest(`/locations/vehicle/${searchVehicleId}`);
            setLocations(data);
        } catch (err) {
            setHistoryError('Failed to fetch location history. Vehicle ID may not exist.');
            console.error('Error fetching location history:', err);
            setLocations([]);
        } finally {
            setHistoryLoading(false);
        }
    };

    // Format date and time
    const formatTimestamp = (timestamp) => {
        const date = new Date(timestamp);
        return date.toLocaleString();
    };

    return (
        <div className="page">
            <h2>GPS Location Tracking</h2>
            <p style={{ color: '#666', marginBottom: '20px' }}>
                This page simulates IoT GPS data from vehicles manually.
            </p>

            {/* Save GPS Location Form */}
            <div className="card">
                <h3>Save GPS Location</h3>

                {/* Success Message */}
                {successMessage && (
                    <div className="success-message">
                        <p>{successMessage}</p>
                    </div>
                )}

                {/* Form Error Message */}
                {formError && (
                    <div className="error-message">
                        <p>{formError}</p>
                    </div>
                )}

                <form className="form" onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label>Vehicle ID: *</label>
                        <input
                            type="number"
                            name="vehicleId"
                            value={formData.vehicleId}
                            onChange={handleInputChange}
                            placeholder="Enter vehicle ID"
                            disabled={formLoading}
                        />
                    </div>

                    <div className="form-row">
                        <div className="form-group">
                            <label>Latitude: *</label>
                            <input
                                type="number"
                                step="0.000001"
                                name="latitude"
                                value={formData.latitude}
                                onChange={handleInputChange}
                                placeholder="e.g., 19.076090"
                                disabled={formLoading}
                            />
                        </div>

                        <div className="form-group">
                            <label>Longitude: *</label>
                            <input
                                type="number"
                                step="0.000001"
                                name="longitude"
                                value={formData.longitude}
                                onChange={handleInputChange}
                                placeholder="e.g., 72.877426"
                                disabled={formLoading}
                            />
                        </div>
                    </div>

                    <button
                        type="submit"
                        className="btn btn-primary"
                        disabled={formLoading}
                    >
                        {formLoading ? 'Saving...' : 'Save Location'}
                    </button>
                </form>
            </div>

            {/* View Location History */}
            <div className="card">
                <h3>View Location History</h3>

                <div className="form-row">
                    <div className="form-group">
                        <label>Vehicle ID:</label>
                        <input
                            type="number"
                            value={searchVehicleId}
                            onChange={(e) => setSearchVehicleId(e.target.value)}
                            placeholder="Enter vehicle ID to view history"
                        />
                    </div>
                    <div className="form-group">
                        <label style={{ visibility: 'hidden' }}>Action</label>
                        <button
                            onClick={fetchLocationHistory}
                            className="btn btn-primary"
                            disabled={historyLoading}
                        >
                            {historyLoading ? 'Loading...' : 'View History'}
                        </button>
                    </div>
                </div>

                {/* History Error */}
                {historyError && (
                    <div className="error-message">
                        <p>{historyError}</p>
                    </div>
                )}

                {/* Location History Table */}
                {!historyLoading && !historyError && locations.length > 0 && (
                    <div className="table-container">
                        <p style={{ marginBottom: '10px', color: '#666' }}>
                            Showing {locations.length} location record(s) for Vehicle ID: {searchVehicleId}
                        </p>
                        <table className="table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Timestamp</th>
                                    <th>Latitude</th>
                                    <th>Longitude</th>
                                </tr>
                            </thead>
                            <tbody>
                                {locations.map((location) => (
                                    <tr key={location.id}>
                                        <td>{location.id}</td>
                                        <td>{formatTimestamp(location.timestamp)}</td>
                                        <td>{location.latitude.toFixed(6)}</td>
                                        <td>{location.longitude.toFixed(6)}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}

                {/* No Locations Found */}
                {!historyLoading && !historyError && locations.length === 0 && searchVehicleId && (
                    <p className="info-text">No GPS locations found for this vehicle.</p>
                )}
            </div>
        </div>
    );
}

export default LocationTracking;
