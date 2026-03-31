import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { postRequest } from '../services/api';
import './LoginStyles.css'; // Inheriting login form layout

function Register() {
    const navigate = useNavigate();
    
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
            setSuccessMessage(`Welcome ${newUser.name}! Check your email for registration details.`);

            // Redirect to login after 3 seconds
            setTimeout(() => {
                navigate('/login');
            }, 3000);

        } catch (err) {
            setFormError('Failed to register. Please try again or use a different email/phone.');
            console.error('Error creating user:', err);
        } finally {
            setFormLoading(false);
        }
    };

    return (
        <div className="page login-page">
            <div className="login-container">
                <div className="login-card">
                    <h2 className="login-title">👤 Create Account</h2>

                    {/* Success Message */}
                    {successMessage && (
                        <div className="success-message" style={{ marginBottom: '15px' }}>
                            <p>✅ {successMessage}</p>
                        </div>
                    )}

                    {/* Form Error Message */}
                    {formError && (
                        <div className="error-message login-error">
                            <p>⚠️ {formError}</p>
                        </div>
                    )}

                    <form className="login-form" onSubmit={handleSubmit}>
                        <div className="form-group">
                            <label>Full Name *</label>
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
                            <label>Email Address *</label>
                            <input
                                type="email"
                                name="email"
                                value={formData.email}
                                onChange={handleInputChange}
                                placeholder="Enter your email"
                                disabled={formLoading}
                            />
                        </div>

                        <div className="form-group">
                            <label>Phone Number *</label>
                            <input
                                type="tel"
                                name="phoneNumber"
                                value={formData.phoneNumber}
                                onChange={handleInputChange}
                                placeholder="Enter your mobile number"
                                disabled={formLoading}
                            />
                        </div>

                        <button
                            type="submit"
                            className="btn btn-primary btn-login"
                            disabled={formLoading}
                            style={{ marginTop: '10px' }}
                        >
                            {formLoading ? 'Registering...' : 'Complete Registration →'}
                        </button>
                    </form>

                    <div className="login-footer">
                        <p>Already have an account?</p>
                        <button
                            className="btn-link"
                            onClick={() => navigate('/login')}
                            type="button"
                        >
                            Go to Auth Portal
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default Register;
