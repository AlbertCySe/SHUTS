import { useState, useEffect } from 'react';
import { getRequest, postRequest } from '../services/api';

function Vehicles() {
    const [vehicles, setVehicles] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Form state
    const [formData, setFormData] = useState({
        vehicleNumber: '',
        vehicleType: '',
        userId: ''
    });
    const [formLoading, setFormLoading] = useState(false);
    const [successMessage, setSuccessMessage] = useState('');
    const [formError, setFormError] = useState('');

    // Fetch vehicles when component mounts
    useEffect(() => {
        fetchVehicles();
    }, []);

    const fetchVehicles = async () => {
        try {
            setLoading(true);
            setError(null);
            const data = await getRequest('/vehicles');
            setVehicles(data);
        } catch (err) {
            setError('Failed to fetch vehicles. Make sure the backend is running.');
            console.error('Error fetching vehicles:', err);
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
        if (!formData.vehicleNumber || !formData.vehicleType || !formData.userId) {
            setFormError('All fields are required');
            return;
        }

        try {
            setFormLoading(true);

            // Prepare data for API (excluding userId from body)
            const vehicleData = {
                vehicleNumber: formData.vehicleNumber,
                vehicleType: formData.vehicleType
            };

            // Send POST request to create vehicle
            // Endpoint: /api/users/{userId}/vehicles
            const newVehicle = await postRequest(`/users/${formData.userId}/vehicles`, vehicleData);

            // Show success message
            setSuccessMessage(`Vehicle "${newVehicle.vehicleNumber}" registered successfully!`);

            // Clear form
            setFormData({
                vehicleNumber: '',
                vehicleType: '',
                userId: ''
            });

            // Refresh vehicle list
            fetchVehicles();

            // Clear success message after 3 seconds
            setTimeout(() => {
                setSuccessMessage('');
            }, 3000);

        } catch (err) {
            setFormError('Failed to register vehicle. Please check the User ID and try again.');
            console.error('Error creating vehicle:', err);
        } finally {
            setFormLoading(false);
        }
    };

    return (
        <div className="page">
            <h2>Vehicle Management</h2>

            {/* Register Vehicle Form */}
            <div className="card">
                <h3>Register New Vehicle</h3>

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
                        <label>Vehicle Number: *</label>
                        <input
                            type="text"
                            name="vehicleNumber"
                            value={formData.vehicleNumber}
                            onChange={handleInputChange}
                            placeholder="e.g., MH01AB1234"
                            disabled={formLoading}
                        />
                    </div>

                    <div className="form-group">
                        <label>Vehicle Type: *</label>
                        <select
                            name="vehicleType"
                            value={formData.vehicleType}
                            onChange={handleInputChange}
                            disabled={formLoading}
                        >
                            <option value="">Select vehicle type</option>
                            <option value="CAR">Car</option>
                            <option value="BIKE">Bike</option>
                            <option value="BUS">Bus</option>
                            <option value="TRUCK">Truck</option>
                        </select>
                    </div>

                    <div className="form-group">
                        <label>User ID: *</label>
                        <input
                            type="number"
                            name="userId"
                            value={formData.userId}
                            onChange={handleInputChange}
                            placeholder="Enter user ID"
                            disabled={formLoading}
                        />
                    </div>

                    <button
                        type="submit"
                        className="btn btn-primary"
                        disabled={formLoading}
                    >
                        {formLoading ? 'Registering...' : 'Register Vehicle'}
                    </button>
                </form>
            </div>

            {/* Vehicles List */}
            <div className="card">
                <h3>All Vehicles</h3>

                {/* Loading State */}
                {loading && (
                    <p className="info-text">Loading vehicles...</p>
                )}

                {/* Error State */}
                {error && (
                    <div className="error-message">
                        <p>{error}</p>
                        <button onClick={fetchVehicles} className="btn btn-primary">
                            Retry
                        </button>
                    </div>
                )}

                {/* Vehicles Table */}
                {!loading && !error && vehicles.length > 0 && (
                    <div className="table-container">
                        <table className="table">
                            <thead>
                                <tr>
                                    <th>Vehicle ID</th>
                                    <th>Vehicle Number</th>
                                    <th>Vehicle Type</th>
                                    <th>User ID</th>
                                </tr>
                            </thead>
                            <tbody>
                                {vehicles.map((vehicle) => (
                                    <tr key={vehicle.vehicleId}>
                                        <td>{vehicle.vehicleId}</td>
                                        <td>{vehicle.vehicleNumber}</td>
                                        <td>
                                            <span className="badge badge-type">{vehicle.vehicleType}</span>
                                        </td>
                                        <td>{vehicle.user?.userId || 'N/A'}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}

                {/* No Vehicles Found */}
                {!loading && !error && vehicles.length === 0 && (
                    <p className="info-text">No vehicles found. Register your first vehicle!</p>
                )}
            </div>
        </div>
    );
}

export default Vehicles;
