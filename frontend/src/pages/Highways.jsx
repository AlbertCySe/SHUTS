import { useState, useEffect } from 'react';
import { getRequest, postRequest } from '../services/api';

function Highways() {
    const [highways, setHighways] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Form state
    const [formData, setFormData] = useState({
        highwayName: '',
        startLatitude: '',
        startLongitude: '',
        endLatitude: '',
        endLongitude: '',
        ratePerKmForCar: '',
        ratePerKmForBike: '',
        ratePerKmForTruck: ''
    });
    const [formLoading, setFormLoading] = useState(false);
    const [successMessage, setSuccessMessage] = useState('');
    const [formError, setFormError] = useState('');

    // Fetch highways when component mounts
    useEffect(() => {
        fetchHighways();
    }, []);

    const fetchHighways = async () => {
        try {
            setLoading(true);
            setError(null);
            const data = await getRequest('/highways');
            setHighways(data);
        } catch (err) {
            setError('Failed to fetch highways. Make sure the backend is running.');
            console.error('Error fetching highways:', err);
        } finally {
            setLoading(false);
        }
    };

    // Handle form input changes
    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({
            ...formData,
            [name]: value
        });
    };

    // Handle form submission
    const handleSubmit = async (e) => {
        e.preventDefault();

        // Clear previous messages
        setSuccessMessage('');
        setFormError('');

        // Basic validation
        const requiredFields = Object.keys(formData);
        const isValid = requiredFields.every(field => formData[field] !== '');

        if (!isValid) {
            setFormError('All fields are required');
            return;
        }

        try {
            setFormLoading(true);

            // Convert numeric fields to numbers
            const highwayData = {
                highwayName: formData.highwayName,
                startLatitude: parseFloat(formData.startLatitude),
                startLongitude: parseFloat(formData.startLongitude),
                endLatitude: parseFloat(formData.endLatitude),
                endLongitude: parseFloat(formData.endLongitude),
                ratePerKmForCar: parseFloat(formData.ratePerKmForCar),
                ratePerKmForBike: parseFloat(formData.ratePerKmForBike),
                ratePerKmForTruck: parseFloat(formData.ratePerKmForTruck)
            };

            // Send POST request to create highway
            const newHighway = await postRequest('/highways', highwayData);

            // Show success message
            setSuccessMessage(`Highway "${newHighway.highwayName}" created successfully!`);

            // Clear form
            setFormData({
                highwayName: '',
                startLatitude: '',
                startLongitude: '',
                endLatitude: '',
                endLongitude: '',
                ratePerKmForCar: '',
                ratePerKmForBike: '',
                ratePerKmForTruck: ''
            });

            // Refresh highway list
            fetchHighways();

            // Clear success message after 3 seconds
            setTimeout(() => {
                setSuccessMessage('');
            }, 3000);

        } catch (err) {
            setFormError('Failed to create highway. Please check your input and try again.');
            console.error('Error creating highway:', err);
        } finally {
            setFormLoading(false);
        }
    };

    return (
        <div className="page">
            <h2>Highway Management</h2>

            {/* Create Highway Form */}
            <div className="card">
                <h3>Add New Highway</h3>

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
                        <label>Highway Name: *</label>
                        <input
                            type="text"
                            name="highwayName"
                            value={formData.highwayName}
                            onChange={handleInputChange}
                            placeholder="e.g., NH-44"
                            disabled={formLoading}
                        />
                    </div>

                    <h4 style={{ marginTop: '15px', color: '#34495e' }}>Start Coordinates</h4>
                    <div className="form-row">
                        <div className="form-group">
                            <label>Start Latitude: *</label>
                            <input
                                type="number"
                                step="0.0001"
                                name="startLatitude"
                                value={formData.startLatitude}
                                onChange={handleInputChange}
                                placeholder="e.g., 28.7041"
                                disabled={formLoading}
                            />
                        </div>

                        <div className="form-group">
                            <label>Start Longitude: *</label>
                            <input
                                type="number"
                                step="0.0001"
                                name="startLongitude"
                                value={formData.startLongitude}
                                onChange={handleInputChange}
                                placeholder="e.g., 77.1025"
                                disabled={formLoading}
                            />
                        </div>
                    </div>

                    <h4 style={{ marginTop: '15px', color: '#34495e' }}>End Coordinates</h4>
                    <div className="form-row">
                        <div className="form-group">
                            <label>End Latitude: *</label>
                            <input
                                type="number"
                                step="0.0001"
                                name="endLatitude"
                                value={formData.endLatitude}
                                onChange={handleInputChange}
                                placeholder="e.g., 19.0760"
                                disabled={formLoading}
                            />
                        </div>

                        <div className="form-group">
                            <label>End Longitude: *</label>
                            <input
                                type="number"
                                step="0.0001"
                                name="endLongitude"
                                value={formData.endLongitude}
                                onChange={handleInputChange}
                                placeholder="e.g., 72.8777"
                                disabled={formLoading}
                            />
                        </div>
                    </div>

                    <h4 style={{ marginTop: '15px', color: '#34495e' }}>Toll Rates (₹ per km)</h4>
                    <div className="form-row">
                        <div className="form-group">
                            <label>Car Rate: *</label>
                            <input
                                type="number"
                                step="0.1"
                                name="ratePerKmForCar"
                                value={formData.ratePerKmForCar}
                                onChange={handleInputChange}
                                placeholder="e.g., 2.5"
                                disabled={formLoading}
                            />
                        </div>

                        <div className="form-group">
                            <label>Bike Rate: *</label>
                            <input
                                type="number"
                                step="0.1"
                                name="ratePerKmForBike"
                                value={formData.ratePerKmForBike}
                                onChange={handleInputChange}
                                placeholder="e.g., 1.0"
                                disabled={formLoading}
                            />
                        </div>

                        <div className="form-group">
                            <label>Truck Rate: *</label>
                            <input
                                type="number"
                                step="0.1"
                                name="ratePerKmForTruck"
                                value={formData.ratePerKmForTruck}
                                onChange={handleInputChange}
                                placeholder="e.g., 5.0"
                                disabled={formLoading}
                            />
                        </div>
                    </div>

                    <button
                        type="submit"
                        className="btn btn-primary"
                        disabled={formLoading}
                    >
                        {formLoading ? 'Creating...' : 'Add Highway'}
                    </button>
                </form>
            </div>

            {/* Highways List */}
            <div className="card">
                <h3>All Highways</h3>

                {/* Loading State */}
                {loading && (
                    <p className="info-text">Loading highways...</p>
                )}

                {/* Error State */}
                {error && (
                    <div className="error-message">
                        <p>{error}</p>
                        <button onClick={fetchHighways} className="btn btn-primary">
                            Retry
                        </button>
                    </div>
                )}

                {/* Highways Table */}
                {!loading && !error && highways.length > 0 && (
                    <div className="table-container">
                        <table className="table table-compact">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Name</th>
                                    <th>Start Lat</th>
                                    <th>Start Lon</th>
                                    <th>End Lat</th>
                                    <th>End Lon</th>
                                    <th>Car (₹/km)</th>
                                    <th>Bike (₹/km)</th>
                                    <th>Truck (₹/km)</th>
                                </tr>
                            </thead>
                            <tbody>
                                {highways.map((highway) => (
                                    <tr key={highway.highwayId}>
                                        <td>{highway.highwayId}</td>
                                        <td><strong>{highway.highwayName}</strong></td>
                                        <td>{highway.startLatitude.toFixed(4)}</td>
                                        <td>{highway.startLongitude.toFixed(4)}</td>
                                        <td>{highway.endLatitude.toFixed(4)}</td>
                                        <td>{highway.endLongitude.toFixed(4)}</td>
                                        <td>₹{highway.ratePerKmForCar.toFixed(2)}</td>
                                        <td>₹{highway.ratePerKmForBike.toFixed(2)}</td>
                                        <td>₹{highway.ratePerKmForTruck.toFixed(2)}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}

                {/* No Highways Found */}
                {!loading && !error && highways.length === 0 && (
                    <p className="info-text">No highways found. Add your first highway!</p>
                )}
            </div>
        </div>
    );
}

export default Highways;
