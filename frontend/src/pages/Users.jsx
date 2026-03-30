import { useState } from 'react';
import { postRequest } from '../services/api';

function Users() {
    // Form state
    const [formData, setFormData] = useState({
        name: '',
        email: '',
        phoneNumber: ''
    });
    const [formLoading, setFormLoading] = useState(false);
    const [successMessage, setSuccessMessage] = useState('');
    const [formError, setFormError] = useState('');

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
        if (!formData.name || !formData.email || !formData.phoneNumber) {
            setFormError('All fields are required');
            return;
        }

        try {
            setFormLoading(true);

            // Send POST request to create user
            const newUser = await postRequest('/users', formData);

            // Show success message
            setSuccessMessage(`User "${newUser.name}" registered successfully! Check your email for login details.`);

            // Clear form
            setFormData({
                name: '',
                email: '',
                phoneNumber: ''
            });

            // Clear success message after 5 seconds
            setTimeout(() => {
                setSuccessMessage('');
            }, 5000);

        } catch (err) {
            setFormError('Failed to register. Please try again.');
            console.error('Error creating user:', err);
        } finally {
            setFormLoading(false);
        }
    };

    return (
        <div className="page">
            <h2>User Registration</h2>

            {/* Create User Form */}
            <div className="card">
                <h3>Register New User</h3>

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
                        <label>Name: *</label>
                        <input
                            type="text"
                            name="name"
                            value={formData.name}
                            onChange={handleInputChange}
                            placeholder="Enter full name"
                            disabled={formLoading}
                        />
                    </div>

                    <div className="form-group">
                        <label>Email: *</label>
                        <input
                            type="email"
                            name="email"
                            value={formData.email}
                            onChange={handleInputChange}
                            placeholder="Enter email address"
                            disabled={formLoading}
                        />
                    </div>

                    <div className="form-group">
                        <label>Phone Number: *</label>
                        <input
                            type="tel"
                            name="phoneNumber"
                            value={formData.phoneNumber}
                            onChange={handleInputChange}
                            placeholder="Enter phone number"
                            disabled={formLoading}
                        />
                    </div>

                    <button
                        type="submit"
                        className="btn btn-primary"
                        disabled={formLoading}
                    >
                        {formLoading ? 'Registering...' : 'Register User'}
                    </button>
                </form>
            </div>
        </div>
    );
}

export default Users;

