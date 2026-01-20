import { useState, useEffect } from 'react';
import { getRequest, postRequest } from '../services/api';

function Users() {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Form state
    const [formData, setFormData] = useState({
        name: '',
        email: '',
        phoneNumber: ''
    });
    const [formLoading, setFormLoading] = useState(false);
    const [successMessage, setSuccessMessage] = useState('');
    const [formError, setFormError] = useState('');

    // Fetch users when component mounts
    useEffect(() => {
        fetchUsers();
    }, []);

    const fetchUsers = async () => {
        try {
            setLoading(true);
            setError(null);
            const data = await getRequest('/users');
            setUsers(data);
        } catch (err) {
            setError('Failed to fetch users. Make sure the backend is running.');
            console.error('Error fetching users:', err);
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
        if (!formData.name || !formData.email || !formData.phoneNumber) {
            setFormError('All fields are required');
            return;
        }

        try {
            setFormLoading(true);

            // Send POST request to create user
            const newUser = await postRequest('/users', formData);

            // Show success message
            setSuccessMessage(`User "${newUser.name}" created successfully!`);

            // Clear form
            setFormData({
                name: '',
                email: '',
                phoneNumber: ''
            });

            // Refresh user list
            fetchUsers();

            // Clear success message after 3 seconds
            setTimeout(() => {
                setSuccessMessage('');
            }, 3000);

        } catch (err) {
            setFormError('Failed to create user. Please try again.');
            console.error('Error creating user:', err);
        } finally {
            setFormLoading(false);
        }
    };

    return (
        <div className="page">
            <h2>User Management</h2>

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
                        {formLoading ? 'Creating...' : 'Register User'}
                    </button>
                </form>
            </div>

            {/* Users List */}
            <div className="card">
                <h3>All Users</h3>

                {/* Loading State */}
                {loading && (
                    <p className="info-text">Loading users...</p>
                )}

                {/* Error State */}
                {error && (
                    <div className="error-message">
                        <p>{error}</p>
                        <button onClick={fetchUsers} className="btn btn-primary">
                            Retry
                        </button>
                    </div>
                )}

                {/* Users Table */}
                {!loading && !error && users.length > 0 && (
                    <div className="table-container">
                        <table className="table">
                            <thead>
                                <tr>
                                    <th>User ID</th>
                                    <th>Name</th>
                                    <th>Email</th>
                                    <th>Phone Number</th>
                                </tr>
                            </thead>
                            <tbody>
                                {users.map((user) => (
                                    <tr key={user.userId}>
                                        <td>{user.userId}</td>
                                        <td>{user.name}</td>
                                        <td>{user.email}</td>
                                        <td>{user.phoneNumber}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}

                {/* No Users Found */}
                {!loading && !error && users.length === 0 && (
                    <p className="info-text">No users found. Create your first user!</p>
                )}
            </div>
        </div>
    );
}

export default Users;
