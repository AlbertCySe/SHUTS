import React, { useState } from 'react';
import { postRequest } from '../../services/api';

function VehicleRegistrationForm({ userId, onVehicleAdded, userRole }) {
    const [formData, setFormData] = useState({
        vehicleNumber: '',
        vehicleType: '',
        userId: userId || ''
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value.toUpperCase() }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        if (!formData.vehicleNumber || !formData.vehicleType) {
            setError('Please fill in all required fields.');
            return;
        }

        const sid = userRole === 'admin' ? formData.userId : userId;
        if (!sid) {
            setError('User ID is missing. Please log in again.');
            return;
        }

        try {
            setLoading(true);
            const vehicleData = {
                vehicleNumber: formData.vehicleNumber,
                vehicleType: formData.vehicleType
            };
            
            const newVehicle = await postRequest(`/users/${sid}/vehicles`, vehicleData);
            setSuccess(`Vehicle "${newVehicle.vehicleNumber}" registered successfully!`);
            setFormData({ vehicleNumber: '', vehicleType: '', userId: userId || '' });
            
            if (onVehicleAdded) onVehicleAdded();
            
            setTimeout(() => setSuccess(''), 3000);
        } catch (err) {
            setError('Failed to register vehicle. Verify the User ID and try again.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="card">
            <h3>Register New Vehicle</h3>
            {success && <div className="success-message"><p>{success}</p></div>}
            {error && <div className="error-message"><p>{error}</p></div>}

            <form className="form" onSubmit={handleSubmit}>
                <div className="form-group">
                    <label>Vehicle Number: *</label>
                    <input
                        type="text"
                        name="vehicleNumber"
                        value={formData.vehicleNumber}
                        onChange={handleInputChange}
                        placeholder="e.g., MH01AB1234"
                        disabled={loading}
                        required
                    />
                </div>

                <div className="form-group">
                    <label>Vehicle Type: *</label>
                    <select
                        name="vehicleType"
                        value={formData.vehicleType}
                        onChange={handleInputChange}
                        disabled={loading}
                        required
                    >
                        <option value="">Select vehicle type</option>
                        <option value="CAR">Car</option>
                        <option value="BIKE">Bike</option>
                        <option value="BUS">Bus</option>
                        <option value="TRUCK">Truck</option>
                        <option value="COMMERCIAL">Commercial</option>
                    </select>
                </div>

                {userRole === 'admin' ? (
                     <div className="form-group">
                        <label>User ID: *</label>
                        <input
                            type="number"
                            name="userId"
                            value={formData.userId}
                            onChange={(e) => setFormData({...formData, userId: e.target.value})}
                            placeholder="Enter user ID"
                            disabled={loading}
                            required
                        />
                    </div>
                ) : (
                    <input type="hidden" name="userId" value={userId} />
                )}

                <button type="submit" className="btn btn-primary" disabled={loading}>
                    {loading ? 'Registering...' : 'Register Vehicle'}
                </button>
            </form>
        </div>
    );
}

export default VehicleRegistrationForm;
